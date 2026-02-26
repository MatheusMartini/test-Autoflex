package com.myapp.service;

import com.myapp.dto.RawMaterialRequestDTO;
import com.myapp.dto.RawMaterialResponseDTO;
import com.myapp.entity.RawMaterial;
import com.myapp.exception.ConflictException;
import com.myapp.mapper.RawMaterialMapper;
import com.myapp.repository.RawMaterialRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.List;

@ApplicationScoped
public class RawMaterialService {

    private final RawMaterialRepository repository;

    public RawMaterialService(RawMaterialRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public RawMaterialResponseDTO create(RawMaterialRequestDTO dto) {
        if (repository.existsByCode(dto.code())) {
            throw new ConflictException("Raw material code already exists");
        }

        RawMaterial entity = RawMaterialMapper.toEntity(dto);
        repository.persist(entity);
        return RawMaterialMapper.toResponse(entity);
    }

    public List<RawMaterialResponseDTO> findAll() {
        return repository.listAll()
                .stream()
                .map(RawMaterialMapper::toResponse)
                .toList();
    }

    public RawMaterialResponseDTO findById(Long id) {
        RawMaterial entity = repository.findById(id);
        if (entity == null) {
            throw new NotFoundException("RawMaterial not found");
        }
        return RawMaterialMapper.toResponse(entity);
    }

    @Transactional
    public RawMaterialResponseDTO update(Long id, RawMaterialRequestDTO dto) {
        RawMaterial entity = repository.findById(id);
        if (entity == null) {
            throw new NotFoundException("RawMaterial not found");
        }

        if (repository.existsByCodeAndIdNot(dto.code(), id)) {
            throw new ConflictException("Raw material code already exists");
        }

        entity.setCode(dto.code());
        entity.setName(dto.name());
        entity.setStockQuantity(dto.stockQuantity());

        return RawMaterialMapper.toResponse(entity);
    }

    @Transactional
    public void delete(Long id) {
        RawMaterial entity = repository.findById(id);
        if (entity == null) {
            throw new NotFoundException("RawMaterial not found");
        }

        if (repository.isUsedInProductMaterials(id)) {
            throw new ConflictException("Cannot delete raw material because it is associated with products");
        }

        if (repository.isUsedInProductionRuns(id)) {
            throw new ConflictException("Cannot delete raw material because it is used in production history");
        }

        repository.delete(entity);
    }
}
