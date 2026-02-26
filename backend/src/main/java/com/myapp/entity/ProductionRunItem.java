package com.myapp.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "production_run_item")
public class ProductionRunItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "run_id")
    public ProductionRun run;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    public Product product;

    @Column(name = "product_code", nullable = false)
    public String productCode;

    @Column(name = "product_name", nullable = false)
    public String productName;

    @Column(name = "quantity", nullable = false)
    public long quantity;

    @Column(name = "unit_price", nullable = false, precision = 19, scale = 2)
    public BigDecimal unitPrice;

    @Column(name = "total_value", nullable = false, precision = 19, scale = 2)
    public BigDecimal totalValue;
}