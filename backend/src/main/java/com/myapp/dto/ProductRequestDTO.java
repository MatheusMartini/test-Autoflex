package com.myapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ProductRequestDTO(

        @NotBlank
        String code,

        @NotBlank
        String name,

        @NotNull
        BigDecimal price
) {}
