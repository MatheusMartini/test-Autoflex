package com.myapp.resource;

import com.myapp.dto.ProductMaterialRequestDTO;
import com.myapp.service.ProductMaterialService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;


@Path("/product-materials")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProductMaterialResource {

    @Inject
    ProductMaterialService service;

    @POST
    public void create(ProductMaterialRequestDTO dto) {
        service.create(dto);
    }
}