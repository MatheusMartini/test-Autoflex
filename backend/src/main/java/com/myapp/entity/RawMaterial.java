package com.myapp.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "raw_material",
        indexes = {
                @Index(name = "idx_raw_material_code", columnList = "code", unique = true)
        }
)
public class RawMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(unique = true, nullable = false, length = 64)
    private String code;

    @Column(nullable = false, length = 180)
    private String name;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal stockQuantity;

    @OneToMany(mappedBy = "rawMaterial", fetch = FetchType.LAZY)
    private List<ProductMaterial> products = new ArrayList<>();

    // getters

    public Long getId() { return id; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public BigDecimal getStockQuantity() { return stockQuantity; }
    public List<ProductMaterial> getProducts() { return products; }

    // setters

    public void setCode(String code) { this.code = code; }
    public void setName(String name) { this.name = name; }
    public void setStockQuantity(BigDecimal stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
}