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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@EnableRuleMigrationSupport
class KubePodServiceImplTest {

    @InjectMocks
    private KubePodServiceImpl kubePodService;

    @Mock
    private KubernetesClient kubernetesClient;

    @Rule
    public KubernetesServer server = new KubernetesServer();

    @BeforeEach
    public void initEach() {
        Deployment deployment1 = getDeployment("name1", "applicationGroup", "beta", 1);
        Deployment deployment2 = getDeployment("name2", "applicationGroup", "gamma", 3);
        Deployment deployment3 = getDeployment("name3", "foo", "bar", 1);

        PodList expectedPodList = new PodListBuilder().withItems(getPod(deployment1),
                getPod(deployment2),
                getPod(deployment2),
                getPod(deployment2),
                getPod(deployment3)).build();
        server.expect().get().withPath("/api/v1/namespaces/default/pods")
                .andReturn(HttpURLConnection.HTTP_OK, expectedPodList)
                .once();

        DeploymentList deploymentList = new DeploymentListBuilder()
                .withItems(deployment1, deployment2, deployment3)
                .build();
        server.expect().get().withPath("/apis/apps/v1/namespaces/default/deployments")
                .andReturn(HttpURLConnection.HTTP_OK, deploymentList).once();
    }

    @Test
    void listKubePodUsingGeTest() {
        KubernetesClient client = server.getClient();

        when(kubernetesClient.pods()).thenReturn(client.pods());
        when(kubernetesClient.apps()).thenReturn(client.apps());

        //call method
        List<KubePod> actual = kubePodService.listKubePodUsingGET("gamma");

        List<KubePod> expected = List.of(
                KubePod.builder()
                        .name("name2")
                        .applicationGroup("gamma")
                        .runningPodsCount(3)
                        .build()
        );

        assertThat(actual, containsInAnyOrder(expected.toArray()));

    }

    @Test
    void listKubePodUsingGeTestAll() {
        KubernetesClient client = server.getClient();

        when(kubernetesClient.pods()).thenReturn(client.pods());
        when(kubernetesClient.apps()).thenReturn(client.apps());

        //call method
        List<KubePod> actual = kubePodService.listKubePodUsingGET();

        List<KubePod> expected = List.of(
                KubePod.builder()
                        .name("name1")
                        .applicationGroup("beta")
                        .runningPodsCount(1)
                        .build(),
                KubePod.builder()
                        .name("name2")
                        .applicationGroup("gamma")
                        .runningPodsCount(3)
                        .build(),
                KubePod.builder()
                        .name("name3")
                        .runningPodsCount(1)
                        .build()
        );

        assertThat(actual, containsInAnyOrder(expected.toArray()));

    }

    private Pod getPod(Deployment deployment) {
        Pod pod = new PodBuilder()
                .withNewMetadataLike(deployment.getSpec().getTemplate().getMetadata())
                .withNamespace("default")
                .endMetadata()
                .withNewSpecLike(deployment.getSpec().getTemplate().getSpec()).endSpec()
                .build();
        pod.setStatus(new PodStatusBuilder().withPhase("Running").build());
        return pod;
    }

    private Deployment getDeployment(String matchLabelValue, String deploymentMetaLabel, String deploymentMetaLabelValue, int replica) {

        Map<String, String> label = Map.of("service", matchLabelValue, deploymentMetaLabel, deploymentMetaLabelValue);

        return new DeploymentBuilder()
                .withNewMetadata()
                .withName(matchLabelValue)
                .withLabels(label)
                .endMetadata()
                .withNewSpec()
                .withNewSelector()
                .addToMatchLabels("service", matchLabelValue)
                .endSelector()
                .withReplicas(replica)
                .withNewTemplate()
                .withNewMetadata()
                .addToLabels("service", matchLabelValue)
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
    }

}