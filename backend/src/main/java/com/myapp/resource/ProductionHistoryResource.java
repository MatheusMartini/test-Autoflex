package com.myapp.resource;

import com.myapp.dto.ProductionRunDetailDTO;
import com.myapp.dto.ProductionRunSummaryDTO;
import com.myapp.dto.ProductionItemDTO;
import com.myapp.entity.ProductionRun;
import com.myapp.repository.ProductionRunRepository;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/production/runs")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProductionHistoryResource {

    @Inject
    ProductionRunRepository repo;

    @GET
    public List<ProductionRunSummaryDTO> listRuns() {
        List<ProductionRun> runs = repo.findAllOrderByExecutedAtDesc();
        return runs.stream().map(r ->
                new ProductionRunSummaryDTO(
                        r.id,
                        r.executedAt,
                        r.grandTotal,
                        r.items == null ? 0 : r.items.size()
                )
        ).toList();
    }

    @GET
    @Path("/{id}")
    public ProductionRunDetailDTO getRun(@PathParam("id") Long id) {
        ProductionRun run = repo.findById(id);
        if (run == null) throw new NotFoundException("Production run not found.");

        List<ProductionItemDTO> items = run.items.stream().map(i ->
                new ProductionItemDTO(
                        i.product.getId(),
                        i.productCode,
                        i.productName,
                        i.quantity,
                        i.unitPrice,
                        i.totalValue
                )
        ).toList();

        return new ProductionRunDetailDTO(run.id, run.executedAt, run.grandTotal, items);
    }
}