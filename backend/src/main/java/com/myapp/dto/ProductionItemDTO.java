package com.myapp.dto;

import java.math.BigDecimal;

public record ProductionItemDTO(
        Long productId,
        String productCode,
        String productName,
        long maxQuantity,
        BigDecimal unitPrice,
        BigDecimal totalValue
) {}