package com.myapp.resource;

import com.myapp.dto.ProductionResponseDTO;
import com.myapp.service.ProductionService;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/production")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProductionResource {

    @Inject
    ProductionService service;

    // PREVIEW - does not change stock
    @GET
    @Path("/preview")
    public List<ProductionResponseDTO> preview() {
        return service.calculateProduction(false);
    }

    // EXECUTE - deducts stock
    @POST
    @Path("/execute")
    public List<ProductionResponseDTO> execute() {
        return service.calculateProduction(true);
    }
}