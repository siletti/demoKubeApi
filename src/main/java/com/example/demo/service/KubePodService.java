package com.example.demo.service;

import com.example.demo.model.KubePod;

import java.util.List;

public interface KubePodService {

    List<KubePod> listKubePodUsingGET(String applicationGroup);

    List<KubePod> listKubePodUsingGET();
}
