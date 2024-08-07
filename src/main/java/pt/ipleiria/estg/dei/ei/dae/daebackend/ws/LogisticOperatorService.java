package pt.ipleiria.estg.dei.ei.dae.daebackend.ws;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MediaType;
import pt.ipleiria.estg.dei.ei.dae.daebackend.dtos.*;
import pt.ipleiria.estg.dei.ei.dae.daebackend.ejbs.LogisticOperatorBean;
import pt.ipleiria.estg.dei.ei.dae.daebackend.entities.*;
import pt.ipleiria.estg.dei.ei.dae.daebackend.entities.Package;
import pt.ipleiria.estg.dei.ei.dae.daebackend.exceptions.MyEntityNotFoundException;
import pt.ipleiria.estg.dei.ei.dae.daebackend.security.Authenticated;

import java.util.List;
import java.util.stream.Collectors;

@Path("logisticOperators")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@Authenticated
@RolesAllowed({"LogisticOperator"})
public class LogisticOperatorService {
    @EJB
    private LogisticOperatorBean logisticOperatorBean;

    private LogisticOperatorDTO toDTO(LogisticOperator logisticOperator) {
        LogisticOperatorDTO logisticOperatorDTO =  new LogisticOperatorDTO(
                logisticOperator.getUsername(),
                logisticOperator.getPassword(),
                logisticOperator.getName(),
                logisticOperator.getEmail(),
                logisticOperator.getCompany(),
                logisticOperator.getVehicleType(),
                logisticOperator.getLicensePlate(),
                logisticOperator.getContact()
        );
        logisticOperatorDTO.setOrders(OrdersToDTOs(logisticOperator.getOrders()));
        return logisticOperatorDTO;
    }

    private OrderDTO toDTO(Order order) {
        OrderDTO orderDTO =  new OrderDTO(
                order.getId(),
                order.getPayment_method(),
                toDTO(order.getEndConsumer()),
                order.getTotal_amount(),
                order.getShipping_cost(),
                order.getDelivery_date(),
                order.getOrder_date(),
                order.getDelivery_status()

        );
        orderDTO.setProducts(ProductsToDTOs(order.getProducts()));
        orderDTO.setPackages(PackagesToDTOs(order.getPackages()));
        return orderDTO;
    }

    private PackageDTO toDTO(Package pack) {
        return new PackageDTO(
                pack.getId(),
                pack.getType(),
                pack.getMaterial(),
                pack.getWeight(),
                pack.getDimensions()
        );
    }

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

    private EndConsumerDTO toDTO(EndConsumer endConsumer) {
        return new EndConsumerDTO(
                endConsumer.getUsername(),
                endConsumer.getPassword(),
                endConsumer.getName(),
                endConsumer.getEmail(),
                endConsumer.getAddress(),
                endConsumer.getPhoneNumber()
        );
    }

    private List <LogisticOperatorDTO> toDTOs(List<LogisticOperator> logisticOperators) {
        return logisticOperators.stream().map(this::toDTO).collect(Collectors.toList());
    }

    private List<OrderDTO> OrdersToDTOs(List<Order> orders) {
        return orders.stream().map(this::toDTO).collect(Collectors.toList());
    }

    private List<PackageDTO> PackagesToDTOs(List<Package> packages) {
        return packages.stream().map(this::toDTO).collect(Collectors.toList());
    }

    private List<ProductDTO> ProductsToDTOs(List<Product> products) {
        return products.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @GET
    @Path("/")
    public List<LogisticOperatorDTO> getAllLogisticOperators() {
        return toDTOs(logisticOperatorBean.getAll());
    }

    @GET
    @Path("{username}")
    public Response getLogisticOperatorDetails(@PathParam("username") String username)
            throws MyEntityNotFoundException{
        LogisticOperator logisticOperator = logisticOperatorBean.find(username);

        return Response.ok(toDTO(logisticOperator)).build();
    }

    @PUT
    @Path("/")
    public Response updateLogisticOperator(LogisticOperatorDTO logisticOperatorDTO)
            throws MyEntityNotFoundException {
        LogisticOperator existingLogisticOperator = logisticOperatorBean.find(logisticOperatorDTO.getUsername());

        existingLogisticOperator.setName(logisticOperatorDTO.getName());
        existingLogisticOperator.setEmail(logisticOperatorDTO.getEmail());
        existingLogisticOperator.setCompany(logisticOperatorDTO.getCompany());
        existingLogisticOperator.setVehicleType(logisticOperatorDTO.getVehicleType());
        existingLogisticOperator.setLicensePlate(logisticOperatorDTO.getLicensePlate());
        existingLogisticOperator.setContact(logisticOperatorDTO.getContact());

        logisticOperatorBean.update(existingLogisticOperator);

        return Response.ok().entity("LogisticOperator updated successfully").build();
    }
}
