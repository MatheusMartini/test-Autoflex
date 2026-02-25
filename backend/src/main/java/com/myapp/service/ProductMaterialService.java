package com.myapp.service;

import com.myapp.dto.ProductMaterialRequestDTO;
import com.myapp.dto.ProductMaterialResponseDTO;
import com.myapp.entity.Product;
import com.myapp.entity.ProductMaterial;
import com.myapp.entity.RawMaterial;
import com.myapp.repository.ProductMaterialRepository;
import com.myapp.repository.ProductRepository;
import com.myapp.repository.RawMaterialRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ProductMaterialService {

    @Inject
    ProductMaterialRepository repository;

    @Inject
    ProductRepository productRepository;

    @Inject
    RawMaterialRepository rawMaterialRepository;

    // CREATE
    @Transactional
    public ProductMaterialResponseDTO create(ProductMaterialRequestDTO dto) {

        validate(dto);

        Product product = productRepository.findById(dto.productId);
        if (product == null) {
            throw new WebApplicationException("Product not found", 404);
        }

        RawMaterial rawMaterial = rawMaterialRepository.findById(dto.rawMaterialId);
        if (rawMaterial == null) {
            throw new WebApplicationException("Raw material not found", 404);
        }

        ProductMaterial entity = new ProductMaterial();
        entity.setProduct(product);
        entity.setRawMaterial(rawMaterial);
        entity.setRequiredQuantity(dto.requiredQuantity);

        repository.persist(entity);

        return toDTO(entity);
    }

    // UPDATE
    @Transactional
    public ProductMaterialResponseDTO update(Long id, ProductMaterialRequestDTO dto) {

        validate(dto);

        ProductMaterial entity = repository.findById(id);
        if (entity == null) {
            throw new WebApplicationException("ProductMaterial not found", 404);
        }

        Product product = productRepository.findById(dto.productId);
        RawMaterial rawMaterial = rawMaterialRepository.findById(dto.rawMaterialId);

        entity.setProduct(product);
        entity.setRawMaterial(rawMaterial);
        entity.setRequiredQuantity(dto.requiredQuantity);

        return toDTO(entity);
    }

    // DELETE
    @Transactional
    public void delete(Long id) {
        ProductMaterial entity = repository.findById(id);
        if (entity == null) {
            throw new WebApplicationException("ProductMaterial not found", 404);
        }
        repository.delete(entity);
    }

    // LIST
    public List<ProductMaterialResponseDTO> listAll() {
        return repository.listAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ProductMaterialResponseDTO findById(Long id) {
        ProductMaterial entity = repository.findById(id);
        if (entity == null) {
            throw new WebApplicationException("ProductMaterial not found", 404);
        }
        return toDTO(entity);
    }

    // =========================

    private void validate(ProductMaterialRequestDTO dto) {
        if (dto.productId == null) {
            throw new WebApplicationException("Product is required", 400);
        }
        if (dto.rawMaterialId == null) {
            throw new WebApplicationException("Raw material is required", 400);
        }
        if (dto.requiredQuantity == null ||
            dto.requiredQuantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new WebApplicationException("Required quantity must be greater than zero", 400);
        }
    }

    private ProductMaterialResponseDTO toDTO(ProductMaterial entity) {

        ProductMaterialResponseDTO dto = new ProductMaterialResponseDTO();

        dto.id = entity.getId();

        dto.productId = entity.getProduct().getId();
        dto.productCode = entity.getProduct().getCode();
        dto.productName = entity.getProduct().getName();

        dto.rawMaterialId = entity.getRawMaterial().getId();
        dto.rawMaterialCode = entity.getRawMaterial().getCode();
        dto.rawMaterialName = entity.getRawMaterial().getName();

        dto.requiredQuantity = entity.getRequiredQuantity();

        return dto;
    }
}