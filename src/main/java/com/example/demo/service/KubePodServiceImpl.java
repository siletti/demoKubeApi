package com.example.demo.service;

import com.example.demo.model.KubePod;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class KubePodServiceImpl implements KubePodService {

    private final KubernetesClient client;

    private final static String nameSpace = "default";  // Can be put all in config props
    private final static String matchLabel = "service";
    private final static String deploymentMetaLabel = "applicationGroup";

    public KubePodServiceImpl(KubernetesClient client) {
        this.client = client;
    }

    @Override
    public List<KubePod> listKubePodUsingGET(String applicationGroup) {
        return kubeList("default", "service", "applicationGroup").stream()
                .filter(kubePod -> Objects.equals(kubePod.getApplicationGroup(), applicationGroup.isEmpty() ? null : applicationGroup))
                .collect(Collectors.toList());
    }

    @Override
    public List<KubePod> listKubePodUsingGET() {
        return kubeList(nameSpace, matchLabel, deploymentMetaLabel);
    }

    private List<KubePod> kubeList(String nameSpace, String matchLabel, String deploymentMetaLabel) {

        Map<String, Long> nodeMap = client.pods().inNamespace(nameSpace).list().getItems()
                .stream()
                .filter(pod -> pod.getStatus().getPhase().equals("Running"))
                .map(Pod::getMetadata)
                .map(ObjectMeta::getLabels)
                .filter(Objects::nonNull) // NPE on pods without metadata.labels
                .map(stringStringMap -> stringStringMap.get(matchLabel))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        List<Deployment> deployments = client.apps().deployments().inNamespace(nameSpace).list().getItems();

        return nodeMap.entrySet().stream()
                .map(entry -> KubePod.builder()
                        .name(entry.getKey())
                        .applicationGroup(deployments.stream()
                                .filter(deployment -> Objects.equals(deployment.getSpec().getSelector().getMatchLabels().get(matchLabel), entry.getKey()))
                                .findFirst()
                                .map(Deployment::getMetadata)
                                .map(ObjectMeta::getLabels)
                                .map(map -> map.get(deploymentMetaLabel))
                                .orElse(null))
                        .runningPodsCount(entry.getValue())
                        .build())
                .collect(Collectors.toList());
    }

}