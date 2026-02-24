package com.myapp.repository;

import com.myapp.entity.Product;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ProductRepository implements PanacheRepository<Product> {

    public Optional<Product> findByCode(String code) {
        return find("code", code).firstResultOptional();
    };

    public List<Product> findAllWithMaterials() {
    return getEntityManager()
        .createQuery(
            "SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.materials",
            Product.class
        )
        .getResultList();
}
}