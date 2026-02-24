package com.myapp.service;

import java.math.BigDecimal;

import com.myapp.dto.ProductMaterialRequestDTO;
import com.myapp.entity.Product;
import com.myapp.entity.ProductMaterial;
import com.myapp.entity.RawMaterial;
import com.myapp.repository.ProductMaterialRepository;
import com.myapp.repository.ProductRepository;
import com.myapp.repository.RawMaterialRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class ProductMaterialService {

    @Inject
    ProductMaterialRepository repository;

    @Inject
    ProductRepository productRepository;

    @Inject
    RawMaterialRepository rawMaterialRepository;

    @Transactional
    public void create(ProductMaterialRequestDTO dto) {

        if (dto == null) {
            throw new BadRequestException("Body cannot be null");
        }

        if (dto.productId == null) {
            throw new BadRequestException("productId is required");
        }

        if (dto.rawMaterialId == null) {
            throw new BadRequestException("rawMaterialId is required");
        }

        if (dto.requiredQuantity == null) {
            throw new BadRequestException("requiredQuantity is required");
        }

        if (dto.requiredQuantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("requiredQuantity must be greater than zero");
        }

        Product product = productRepository.findById(dto.productId);
        if (product == null) {
            throw new NotFoundException("Product not found with id: " + dto.productId);
        }

        RawMaterial rawMaterial = rawMaterialRepository.findById(dto.rawMaterialId);
        if (rawMaterial == null) {
            throw new NotFoundException("RawMaterial not found with id: " + dto.rawMaterialId);
        }

        ProductMaterial entity = new ProductMaterial();
        entity.setProduct(product);
        entity.setRawMaterial(rawMaterial);
        entity.setRequiredQuantity(dto.requiredQuantity);

        repository.persist(entity);
    }
}