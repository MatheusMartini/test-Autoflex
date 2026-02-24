package com.myapp.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "raw_material")
public class RawMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal stockQuantity;

    @OneToMany(mappedBy = "rawMaterial", fetch = FetchType.LAZY)
    private List<ProductMaterial> products = new ArrayList<>();

    // GETTERS

    public Long getId() { return id; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public BigDecimal getStockQuantity() { return stockQuantity; }
    public List<ProductMaterial> getProducts() { return products; }

    // SETTERS

    public void setCode(String code) { this.code = code; }
    public void setName(String name) { this.name = name; }
    public void setStockQuantity(BigDecimal stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
}