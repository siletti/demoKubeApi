package com.example.demo.service;

import com.example.demo.model.KubePod;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class KubePodServiceImpl implements KubePodService {

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


}