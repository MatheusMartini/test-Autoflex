package com.myapp.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record ProductionRunDetailDTO(
        Long id,
        Instant executedAt,
        BigDecimal grandTotal,
        List<ProductionItemDTO> items
) {}