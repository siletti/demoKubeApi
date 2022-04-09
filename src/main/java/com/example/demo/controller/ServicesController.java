package com.example.demo.controller;


import com.example.demo.model.KubePod;
import com.example.demo.service.KubePodService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ServicesController {

    private final KubePodService kubePodService;

    private static final Logger log = LoggerFactory.getLogger(ServicesController.class);

    public ServicesController(KubePodService kubePodService) {
        this.kubePodService = kubePodService;
    }

    @GetMapping("/services/{applicationGroup}")
    @ApiOperation(value = "Get the pods that are part of the same 'applicationGroup'", notes = "Expose information on a group of applications in the cluster with namespace 'default'")
    public ResponseEntity<List<KubePod>> get(@PathVariable final String applicationGroup) {

        log.info("Handling request for {}", applicationGroup);

        List<KubePod> kubePods = kubePodService.listKubePodUsingGET(applicationGroup.trim());

        if (CollectionUtils.isEmpty(kubePods)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        log.info("Return: {}", kubePods);
        return ResponseEntity.ok(kubePods);
    }

    @GetMapping("/services")
    @ApiOperation(value = "Get all pods", notes = "Expose information on all pods in the cluster with namespace 'default'")
    public ResponseEntity<List<KubePod>> getAll() {

        log.info("Handling request for all pods");
        List<KubePod> kubePods = kubePodService.listKubePodUsingGET();

        log.info("Return: {}", kubePods);
        return ResponseEntity.ok(kubePods);
    }

}