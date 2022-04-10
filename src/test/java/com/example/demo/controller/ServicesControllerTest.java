package com.example.demo.controller;

import com.example.demo.model.KubePod;
import com.example.demo.service.KubePodService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServicesControllerTest {

    @InjectMocks
    ServicesController controller;

    @Mock
    private KubePodService kubePodService;

    private final MockHttpServletRequest request = new MockHttpServletRequest();

    private List<KubePod> expected;


    @BeforeEach
    public void initEach() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        expected = List.of(
                KubePod.builder()
                        .name("n1")
                        .applicationGroup("beta")
                        .runningPodsCount(1)
                        .build(),
                KubePod.builder()
                        .name("n2")
                        .applicationGroup("beta")
                        .runningPodsCount(2)
                        .build());
    }

    @Test
    void getTest() {
        when(kubePodService.listKubePodUsingGET("beta")).thenReturn(expected);

        //call method to test
        ResponseEntity<List<KubePod>> responseEntity = controller.get("beta");

        List<KubePod> actual = responseEntity.getBody();

        assertEquals(responseEntity.getStatusCodeValue(), 200);

        assertThat(actual, containsInAnyOrder(expected.toArray()));
    }

    @Test
    void getTest404() {
        when(kubePodService.listKubePodUsingGET("bar")).thenReturn(Collections.emptyList());

        //call method to test
        ResponseEntity<List<KubePod>> responseEntity = controller.get("bar");

        assertEquals(responseEntity.getStatusCodeValue(), 404);
    }

    @Test
    void getAllTest() {
        when(kubePodService.listKubePodUsingGET()).thenReturn(expected);

        //call method to test
        ResponseEntity<List<KubePod>> responseEntity = controller.getAll();

        List<KubePod> actual = responseEntity.getBody();

        assertEquals(responseEntity.getStatusCodeValue(), 200);

        assertThat(actual, containsInAnyOrder(expected.toArray()));
    }


}