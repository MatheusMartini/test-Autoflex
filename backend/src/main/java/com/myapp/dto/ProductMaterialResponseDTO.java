package com.myapp.dto;

import java.math.BigDecimal;

public class ProductMaterialResponseDTO {

    public Long id;

    public Long productId;
    public String productCode;
    public String productName;

    public Long rawMaterialId;
    public String rawMaterialCode;
    public String rawMaterialName;

    public BigDecimal requiredQuantity;
}