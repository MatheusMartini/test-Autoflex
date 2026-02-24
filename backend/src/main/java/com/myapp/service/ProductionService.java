package com.myapp.service;

import com.myapp.dto.ProductionResponseDTO;
import com.myapp.entity.Product;
import com.myapp.entity.ProductMaterial;
import com.myapp.entity.RawMaterial;
import com.myapp.repository.ProductRepository;
import com.myapp.repository.RawMaterialRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@ApplicationScoped
public class ProductionService {

    @Inject
    ProductRepository productRepository;

    @Inject
    RawMaterialRepository rawMaterialRepository;

    @Transactional
    public List<ProductionResponseDTO> calculateProduction(boolean persistStock) {

        List<Product> products = productRepository.findAllWithMaterials();

        // Ordena por maior valor primeiro (opcional mas ideal)
        products.sort((p1, p2) ->
                p2.getPrice().compareTo(p1.getPrice()));

        List<RawMaterial> rawMaterials = rawMaterialRepository.listAll();

        Map<Long, BigDecimal> stockMap = new HashMap<>();
        Map<Long, RawMaterial> rawMaterialEntityMap = new HashMap<>();

        for (RawMaterial rm : rawMaterials) {
            stockMap.put(rm.getId(), rm.getStockQuantity());
            rawMaterialEntityMap.put(rm.getId(), rm);
        }

        List<ProductionResponseDTO> result = new ArrayList<>();

        for (Product product : products) {

            if (product.getMaterials() == null || product.getMaterials().isEmpty())
                continue;

            BigDecimal maxProduction = null;

            // Calcula produção máxima possível
            for (ProductMaterial pm : product.getMaterials()) {

                Long rmId = pm.getRawMaterial().getId();
                BigDecimal availableStock = stockMap.get(rmId);
                BigDecimal required = pm.getRequiredQuantity();

                if (availableStock == null ||
                    required == null ||
                    required.compareTo(BigDecimal.ZERO) <= 0) {

                    maxProduction = BigDecimal.ZERO;
                    break;
                }

                BigDecimal possible =
                        availableStock.divide(required, 0, RoundingMode.DOWN);

                if (maxProduction == null ||
                    possible.compareTo(maxProduction) < 0) {

                    maxProduction = possible;
                }
            }

            if (maxProduction != null &&
                maxProduction.compareTo(BigDecimal.ZERO) > 0) {

                if (persistStock) {

                    for (ProductMaterial pm : product.getMaterials()) {

                        Long rmId = pm.getRawMaterial().getId();
                        RawMaterial rawMaterial = rawMaterialEntityMap.get(rmId);

                        BigDecimal requiredTotal =
                                pm.getRequiredQuantity().multiply(maxProduction);

                        BigDecimal newStock =
                                stockMap.get(rmId).subtract(requiredTotal);

                        // Atualiza mapa
                        stockMap.put(rmId, newStock);

                        // Atualiza entidade gerenciada
                        rawMaterial.setStockQuantity(newStock);

                        // Persist explícito (garantia)
                        rawMaterialRepository.persist(rawMaterial);
                    }
                }

                ProductionResponseDTO dto = new ProductionResponseDTO();
                dto.setProductId(product.getId());
                dto.setProductCode(product.getCode());
                dto.setProductName(product.getName());
                dto.setUnitPrice(product.getPrice());
                dto.setQuantityToProduce(maxProduction);
                dto.setTotalValue(product.getPrice().multiply(maxProduction));

                result.add(dto);
            }
        }

        return result;
    }
}