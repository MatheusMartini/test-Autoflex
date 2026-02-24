package com.myapp.repository;

import com.myapp.entity.RawMaterial;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RawMaterialRepository implements PanacheRepository<RawMaterial> {

    public RawMaterial findByCode(String code) {
        return find("code", code).firstResult();
    }
}