package com.myapp.dto;

import java.math.BigDecimal;

public record RawMaterialResponseDTO(
        Long id,
        String code,
        String name,
        BigDecimal stockQuantity
) {}
