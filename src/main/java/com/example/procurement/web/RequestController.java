package com.example.procurement.web;

import com.example.procurement.domain.Request;
import com.example.procurement.service.RequestService;
import com.example.procurement.web.dto.ApproveRequestDto;
import com.example.procurement.web.dto.CreateRequestDto;
import com.example.procurement.web.dto.CreateRequestResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/requests")
@Tag(name = "Requests")
public class RequestController {

    private final RequestService requestService;

    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    public ResponseEntity<CreateRequestResponse> create(@RequestHeader("X-Actor-Id") String actor,
                                                        @Valid @RequestBody CreateRequestDto body) {
        UUID actorId = UUID.fromString(actor);
        var saved = requestService.create(body, actorId);
        return ResponseEntity.ok(toResp(saved));
    }

    private CreateRequestResponse toResp(Request r) {
        return new CreateRequestResponse(r.getId().toString(), r.getStatus().name());
    }

    @Operation(summary = "購買申請を承認する（単段）")
    @PatchMapping("/{id}/approve")
    public ResponseEntity<Map<String, Object>> approve(
            @PathVariable("id") UUID id,
            @RequestHeader("X-Actor-Id") @NotBlank String actorHeader,
            @RequestBody(required = false) ApproveRequestDto body
    ) {
        UUID actorId = UUID.fromString(actorHeader);
        String comment = (body == null ? null : body.comment());

        String status = requestService.approve(id, actorId, comment);

        return ResponseEntity.ok(Map.of(
                "id", id.toString(),
                "status", status
        ));
    }
}
