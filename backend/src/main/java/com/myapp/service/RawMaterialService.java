package com.myapp.service;

import com.myapp.dto.RawMaterialRequestDTO;
import com.myapp.dto.RawMaterialResponseDTO;
import com.myapp.entity.RawMaterial;
import com.myapp.mapper.RawMaterialMapper;
import com.myapp.repository.RawMaterialRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class RawMaterialService {

    @Inject
    RawMaterialRepository repository;

    @Transactional
    public RawMaterialResponseDTO create(RawMaterialRequestDTO dto) {
        RawMaterial entity = RawMaterialMapper.toEntity(dto);
        repository.persist(entity);
        return RawMaterialMapper.toResponse(entity);
    }

    public List<RawMaterialResponseDTO> findAll() {
        return repository.listAll()
                .stream()
                .map(RawMaterialMapper::toResponse)
                .collect(Collectors.toList());
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

        entity.setCode(dto.code);
        entity.setName(dto.name);
        entity.setStockQuantity(dto.stockQuantity);

        return RawMaterialMapper.toResponse(entity);
    }

    @Transactional
    public void delete(Long id) {
        boolean deleted = repository.deleteById(id);
        if (!deleted) {
            throw new NotFoundException("RawMaterial not found");
        }
    }
}