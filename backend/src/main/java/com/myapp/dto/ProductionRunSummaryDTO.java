package com.myapp.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record ProductionRunSummaryDTO(
        Long id,
        Instant executedAt,
        BigDecimal grandTotal,
        int itemsCount
) {}