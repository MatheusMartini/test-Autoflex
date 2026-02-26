package com.myapp.repository;

import com.myapp.entity.ProductionRun;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class ProductionRunRepository implements PanacheRepository<ProductionRun> {

    public List<ProductionRun> findAllOrderByExecutedAtDesc() {
        return find("order by executedAt desc").list();
    }
}