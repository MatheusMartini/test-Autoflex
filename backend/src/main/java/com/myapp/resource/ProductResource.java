package com.myapp.resource;

import com.myapp.dto.ProductRequestDTO;
import com.myapp.dto.ProductResponseDTO;
import com.myapp.service.ProductService;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/products")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProductResource {

    @Inject
    ProductService service;

    @GET
    public List<ProductResponseDTO> findAll() {
        return service.findAll();
    }

    @GET
    @Path("/{id}")
    public ProductResponseDTO findById(@PathParam("id") Long id) {
        return service.findById(id);
    }

    @POST
    public ProductResponseDTO create(ProductRequestDTO dto) {
        return service.create(dto);
    }

    @PUT
    @Path("/{id}")
    public ProductResponseDTO update(@PathParam("id") Long id, ProductRequestDTO dto) {
        return service.update(id, dto);
    }

    @DELETE
    @Path("/{id}")
    public void delete(@PathParam("id") Long id) {
        service.delete(id);
    }
}