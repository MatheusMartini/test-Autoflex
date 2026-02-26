package com.myapp.repository;

import com.myapp.entity.RawMaterial;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class RawMaterialRepository implements PanacheRepository<RawMaterial> {

    public Optional<RawMaterial> findByCode(String code) {
        return find("code", code).firstResultOptional();
    }

    public boolean existsByCode(String code) {
        return count("code", code) > 0;
    }

    public boolean existsByCodeAndIdNot(String code, Long id) {
        return count("code = ?1 and id <> ?2", code, id) > 0;
    }

    public boolean isUsedInProductMaterials(Long rawMaterialId) {
        Long count = getEntityManager()
                .createQuery(
                        "SELECT COUNT(pm) FROM ProductMaterial pm WHERE pm.rawMaterial.id = :rawMaterialId",
                        Long.class
                )
                .setParameter("rawMaterialId", rawMaterialId)
                .getSingleResult();
        return count != null && count > 0;
    }

    public boolean isUsedInProductionRuns(Long rawMaterialId) {
        Long count = getEntityManager()
                .createQuery(
                        "SELECT COUNT(ri) FROM ProductionRunItem ri " +
                                "WHERE EXISTS (" +
                                "SELECT pm.id FROM ProductMaterial pm " +
                                "WHERE pm.product.id = ri.product.id " +
                                "AND pm.rawMaterial.id = :rawMaterialId" +
                                ")",
                        Long.class
                )
                .setParameter("rawMaterialId", rawMaterialId)
                .getSingleResult();
        return count != null && count > 0;
    }
}
