package com.example.procurement.web;

import com.example.procurement.domain.Request;
import com.example.procurement.service.RequestService;
import com.example.procurement.web.dto.CreateRequestDto;
import com.example.procurement.web.dto.CreateRequestResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/requests")
public class RequestController {

    private final RequestService service;

    public RequestController(RequestService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CreateRequestResponse> create(@RequestHeader("X-Actor-Id") String actor,
                                                        @Valid @RequestBody CreateRequestDto body) {
        UUID actorId = UUID.fromString(actor);
        var saved = service.create(body, actorId);
        return ResponseEntity.ok(toResp(saved));
    }

    private CreateRequestResponse toResp(Request r) {
        return new CreateRequestResponse(r.getId().toString(), r.getStatus().name());
    }
}
