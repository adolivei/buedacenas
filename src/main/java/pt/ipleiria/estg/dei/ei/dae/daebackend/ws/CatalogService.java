package pt.ipleiria.estg.dei.ei.dae.daebackend.ws;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pt.ipleiria.estg.dei.ei.dae.daebackend.dtos.CatalogDTO;
import pt.ipleiria.estg.dei.ei.dae.daebackend.dtos.OrderDTO;
import pt.ipleiria.estg.dei.ei.dae.daebackend.dtos.ProductDTO;
import pt.ipleiria.estg.dei.ei.dae.daebackend.ejbs.CatalogBean;
import pt.ipleiria.estg.dei.ei.dae.daebackend.ejbs.ManufacturerBean;
import pt.ipleiria.estg.dei.ei.dae.daebackend.entities.Catalog;
import pt.ipleiria.estg.dei.ei.dae.daebackend.entities.Manufacturer;
import pt.ipleiria.estg.dei.ei.dae.daebackend.entities.Order;
import pt.ipleiria.estg.dei.ei.dae.daebackend.entities.Product;
import pt.ipleiria.estg.dei.ei.dae.daebackend.exceptions.MyConstraintViolationException;
import pt.ipleiria.estg.dei.ei.dae.daebackend.exceptions.MyEntityExistsException;
import pt.ipleiria.estg.dei.ei.dae.daebackend.exceptions.MyEntityNotFoundException;
import pt.ipleiria.estg.dei.ei.dae.daebackend.security.Authenticated;

import java.util.List;
import java.util.stream.Collectors;

@Path("catalogs")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@Authenticated
@RolesAllowed({"Manufacturer", "EndConsumer"})
public class CatalogService {
    @EJB
    private CatalogBean catalogBean;
    @EJB
    private ManufacturerBean manufacturerBean;

    private CatalogDTO toDTO(Catalog catalog) {
        CatalogDTO catalogDTO = new CatalogDTO(
                catalog.getCode(),
                catalog.getName(),
                catalog.getManufacturer().getUsername()
        );
        catalogDTO.setProducts(productsToDTOs(catalog.getProducts()));
        return catalogDTO;
    }

    private ProductDTO productToDTO(Product product) {
        return new ProductDTO(
                product.getCode(),
                product.getName(),
                product.getUnit_price(),
                product.getWeight(),
                product.getCatalog().getCode(),
                product.getCatalog().getName(),
                product.getStatus()
        );
    }

    private List<CatalogDTO> toDTOs(List<Catalog> catalogs) {
        return catalogs.stream().map(this::toDTO).collect(Collectors.toList());
    }

    private List<ProductDTO> productsToDTOs(List<Product> products) {
        return products.stream().map(this::productToDTO).collect(Collectors.toList());
    }

    @GET
    @Path("/")
    public List<CatalogDTO> getAllCatalogs() {
        return toDTOs(catalogBean.getAll());
    }

    @POST
    @Path("/")
    public Response createNewCatalog(CatalogDTO catalogDTO) throws
            MyEntityExistsException, MyEntityNotFoundException, MyConstraintViolationException {
        catalogBean.create(
                catalogDTO.getCode(),
                catalogDTO.getName(),
                catalogDTO.getManufacturerUsername()
        );

        Catalog newCatalog = catalogBean.find(catalogDTO.getCode());
        return Response.status(Response.Status.CREATED).entity(toDTO(newCatalog)).build();
    }
    @GET
    @Path("{code}")
    public Response getCatalogDetails(@PathParam("code") long code)
            throws MyEntityNotFoundException{
        Catalog catalog = catalogBean.find(code);

        return Response.ok(toDTO(catalog)).build();
    }

    @PUT
    @Path("/")
    public Response updateCatalog(CatalogDTO catalogDTO)
            throws MyEntityNotFoundException{
        Catalog existingCatalog = catalogBean.find(catalogDTO.getCode());


        Manufacturer manufacturer = manufacturerBean.find(catalogDTO.getManufacturerUsername());

        existingCatalog.setName(catalogDTO.getName());
        existingCatalog.setManufacturer(manufacturer);

        catalogBean.updateCatalog(existingCatalog);

        return Response.ok().entity("Catalog updated successfully").build();
    }

    @DELETE
    @Path("{code}")
    public Response deleteCatalog(@PathParam("code") long code)
            throws MyEntityNotFoundException {
        Catalog catalog = catalogBean.find(code);


        var products = catalog.getProducts();
        for (var product : products) {
            catalog.removeProduct(product);
            product.removeCatalog();
        }
        catalogBean.deleteCatalog(code);
        return Response.ok().entity("Catalog deleted successfully").build();
    }

    @GET
    @Path("/{code}/products")
    public Response getCatalogProducts(@PathParam("code") long catalogCode)
            throws MyEntityNotFoundException{

        Catalog catalog = catalogBean.getCatalogProducts(catalogCode);

        List<ProductDTO> dtos = productsToDTOs(catalog.getProducts());
        return Response.ok(dtos).build();
    }
}
