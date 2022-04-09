package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class KubePod implements Serializable {

    @JsonProperty("name")
    private String name;

    @JsonProperty("applicationGroup")
    private String applicationGroup;

    @JsonProperty("runningPodsCount")
    private long runningPodsCount;

}

