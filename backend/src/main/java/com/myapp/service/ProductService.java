package com.myapp.service;

import com.myapp.dto.ProductRequestDTO;
import com.myapp.dto.ProductResponseDTO;
import com.myapp.entity.Product;
import com.myapp.exception.ConflictException;
import com.myapp.mapper.ProductMapper;
import com.myapp.repository.ProductRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.List;
@ApplicationScoped
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public List<ProductResponseDTO> findAll() {
        return repository.listAll()
                .stream()
                .map(ProductMapper::toDTO)
                .toList();
    }

    public ProductResponseDTO findById(Long id) {
        Product product = repository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        return ProductMapper.toDTO(product);
    }

    @Transactional
    public ProductResponseDTO create(ProductRequestDTO dto) {
        if (repository.existsByCode(dto.code())) {
            throw new ConflictException("Product code already exists");
        }

        Product product = ProductMapper.toEntity(dto);
        repository.persist(product);

        return ProductMapper.toDTO(product);
    }

    @Transactional
    public ProductResponseDTO update(Long id, ProductRequestDTO dto) {

        Product product = repository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        if (repository.existsByCodeAndIdNot(dto.code(), id)) {
            throw new ConflictException("Product code already exists");
        }

        product.setCode(dto.code());
        product.setName(dto.name());
        product.setPrice(dto.price());

        return ProductMapper.toDTO(product);
    }

    @Transactional
    public void delete(Long id) {

        Product product = repository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        if (repository.isUsedInProductionRuns(id)) {
            throw new ConflictException("Cannot delete product because it is used in production history");
        }

        repository.delete(product);
    }
}
