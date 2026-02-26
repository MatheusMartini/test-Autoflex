package com.myapp.service;

import com.myapp.dto.RawMaterialRequestDTO;
import com.myapp.dto.RawMaterialResponseDTO;
import com.myapp.entity.RawMaterial;
import com.myapp.exception.ConflictException;
import com.myapp.repository.RawMaterialRepository;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RawMaterialServiceTest {

    @Mock
    RawMaterialRepository repository;

    @InjectMocks
    RawMaterialService service;

    @Test
    void shouldThrowConflictWhenCodeAlreadyExists() {
        RawMaterialRequestDTO request = new RawMaterialRequestDTO(
                "RM-001",
                "Steel Coil",
                BigDecimal.valueOf(50)
        );
        when(repository.existsByCode("RM-001")).thenReturn(true);

        assertThrows(ConflictException.class, () -> service.create(request));

        verify(repository, never()).persist(org.mockito.ArgumentMatchers.any(RawMaterial.class));
    }

    @Test
    void shouldCreateRawMaterialSuccessfully() {
        RawMaterialRequestDTO request = new RawMaterialRequestDTO(
                "RM-002",
                "Aluminum Sheet",
                BigDecimal.valueOf(80)
        );
        when(repository.existsByCode("RM-002")).thenReturn(false);

        RawMaterialResponseDTO result = service.create(request);

        assertEquals("RM-002", result.code());
        assertEquals("Aluminum Sheet", result.name());
        assertEquals(0, BigDecimal.valueOf(80).compareTo(result.stockQuantity()));
        verify(repository).persist(org.mockito.ArgumentMatchers.any(RawMaterial.class));
    }

    @Test
    void shouldThrowNotFoundWhenUpdatingNonExistingMaterial() {
        RawMaterialRequestDTO request = new RawMaterialRequestDTO(
                "RM-003",
                "Copper Wire",
                BigDecimal.valueOf(10)
        );
        when(repository.findById(99L)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> service.update(99L, request));

        verify(repository, never()).existsByCodeAndIdNot(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyLong());
    }

    @Test
    void shouldThrowConflictWhenDeletingRawMaterialAssociatedWithProducts() {
        RawMaterial entity = new RawMaterial();
        when(repository.findById(1L)).thenReturn(entity);
        when(repository.isUsedInProductMaterials(1L)).thenReturn(true);

        ConflictException ex = assertThrows(ConflictException.class, () -> service.delete(1L));

        assertEquals("Cannot delete raw material because it is associated with products", ex.getMessage());
        verify(repository, never()).delete(entity);
    }

    @Test
    void shouldThrowConflictWhenDeletingRawMaterialUsedInProductionHistory() {
        RawMaterial entity = new RawMaterial();
        when(repository.findById(2L)).thenReturn(entity);
        when(repository.isUsedInProductMaterials(2L)).thenReturn(false);
        when(repository.isUsedInProductionRuns(2L)).thenReturn(true);

        ConflictException ex = assertThrows(ConflictException.class, () -> service.delete(2L));

        assertEquals("Cannot delete raw material because it is used in production history", ex.getMessage());
        verify(repository, never()).delete(entity);
    }

    @Test
    void shouldDeleteRawMaterialWhenNotAssociatedAndNotUsedInProductionHistory() {
        RawMaterial entity = new RawMaterial();
        when(repository.findById(3L)).thenReturn(entity);
        when(repository.isUsedInProductMaterials(3L)).thenReturn(false);
        when(repository.isUsedInProductionRuns(3L)).thenReturn(false);

        service.delete(3L);

        verify(repository).delete(entity);
    }
}
