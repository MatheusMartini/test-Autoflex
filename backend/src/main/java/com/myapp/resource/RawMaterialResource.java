package com.myapp.resource;

import com.myapp.dto.RawMaterialRequestDTO;
import com.myapp.dto.RawMaterialResponseDTO;
import com.myapp.service.RawMaterialService;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@Path("/raw-materials")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RawMaterialResource {

    @Inject
    RawMaterialService service;

    @GET
    public List<RawMaterialResponseDTO> findAll() {
        return service.findAll();
    }

    @GET
    @Path("/{id}")
    public RawMaterialResponseDTO findById(@PathParam("id") @NotNull @Positive Long id) {
        return service.findById(id);
    }

    @POST
    public RawMaterialResponseDTO create(@Valid RawMaterialRequestDTO dto) {
        return service.create(dto);
    }

    @PUT
    @Path("/{id}")
    public RawMaterialResponseDTO update(@PathParam("id") @NotNull @Positive Long id, @Valid RawMaterialRequestDTO dto) {
        return service.update(id, dto);
    }

    @DELETE
    @Path("/{id}")
    public void delete(@PathParam("id") @NotNull @Positive Long id) {
        service.delete(id);
    }
}
