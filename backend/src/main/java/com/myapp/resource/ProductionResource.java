package com.myapp.resource;

import com.myapp.dto.ProductionPreviewResponseDTO;
import com.myapp.service.ProductionService;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/production")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProductionResource {

    @Inject
    ProductionService service;

    @GET
    @Path("/preview")
    public ProductionPreviewResponseDTO preview() {
        return service.previewProduction();
    }

    @POST
    @Path("/execute")
    public ProductionPreviewResponseDTO execute() {
        return service.executeProduction();
    }
}