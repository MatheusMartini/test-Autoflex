package com.myapp.dto;

import java.math.BigDecimal;
import java.util.List;

public record ProductionPreviewResponseDTO(
        List<ProductionPreviewItemDTO> items,
        BigDecimal grandTotal
) {}
