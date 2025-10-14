package com.example.procurement.web;

import com.example.procurement.domain.Request;
import com.example.procurement.domain.RequestStatus;
import com.example.procurement.service.RequestService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("slice-test")
@WebMvcTest(RequestController.class)
public class RequestControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    RequestService requestService;

    @Test
    void create_ok() throws Exception {
        var reqId = UUID.randomUUID();

        var saved = new Request();
        saved.setId(reqId);
        saved.setStatus(RequestStatus.SUBMITTED);

        Mockito.when(requestService.create(Mockito.any(), Mockito.any())).thenReturn(saved);

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

    @Test
    void create_validationError_onQtyZero() throws Exception {

        var actor = UUID.randomUUID().toString();

        var json = """
                {
                    "applicantId": "%s",
                    "items": [{"skuId":"A-001","qty":0,"price":1000.00}],
                    "totalAmount": 2000.00
                }
                """.formatted(actor);
        mvc.perform(post("/api/requests")
                        .header("X-Actor-Id", actor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details").exists())
                .andExpect(jsonPath("$.details['items[0].qty']").value("must be greater than or equal to 1"));
    }

    @Test
    void approve_success_200_and_status_APPROVED() throws Exception {
        var reqId = UUID.randomUUID();
        var actor = UUID.randomUUID();

        Mockito.when(requestService.approve(eq(reqId), eq(actor), any()))
                .thenReturn("APPROVED");

        var json = """
                {"comment":"OK"}
                """;

        mvc.perform(patch("/api/requests/{id}/approve", reqId)
                        .header("X-Actor-Id", actor.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reqId.toString()))
                .andExpect(jsonPath("$.status").value("APPROVED"));

        Mockito.verify(requestService).approve(eq(reqId), eq(actor), eq("OK"));
    }

    @Test
    void approve_twice_returns_409_conflict() throws Exception {
        var reqId = UUID.randomUUID();
        var actor = UUID.randomUUID();

        Mockito.when(requestService.approve(eq(reqId), eq(actor), any()))
                .thenThrow(new IllegalStateException("invalid status transition: APPROVED -> APPROVED"));

        mvc.perform(patch("/api/requests/{id}/approve", reqId)
                        .header("X-Actor-Id", actor.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"comment\":\"2nd\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("invalid status transition: APPROVED -> APPROVED"));
    }

}
