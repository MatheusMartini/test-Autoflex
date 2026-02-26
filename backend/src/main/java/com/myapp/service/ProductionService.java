package com.myapp.service;

import com.myapp.dto.ProductionPreviewItemDTO;
import com.myapp.dto.ProductionPreviewResponseDTO;
import com.myapp.entity.Product;
import com.myapp.entity.ProductMaterial;
import com.myapp.entity.ProductionRun;
import com.myapp.entity.ProductionRunItem;
import com.myapp.entity.RawMaterial;
import com.myapp.repository.ProductRepository;
import com.myapp.repository.RawMaterialRepository;
import com.myapp.repository.ProductionRunRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class ProductionService {

    @Inject
    ProductRepository productRepository;

    @Inject
    RawMaterialRepository rawMaterialRepository;

    @Inject
    ProductionRunRepository productionRunRepository;

    public ProductionPreviewResponseDTO previewProduction() {
        Map<Long, BigDecimal> availableStock = loadStockSnapshot();
        return calculatePlan(availableStock);
    }

    @Transactional
    public ProductionPreviewResponseDTO executeProduction() {
        List<RawMaterial> rawMaterials = rawMaterialRepository.listAll();

        Map<Long, RawMaterial> rawMaterialById = new HashMap<>();
        Map<Long, BigDecimal> availableStock = new HashMap<>();

        for (RawMaterial rawMaterial : rawMaterials) {
            Long rawMaterialId = rawMaterial.getId();
            if (rawMaterialId == null) {
                throw new WebApplicationException("Raw material with null id cannot be used.", Response.Status.CONFLICT);
            }

            BigDecimal quantity = rawMaterial.getStockQuantity();
            if (quantity == null || quantity.compareTo(BigDecimal.ZERO) < 0) {
                quantity = BigDecimal.ZERO;
            }

            rawMaterialById.put(rawMaterialId, rawMaterial);
            availableStock.put(rawMaterialId, quantity);
        }

        ProductionPreviewResponseDTO plan = calculatePlan(availableStock);

        // Persist updated stock
        for (Map.Entry<Long, BigDecimal> entry : availableStock.entrySet()) {
            RawMaterial rawMaterial = rawMaterialById.get(entry.getKey());
            if (rawMaterial == null) {
                throw new WebApplicationException("Raw material not found while executing production.", Response.Status.CONFLICT);
            }
            rawMaterial.setStockQuantity(entry.getValue());
        }

        ProductionRun run = new ProductionRun();
        run.executedAt = Instant.now();
        run.grandTotal = plan.grandTotal();

        for (ProductionPreviewItemDTO item : plan.items()) {
            ProductionRunItem ri = new ProductionRunItem();
            ri.run = run;

            Product p = productRepository.findById(item.productId());
            ri.product = p;

            ri.productCode = item.productCode();
            ri.productName = item.productName();
            ri.quantity = item.maxQuantity();
            ri.unitPrice = item.unitPrice();
            ri.totalValue = item.totalValue();

            run.items.add(ri);
        }

        productionRunRepository.persist(run);

        return plan;
    }

    private Map<Long, BigDecimal> loadStockSnapshot() {
        List<RawMaterial> rawMaterials = rawMaterialRepository.listAll();
        Map<Long, BigDecimal> availableStock = new HashMap<>();

        for (RawMaterial rawMaterial : rawMaterials) {
            Long rawMaterialId = rawMaterial.getId();
            if (rawMaterialId == null) {
                continue;
            }

            BigDecimal quantity = rawMaterial.getStockQuantity();
            if (quantity == null || quantity.compareTo(BigDecimal.ZERO) < 0) {
                quantity = BigDecimal.ZERO;
            }

            availableStock.put(rawMaterialId, quantity);
        }

        return availableStock;
    }

    private ProductionPreviewResponseDTO calculatePlan(Map<Long, BigDecimal> availableStock) {
        List<Product> products = productRepository.findAllWithMaterials();

        products.sort((a, b) -> {
            BigDecimal sa = scoreByBottleneckEfficiency(a, availableStock);
            BigDecimal sb = scoreByBottleneckEfficiency(b, availableStock);

            int cmp = sb.compareTo(sa); // desc
            if (cmp != 0) return cmp;

            BigDecimal pa = (a == null || a.getPrice() == null) ? BigDecimal.ZERO : a.getPrice();
            BigDecimal pb = (b == null || b.getPrice() == null) ? BigDecimal.ZERO : b.getPrice();
            cmp = pb.compareTo(pa); // price desc
            if (cmp != 0) return cmp;

            // desempate extra: produto que consome menos do gargalo primeiro
            BigDecimal ca = bottleneckRequiredQty(a, availableStock);
            BigDecimal cb = bottleneckRequiredQty(b, availableStock);
            cmp = ca.compareTo(cb); // menor consumo primeiro
            if (cmp != 0) return cmp;

            Long ia = (a == null || a.getId() == null) ? Long.MAX_VALUE : a.getId();
            Long ib = (b == null || b.getId() == null) ? Long.MAX_VALUE : b.getId();
            return ia.compareTo(ib);
        });

        List<ProductionPreviewItemDTO> items = new ArrayList<>();
        BigDecimal grandTotal = BigDecimal.ZERO;

        for (Product product : products) {
            if (product == null || product.getMaterials() == null || product.getMaterials().isEmpty()) {
                continue;
            }

            BigDecimal unitPrice = product.getPrice();
            if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) < 0) {
                throw new BadRequestException("Invalid product price for product id: " + product.getId());
            }

            long maxProducible = calculateMaxProducible(product, availableStock);
            if (maxProducible <= 0L) {
                continue;
            }

            consumeStock(product, maxProducible, availableStock);

            BigDecimal totalValue = unitPrice.multiply(BigDecimal.valueOf(maxProducible));

            items.add(new ProductionPreviewItemDTO(
                    product.getId(),
                    product.getCode(),
                    product.getName(),
                    maxProducible,
                    unitPrice,
                    totalValue
            ));

            grandTotal = grandTotal.add(totalValue);
        }

        return new ProductionPreviewResponseDTO(items, grandTotal);
    }

    /**
     * score = unitPrice / requiredQuantity
     */
    private BigDecimal scoreByBottleneckEfficiency(Product product, Map<Long, BigDecimal> availableStock) {
        if (product == null || product.getMaterials() == null || product.getMaterials().isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal unitPrice = product.getPrice();
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        ProductMaterial bottleneck = findBottleneckMaterial(product, availableStock);
        if (bottleneck == null) return BigDecimal.ZERO;

        BigDecimal req = bottleneck.getRequiredQuantity();
        if (req == null || req.compareTo(BigDecimal.ZERO) <= 0) return BigDecimal.ZERO;

        return unitPrice.divide(req, 12, RoundingMode.HALF_UP);
    }

    private BigDecimal bottleneckRequiredQty(Product product, Map<Long, BigDecimal> availableStock) {
        ProductMaterial bottleneck = findBottleneckMaterial(product, availableStock);
        if (bottleneck == null || bottleneck.getRequiredQuantity() == null) return new BigDecimal("999999999");
        return bottleneck.getRequiredQuantity();
    }

    /**
     * (stock / requiredQuantity)
     */
    private ProductMaterial findBottleneckMaterial(Product product, Map<Long, BigDecimal> availableStock) {
        if (product == null || product.getMaterials() == null) return null;

        ProductMaterial bottleneck = null;
        BigDecimal worstRatio = null;

        for (ProductMaterial pm : product.getMaterials()) {
            if (pm == null || pm.getRawMaterial() == null || pm.getRawMaterial().getId() == null) {
                continue;
            }

            BigDecimal req = pm.getRequiredQuantity();
            if (req == null || req.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            Long rawId = pm.getRawMaterial().getId();
            BigDecimal stock = availableStock.getOrDefault(rawId, BigDecimal.ZERO);

            BigDecimal ratio = stock.divide(req, 12, RoundingMode.HALF_UP);

            if (worstRatio == null || ratio.compareTo(worstRatio) < 0) {
                worstRatio = ratio;
                bottleneck = pm;
            }
        }

        return bottleneck;
    }

    private long calculateMaxProducible(Product product, Map<Long, BigDecimal> availableStock) {
        long maxProducible = Long.MAX_VALUE;

        for (ProductMaterial productMaterial : product.getMaterials()) {
            if (productMaterial == null || productMaterial.getRawMaterial() == null || productMaterial.getRawMaterial().getId() == null) {
                return 0L;
            }

            BigDecimal requiredQuantity = productMaterial.getRequiredQuantity();
            if (requiredQuantity == null || requiredQuantity.compareTo(BigDecimal.ZERO) <= 0) {
                throw new BadRequestException("Invalid requiredQuantity for product material.");
            }

            BigDecimal stock = availableStock.getOrDefault(productMaterial.getRawMaterial().getId(), BigDecimal.ZERO);
            BigDecimal possible = stock.divide(requiredQuantity, 0, RoundingMode.DOWN);

            long possibleQuantity;
            try {
                possibleQuantity = possible.longValueExact();
            } catch (ArithmeticException ex) {
                throw new BadRequestException("Computed quantity is out of range.");
            }

            if (possibleQuantity < maxProducible) {
                maxProducible = possibleQuantity;
            }
            if (maxProducible == 0L) {
                return 0L;
            }
        }

        return maxProducible == Long.MAX_VALUE ? 0L : maxProducible;
    }

    private void consumeStock(Product product, long quantity, Map<Long, BigDecimal> availableStock) {
        for (ProductMaterial productMaterial : product.getMaterials()) {
            if (productMaterial == null || productMaterial.getRawMaterial() == null || productMaterial.getRawMaterial().getId() == null) {
                continue;
            }

            BigDecimal requiredQuantity = productMaterial.getRequiredQuantity();
            if (requiredQuantity == null || requiredQuantity.compareTo(BigDecimal.ZERO) <= 0) {
                throw new BadRequestException("Invalid requiredQuantity for product material.");
            }

            Long rawMaterialId = productMaterial.getRawMaterial().getId();
            BigDecimal currentStock = availableStock.getOrDefault(rawMaterialId, BigDecimal.ZERO);
            BigDecimal consumed = requiredQuantity.multiply(BigDecimal.valueOf(quantity));
            BigDecimal newStock = currentStock.subtract(consumed);

            if (newStock.compareTo(BigDecimal.ZERO) < 0) {
                throw new WebApplicationException("Insufficient stock while executing production.", Response.Status.CONFLICT);
            }

            availableStock.put(rawMaterialId, newStock);
        }
    }
}