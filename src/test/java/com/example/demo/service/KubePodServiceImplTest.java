package com.example.demo.service;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@Tag("test")
@SpringBootTest
class KubePodServiceImplTest {


    @Autowired
    private KubePodServiceImpl kubePodService;

//    @Mock
//    private KubernetesClient client;


    @Test
    void getKubePodUsingGET() {
    }

    @Test
    void listKubePodUsingGET() {
    }

    @Test
    void podList() {
        List<Pod> list = kubePodService.podList("default", "service", "applicationGroup");
        System.out.println("list = " + list);

    }

    @Test
    void deploymenList() {
        List<Deployment> list = kubePodService.deploymenList("default");
        System.out.println("list = " + list);

    }
}