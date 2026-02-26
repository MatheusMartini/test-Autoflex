package com.myapp.service;

import com.myapp.dto.ProductMaterialRequestDTO;
import com.myapp.dto.ProductMaterialResponseDTO;
import com.myapp.entity.Product;
import com.myapp.entity.ProductMaterial;
import com.myapp.entity.RawMaterial;
import com.myapp.exception.ConflictException;
import com.myapp.repository.ProductMaterialRepository;
import com.myapp.repository.ProductRepository;
import com.myapp.repository.RawMaterialRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.List;

@ApplicationScoped
public class ProductMaterialService {

    private final ProductMaterialRepository repository;
    private final ProductRepository productRepository;
    private final RawMaterialRepository rawMaterialRepository;

    public ProductMaterialService(
            ProductMaterialRepository repository,
            ProductRepository productRepository,
            RawMaterialRepository rawMaterialRepository
    ) {
        this.repository = repository;
        this.productRepository = productRepository;
        this.rawMaterialRepository = rawMaterialRepository;
    }

    // CREATE
    @Transactional
    public ProductMaterialResponseDTO create(ProductMaterialRequestDTO dto) {
        Product product = findProduct(dto.productId());
        RawMaterial rawMaterial = findRawMaterial(dto.rawMaterialId());

        if (repository.existsByProductAndRawMaterial(product.getId(), rawMaterial.getId())) {
            throw new ConflictException("Product material mapping already exists");
        }

        ProductMaterial entity = new ProductMaterial();
        entity.setProduct(product);
        entity.setRawMaterial(rawMaterial);
        entity.setRequiredQuantity(dto.requiredQuantity());

        repository.persist(entity);

        return toDTO(entity);
    }

    // UPDATE
    @Transactional
    public ProductMaterialResponseDTO update(Long id, ProductMaterialRequestDTO dto) {
        ProductMaterial entity = findProductMaterial(id);
        Product product = findProduct(dto.productId());
        RawMaterial rawMaterial = findRawMaterial(dto.rawMaterialId());

        if (repository.existsByProductAndRawMaterialAndIdNot(product.getId(), rawMaterial.getId(), id)) {
            throw new ConflictException("Product material mapping already exists");
        }

        entity.setProduct(product);
        entity.setRawMaterial(rawMaterial);
        entity.setRequiredQuantity(dto.requiredQuantity());

        return toDTO(entity);
    }

    // DELETE
    @Transactional
    public void delete(Long id) {
        ProductMaterial entity = findProductMaterial(id);
        repository.delete(entity);
    }

    // LIST
    public List<ProductMaterialResponseDTO> listAll() {
        return repository.listAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public ProductMaterialResponseDTO findById(Long id) {
        return toDTO(findProductMaterial(id));
    }

    // =========================

    private ProductMaterialResponseDTO toDTO(ProductMaterial entity) {
        return new ProductMaterialResponseDTO(
                entity.getId(),
                entity.getProduct().getId(),
                entity.getProduct().getCode(),
                entity.getProduct().getName(),
                entity.getRawMaterial().getId(),
                entity.getRawMaterial().getCode(),
                entity.getRawMaterial().getName(),
                entity.getRequiredQuantity()
        );
    }

    private Product findProduct(Long id) {
        Product product = productRepository.findById(id);
        if (product == null) {
            throw new NotFoundException("Product not found");
        }
        return product;
    }

    private RawMaterial findRawMaterial(Long id) {
        RawMaterial rawMaterial = rawMaterialRepository.findById(id);
        if (rawMaterial == null) {
            throw new NotFoundException("Raw material not found");
        }
        return rawMaterial;
    }

    private ProductMaterial findProductMaterial(Long id) {
        ProductMaterial entity = repository.findById(id);
        if (entity == null) {
            throw new NotFoundException("ProductMaterial not found");
        }
        return entity;
    }
}
