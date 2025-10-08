package com.example.procurement.web;

import com.example.procurement.domain.Request;
import com.example.procurement.domain.RequestStatus;
import com.example.procurement.service.RequestService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RequestController.class)
public class RequestControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    RequestService service;

    @Test
    void create_ok() throws Exception {
        var reqId = UUID.randomUUID();

        var saved = new Request();
        saved.setId(reqId);
        saved.setStatus(RequestStatus.SUBMITTED);

        Mockito.when(service.create(Mockito.any(), Mockito.any())).thenReturn(saved);

        var json = """
                {
                  "applicantId": "%s",
                  "items": [{"skuId":"A-001","qty":2,"price":1000.00}],
                  "totalAmount": 2000.00
                }
                """.formatted(UUID.randomUUID());

        mvc.perform(post("/api/requests")
                        .header("X-Actor-Id", UUID.randomUUID().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reqId.toString()))
                .andExpect(jsonPath("$.status").value("SUBMITTED"));
    }
}
