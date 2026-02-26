package com.myapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ProductRequestDTO(

        @NotBlank
        @Size(max = 64)
        String code,

        @NotBlank
        @Size(max = 180)
        String name,

        @NotNull
        @Positive
        @Digits(integer = 15, fraction = 4)
        BigDecimal price
) {}
