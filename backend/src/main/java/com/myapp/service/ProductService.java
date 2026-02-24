package com.myapp.service;

import com.myapp.dto.ProductRequestDTO;
import com.myapp.dto.ProductResponseDTO;
import com.myapp.entity.Product;
import com.myapp.mapper.ProductMapper;
import com.myapp.repository.ProductRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

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
                .collect(Collectors.toList());
    }

    public ProductResponseDTO findById(Long id) {
        Product product = repository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        return ProductMapper.toDTO(product);
    }

    @Transactional
    public ProductResponseDTO create(ProductRequestDTO dto) {

        if (repository.findByCode(dto.code()).isPresent()) {
            throw new IllegalArgumentException("Product code already exists");
        }

        Product product = ProductMapper.toEntity(dto);
        repository.persist(product);

        return ProductMapper.toDTO(product);
    }

    @Transactional
    public ProductResponseDTO update(Long id, ProductRequestDTO dto) {

        Product product = repository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        // Usando setters
        product.setCode(dto.code());
        product.setName(dto.name());
        product.setPrice(dto.price());

        return ProductMapper.toDTO(product);
    }

    @Transactional
    public void delete(Long id) {

        Product product = repository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        repository.delete(product);
    }
}