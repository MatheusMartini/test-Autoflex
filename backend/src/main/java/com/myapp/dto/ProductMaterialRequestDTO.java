package com.myapp.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ProductMaterialRequestDTO(
        @NotNull
        @Positive
        Long productId,
        @NotNull
        @Positive
        Long rawMaterialId,
        @NotNull
        @Positive
        @Digits(integer = 15, fraction = 4)
        BigDecimal requiredQuantity
) {}
