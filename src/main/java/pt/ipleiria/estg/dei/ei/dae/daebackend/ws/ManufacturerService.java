package pt.ipleiria.estg.dei.ei.dae.daebackend.ws;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MediaType;
import pt.ipleiria.estg.dei.ei.dae.daebackend.dtos.ManufacturerDTO;
import pt.ipleiria.estg.dei.ei.dae.daebackend.ejbs.ManufacturerBean;
import pt.ipleiria.estg.dei.ei.dae.daebackend.entities.Manufacturer;
import pt.ipleiria.estg.dei.ei.dae.daebackend.exceptions.MyEntityNotFoundException;
import pt.ipleiria.estg.dei.ei.dae.daebackend.security.Authenticated;

import java.util.List;
import java.util.stream.Collectors;

@Path("manufacturers")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@Authenticated
@RolesAllowed({"Manufacturer"})
public class ManufacturerService {
    @EJB
    private ManufacturerBean manufacturerBean;

    private ManufacturerDTO toDTO(Manufacturer manufacturer) {
        return new ManufacturerDTO(
                manufacturer.getUsername(),
                manufacturer.getPassword(),
                manufacturer.getName(),
                manufacturer.getEmail(),
                manufacturer.getIndustry(),
                manufacturer.getLocation(),
                manufacturer.getContact()
        );
    }

    private List <ManufacturerDTO> toDTOs(List<Manufacturer> manufacturers) {
        return manufacturers.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @GET
    @Path("/")
    public List<ManufacturerDTO> getAllManufacturers() {
        return toDTOs(manufacturerBean.getAll());
    }

    @GET
    @Path("{username}")
    public Response getManufacturerDetails(@PathParam("username") String username)
            throws MyEntityNotFoundException{
        Manufacturer manufacturer = manufacturerBean.find(username);
        return Response.ok(toDTO(manufacturer)).build();
    }

    @PUT
    @Path("/")
    public Response updateManufacturer(ManufacturerDTO manufacturerDTO)
            throws MyEntityNotFoundException {
        Manufacturer existingManufacturer = manufacturerBean.find(manufacturerDTO.getUsername());

        existingManufacturer.setName(manufacturerDTO.getName());
        existingManufacturer.setEmail(manufacturerDTO.getEmail());
        existingManufacturer.setIndustry(manufacturerDTO.getIndustry());
        existingManufacturer.setLocation(manufacturerDTO.getLocation());
        existingManufacturer.setContact(manufacturerDTO.getContact());

        manufacturerBean.update(existingManufacturer);

        return Response.ok().entity("Manufacturer updated successfully").build();
    }
}
