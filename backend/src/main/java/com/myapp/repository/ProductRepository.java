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
    }

    public boolean existsByCode(String code) {
        return count("code", code) > 0;
    }

    public boolean existsByCodeAndIdNot(String code, Long id) {
        return count("code = ?1 and id <> ?2", code, id) > 0;
    }

    public boolean isUsedInProductionRuns(Long productId) {
        Long count = getEntityManager()
                .createQuery(
                        "SELECT COUNT(ri) FROM ProductionRunItem ri WHERE ri.product.id = :productId",
                        Long.class
                )
                .setParameter("productId", productId)
                .getSingleResult();
        return count != null && count > 0;
    }

    public List<Product> findAllWithMaterials() {
        return getEntityManager()
                .createQuery(
                        "SELECT DISTINCT p FROM Product p " +
                                "LEFT JOIN FETCH p.materials pm " +
                                "LEFT JOIN FETCH pm.rawMaterial",
                        Product.class
                )
                .getResultList();
    }

    public List<Product> findAllWithMaterialsOrderByPriceDesc() {
        return getEntityManager()
                .createQuery(
                        "SELECT DISTINCT p FROM Product p " +
                                "LEFT JOIN FETCH p.materials pm " +
                                "LEFT JOIN FETCH pm.rawMaterial " +
                                "ORDER BY p.price DESC",
                        Product.class
                )
                .getResultList();
    }
}