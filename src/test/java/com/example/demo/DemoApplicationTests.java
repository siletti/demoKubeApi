package com.example.demo;

import com.example.demo.controller.ServicesController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class DemoApplicationTests {

    @Autowired
    private ServicesController controller;

    @Test
    public void contextLoads() {
        assertThat(controller).isNotNull();
    }

}

