package com.example.procurement.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

public record CreateRequestDto(
        @NotBlank String applicantId,
        @NotNull @Size(min = 1, max = 100) List<@Valid Item> items,
        @NotNull @DecimalMin("0.0") BigDecimal totalAmount
) {
    public record Item(
            @NotBlank String skuId,
            @Min(1) int qty,
            @NotNull @DecimalMin("0.0") BigDecimal price
    ) {
    }
}
