package com.myapp.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal price;

    @OneToMany(
        mappedBy = "product",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    private List<ProductMaterial> materials = new ArrayList<>();

    // GETTERS

    public Long getId() { return id; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public BigDecimal getPrice() { return price; }
    public List<ProductMaterial> getMaterials() { return materials; }

    // SETTERS

    public void setCode(String code) { this.code = code; }
    public void setName(String name) { this.name = name; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public void setMaterials(List<ProductMaterial> materials) {
        this.materials = materials;
    }

    // MÉTODO AUXILIAR

    public void addMaterial(ProductMaterial material) {
        materials.add(material);
        material.setProduct(this);
    }

    public void removeMaterial(ProductMaterial material) {
        materials.remove(material);
        material.setProduct(null);
    }
}