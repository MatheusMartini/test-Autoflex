package com.myapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record RawMaterialRequestDTO(
        @NotBlank
        @Size(max = 64)
        String code,
        @NotBlank
        @Size(max = 180)
        String name,
        @NotNull
        @PositiveOrZero
        @Digits(integer = 15, fraction = 4)
        BigDecimal stockQuantity
) {}
