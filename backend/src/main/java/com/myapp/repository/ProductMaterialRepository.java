package com.myapp.repository;

import com.myapp.entity.ProductMaterial;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProductMaterialRepository implements PanacheRepository<ProductMaterial> {
}