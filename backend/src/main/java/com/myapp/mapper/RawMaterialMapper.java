package com.myapp.mapper;

import com.myapp.dto.RawMaterialRequestDTO;
import com.myapp.dto.RawMaterialResponseDTO;
import com.myapp.entity.RawMaterial;

public class RawMaterialMapper {

    public static RawMaterial toEntity(RawMaterialRequestDTO dto) {
        RawMaterial rawMaterial = new RawMaterial();
        rawMaterial.setCode(dto.code());
        rawMaterial.setName(dto.name());
        rawMaterial.setStockQuantity(dto.stockQuantity());
        return rawMaterial;
    }

    public static RawMaterialResponseDTO toResponse(RawMaterial entity) {
        return new RawMaterialResponseDTO(
                entity.getId(),
                entity.getCode(),
                entity.getName(),
                entity.getStockQuantity()
        );
    }
}
