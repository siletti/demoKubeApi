package com.example.demo.service;

import com.example.demo.model.KubePod;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.api.model.apps.DeploymentList;
import io.fabric8.kubernetes.api.model.apps.DeploymentListBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import org.junit.Rule;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;


@Tag("test")
@ExtendWith(MockitoExtension.class)
@EnableRuleMigrationSupport
class KubePodServiceImplTest {

    @InjectMocks
    private KubePodServiceImpl kubePodService;

    @Mock
    private KubernetesClient kubernetesClient;

    @Rule
    public KubernetesServer server = new KubernetesServer();

    @Test
    void listKubePodUsingGET() {

        Map<String, String> label = Map.of("service", "blissful-goodall", "applicationGroup", "beta");

        Deployment deployment = new DeploymentBuilder()
                .withNewMetadata()
                .withName("blissful-goodall-deployment")
                .withLabels(label)
                .endMetadata()
                .withNewSpec()
                .withNewSelector()
                .addToMatchLabels("service", "blissful-goodall")
                .endSelector()
                .withReplicas(1)
                .withNewTemplate()
                .withNewMetadata()
                .addToLabels("service", "blissful-goodall")
                .endMetadata()
                .withNewSpec()
                .withContainers()
                .addNewContainer()
                .withName("nginx")
                .withImage("nginx:1.15.8")
                .withPorts(new ContainerPortBuilder()
                        .withName("http")
                        .withContainerPort(80)
                        .build())
                .endContainer()
                .endSpec()
                .endTemplate()
                .endSpec()
                .build();

        Pod pod = new PodBuilder()
                .withNewMetadataLike(deployment.getSpec().getTemplate().getMetadata())
                .withNamespace("default")
                .withName("pod1")
                .endMetadata()
                .withNewSpecLike(deployment.getSpec().getTemplate().getSpec()).endSpec()
                .build();
        pod.setStatus(new PodStatusBuilder().withPhase("Running").build());

        PodList expectedPodList = new PodListBuilder().withItems(pod).build();
        server.expect().get().withPath("/api/v1/namespaces/default/pods")
                .andReturn(HttpURLConnection.HTTP_OK, expectedPodList)
                .once();

        DeploymentList deploymentList = new DeploymentListBuilder()
                .withItems(deployment)
                .build();
        server.expect().get().withPath("/apis/apps/v1/namespaces/default/deployments")
                .andReturn(HttpURLConnection.HTTP_OK, deploymentList).times(2);

        KubernetesClient client = server.getClient();

        when(kubernetesClient.pods()).thenReturn(client.pods());
        when(kubernetesClient.apps()).thenReturn(client.apps());

        //call method
        List<KubePod> kubePods = kubePodService.listKubePodUsingGET();

        System.out.println("kubePods = " + kubePods);

    }

    @Test
    void testListKubePodUsingGET() {
    }


}