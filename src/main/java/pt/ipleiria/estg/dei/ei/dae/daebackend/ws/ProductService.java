package pt.ipleiria.estg.dei.ei.dae.daebackend.ws;


import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pt.ipleiria.estg.dei.ei.dae.daebackend.dtos.PackageDTO;
import pt.ipleiria.estg.dei.ei.dae.daebackend.dtos.ProductDTO;
import pt.ipleiria.estg.dei.ei.dae.daebackend.dtos.SensorDTO;
import pt.ipleiria.estg.dei.ei.dae.daebackend.ejbs.PackageBean;
import pt.ipleiria.estg.dei.ei.dae.daebackend.ejbs.ProductBean;
import pt.ipleiria.estg.dei.ei.dae.daebackend.entities.Package;
import pt.ipleiria.estg.dei.ei.dae.daebackend.entities.Product;
import pt.ipleiria.estg.dei.ei.dae.daebackend.entities.Sensor;
import pt.ipleiria.estg.dei.ei.dae.daebackend.enums.PackageType;
import pt.ipleiria.estg.dei.ei.dae.daebackend.exceptions.MyEntityExistsException;
import pt.ipleiria.estg.dei.ei.dae.daebackend.exceptions.MyEntityNotFoundException;
import pt.ipleiria.estg.dei.ei.dae.daebackend.exceptions.MyConstraintViolationException;
import pt.ipleiria.estg.dei.ei.dae.daebackend.security.Authenticated;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Path("products")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@Authenticated
@RolesAllowed({"Manufacturer","EndConsumer"})
public class ProductService {
    @EJB
    private ProductBean productBean;
    @EJB
    private PackageBean packageBean;

    private ProductDTO toDTO(Product product) {
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

    private List<ProductDTO> toDTOs(List<Product> products) {
        return products.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @GET
    @Path("/")
    public List<ProductDTO> getAllProducts() {
        return toDTOs(productBean.getAll());
    }

    @GET
    @Path("{code}")
    public Response getProductDetails(@PathParam("code") long code)
            throws MyEntityNotFoundException{
        Product product = productBean.find(code);

        return Response.ok(toDTO(product)).build();
    }

    @POST
    @Path("/")
    public Response createNewProduct(ProductDTO productDTO)
            throws MyEntityExistsException, MyEntityNotFoundException, MyConstraintViolationException{
        productBean.create(
                productDTO.getCode(),
                productDTO.getName(),
                productDTO.getUnit_price(),
                productDTO.getWeight(),
                productDTO.getCatalogCode()
        );
        //packageBean.create(PackageType.PRIMARY, productDTO.getName() + "_BOX", productDTO.getWeight() + 0.5, )
        Product newProduct = productBean.find(productDTO.getCode());
        return Response.status(Response.Status.CREATED).entity(toDTO(newProduct)).build();
    }

    @PUT
    @Path("/")
    public Response updateProduct(ProductDTO productDTO)
            throws MyEntityNotFoundException {
            Product existingProduct = productBean.find(productDTO.getCode());

            existingProduct.setName(productDTO.getName());
            existingProduct.setUnit_price(productDTO.getUnit_price());
            existingProduct.setWeight(productDTO.getWeight());
            productBean.updateProduct(existingProduct);
            return Response.ok().entity("Product updated successfully").build();
    }

    @DELETE
    @Path("{code}")
    public Response deleteProduct(@PathParam("code") long code)
            throws MyEntityNotFoundException {
        productBean.deleteProduct(code);
        return Response.ok().entity("Product deleted successfully").build();
    }
}
