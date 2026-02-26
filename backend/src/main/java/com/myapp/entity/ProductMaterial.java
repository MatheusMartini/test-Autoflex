package com.myapp.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(
        name = "product_material",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_product_material_product_raw",
                        columnNames = {"product_id", "raw_material_id"}
                )
        },
        indexes = {
                @Index(name = "idx_product_material_product_id", columnList = "product_id"),
                @Index(name = "idx_product_material_raw_material_id", columnList = "raw_material_id")
        }
)
public class ProductMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnore
    private Product product;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "raw_material_id", nullable = false)
    @JsonIgnore
    private RawMaterial rawMaterial;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal requiredQuantity;

    // getters

    public Long getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public RawMaterial getRawMaterial() {
        return rawMaterial;
    }

    public BigDecimal getRequiredQuantity() {
        return requiredQuantity;
    }

    // setters

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setRawMaterial(RawMaterial rawMaterial) {
        this.rawMaterial = rawMaterial;
    }

    public void setRequiredQuantity(BigDecimal requiredQuantity) {
        this.requiredQuantity = requiredQuantity;
    }
}