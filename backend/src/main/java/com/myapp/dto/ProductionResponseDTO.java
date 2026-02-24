package com.myapp.dto;

import java.math.BigDecimal;

public class ProductionResponseDTO {

    private Long productId;
    private String productCode;
    private String productName;
    private BigDecimal unitPrice;
    private BigDecimal quantityToProduce;
    private BigDecimal totalValue;

    public ProductionResponseDTO() {}

    // Getters e Setters
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductCode() { return productCode; }
    public void setProductCode(String productCode) { this.productCode = productCode; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public BigDecimal getQuantityToProduce() { return quantityToProduce; }
    public void setQuantityToProduce(BigDecimal quantityToProduce) { this.quantityToProduce = quantityToProduce; }

    public BigDecimal getTotalValue() { return totalValue; }
    public void setTotalValue(BigDecimal totalValue) { this.totalValue = totalValue; }
}