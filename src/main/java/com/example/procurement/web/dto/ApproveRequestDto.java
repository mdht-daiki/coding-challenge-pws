package com.example.procurement.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record ApproveRequestDto(
        @Schema(description = "承認コメント（任意）")
        String comment
) {
}
