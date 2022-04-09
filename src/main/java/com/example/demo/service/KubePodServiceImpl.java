package com.example.demo.service;

import com.example.demo.model.KubePod;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class KubePodServiceImpl implements KubePodService {

    private final KubernetesClient client;

    public KubePodServiceImpl(KubernetesClient client) {
        this.client = client;
    }

    @Override
    public List<KubePod> getKubePodUsingGET(String applicationGroup) {
        return Collections.singletonList(KubePod.builder()
                .name("name")
                .applicationGroup("group")
                .runningPodsCount(1)
                .build());
    }

    @Override
    public List<KubePod> listKubePodUsingGET() {
        return List.of(KubePod.builder()
                        .name("name2")
                        .applicationGroup("group2")
                        .runningPodsCount(2)
                        .build(),
                KubePod.builder()
                        .name("name")
                        .applicationGroup("group")
                        .runningPodsCount(1)
                        .build());
    }

    List<Pod> podList(String nameSpace, String matchLabel, String deploymentMetaLabel) {
        return client.pods().inNamespace(nameSpace).list().getItems();
    }

    List<Deployment> deploymenList(String nameSpace) {
        return client.apps().deployments().inNamespace(nameSpace).list().getItems();
    }

}