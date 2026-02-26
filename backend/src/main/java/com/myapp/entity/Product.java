package com.myapp.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(
        name = "product",
        indexes = {
                @Index(name = "idx_product_code", columnList = "code", unique = true)
        }
)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(unique = true, nullable = false, length = 64)
    private String code;

    @Column(nullable = false, length = 180)
    private String name;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal price;

    @OneToMany(
        mappedBy = "product",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    @JsonIgnore
    public List<ProductMaterial> materials = new ArrayList<>();

    // getters

    public Long getId() { return id; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public BigDecimal getPrice() { return price; }
    public List<ProductMaterial> getMaterials() { return materials; }

    // setters

    public void setCode(String code) { this.code = code; }
    public void setName(String name) { this.name = name; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public void setMaterials(List<ProductMaterial> materials) {
        this.materials = materials;
    }

    // Auxiliary Method

    public void addMaterial(ProductMaterial material) {
        materials.add(material);
        material.setProduct(this);
    }

    public void removeMaterial(ProductMaterial material) {
        materials.remove(material);
        material.setProduct(null);
    }
}