package com.myapp.dto;

import java.math.BigDecimal;

public record ProductMaterialResponseDTO(
        Long id,
        Long productId,
        String productCode,
        String productName,
        Long rawMaterialId,
        String rawMaterialCode,
        String rawMaterialName,
        BigDecimal requiredQuantity
) {}
