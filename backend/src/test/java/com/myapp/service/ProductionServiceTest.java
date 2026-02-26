package com.myapp.service;

import com.myapp.dto.ProductionPreviewResponseDTO;
import com.myapp.entity.Product;
import com.myapp.entity.ProductMaterial;
import com.myapp.entity.RawMaterial;
import com.myapp.repository.ProductRepository;
import com.myapp.repository.ProductionRunRepository;
import com.myapp.repository.RawMaterialRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductionServiceTest {

    @Mock
    ProductRepository productRepository;

    @Mock
    RawMaterialRepository rawMaterialRepository;

    @Mock
    ProductionRunRepository productionRunRepository;

    @InjectMocks
    ProductionService service;

    @Test
    void shouldPrioritizeHigherValueProducts() {
        RawMaterial steel = new RawMaterial();
        setId(steel, 1L);
        steel.setCode("RM-STEEL");
        steel.setName("Steel");
        steel.setStockQuantity(BigDecimal.TEN);

        Product premiumProduct = createProduct(
                10L,
                "PRM-001",
                "Premium Part",
                BigDecimal.valueOf(100),
                steel,
                BigDecimal.valueOf(2)
        );

        Product standardProduct = createProduct(
                20L,
                "STD-001",
                "Standard Part",
                BigDecimal.valueOf(60),
                steel,
                BigDecimal.ONE
        );

        when(rawMaterialRepository.listAll()).thenReturn(List.of(steel));
        when(productRepository.findAllWithMaterialsOrderByPriceDesc())
                .thenReturn(List.of(premiumProduct, standardProduct));

        ProductionPreviewResponseDTO plan = service.previewProduction();

        assertEquals(1, plan.items().size());
        assertEquals("PRM-001", plan.items().get(0).productCode());
        assertEquals(5L, plan.items().get(0).maxQuantity());
        assertEquals(0, BigDecimal.valueOf(500).compareTo(plan.items().get(0).totalValue()));
        assertEquals(0, BigDecimal.valueOf(500).compareTo(plan.grandTotal()));
        verify(productRepository).findAllWithMaterialsOrderByPriceDesc();
        verify(rawMaterialRepository).listAll();
    }

    private Product createProduct(
            Long id,
            String code,
            String name,
            BigDecimal price,
            RawMaterial material,
            BigDecimal requiredQuantity
    ) {
        Product product = new Product();
        setId(product, id);
        product.setCode(code);
        product.setName(name);
        product.setPrice(price);

        ProductMaterial productMaterial = new ProductMaterial();
        productMaterial.setProduct(product);
        productMaterial.setRawMaterial(material);
        productMaterial.setRequiredQuantity(requiredQuantity);
        product.setMaterials(List.of(productMaterial));

        return product;
    }

    private void setId(Object target, Long id) {
        try {
            Field field = target.getClass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(target, id);
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            throw new RuntimeException("Failed to set test entity id", ex);
        }
    }
}
