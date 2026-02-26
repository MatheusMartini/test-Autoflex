package com.myapp.resource;

import com.myapp.dto.ProductMaterialRequestDTO;
import com.myapp.dto.ProductMaterialResponseDTO;
import com.myapp.service.ProductMaterialService;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
@Path("/product-materials")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProductMaterialResource {

    @Inject
    ProductMaterialService service;

    @POST
    public ProductMaterialResponseDTO create(ProductMaterialRequestDTO dto) {
        return service.create(dto);
    }

    @PUT
    @Path("/{id}")
    public ProductMaterialResponseDTO update(@PathParam("id") Long id,
    ProductMaterialRequestDTO dto) {
        return service.update(id, dto);
    }

    @GET
    public List<ProductMaterialResponseDTO> list() {
        return service.listAll();
    }

    @GET
    @Path("/{id}")
    public ProductMaterialResponseDTO findById(@PathParam("id") Long id) {
        return service.findById(id);
    }

    @DELETE
    @Path("/{id}")
    public void delete(@PathParam("id") Long id) {
        service.delete(id);
    }
}