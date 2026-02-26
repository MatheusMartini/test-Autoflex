package com.myapp.repository;

import com.myapp.entity.ProductMaterial;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProductMaterialRepository implements PanacheRepository<ProductMaterial> {

    public boolean existsByProductAndRawMaterial(Long productId, Long rawMaterialId) {
        return count("product.id = ?1 and rawMaterial.id = ?2", productId, rawMaterialId) > 0;
    }

    public boolean existsByProductAndRawMaterialAndIdNot(Long productId, Long rawMaterialId, Long id) {
        return count("product.id = ?1 and rawMaterial.id = ?2 and id <> ?3", productId, rawMaterialId, id) > 0;
    }
}
