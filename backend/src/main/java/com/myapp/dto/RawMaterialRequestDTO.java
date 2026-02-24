package com.myapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class RawMaterialRequestDTO {

    @NotBlank
    public String code;

    @NotBlank
    public String name;

    @NotNull
    public BigDecimal stockQuantity;
}