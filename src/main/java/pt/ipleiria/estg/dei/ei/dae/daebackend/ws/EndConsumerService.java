package pt.ipleiria.estg.dei.ei.dae.daebackend.ws;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MediaType;
import org.hibernate.Hibernate;
import pt.ipleiria.estg.dei.ei.dae.daebackend.dtos.*;
import pt.ipleiria.estg.dei.ei.dae.daebackend.ejbs.EndConsumerBean;
import pt.ipleiria.estg.dei.ei.dae.daebackend.entities.*;
import pt.ipleiria.estg.dei.ei.dae.daebackend.entities.Package;
import pt.ipleiria.estg.dei.ei.dae.daebackend.exceptions.MyEntityNotFoundException;
import pt.ipleiria.estg.dei.ei.dae.daebackend.security.Authenticated;

import java.time.OffsetDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Path("endConsumers")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@Authenticated
@RolesAllowed({"EndConsumer","LogisticOperator"})
public class EndConsumerService {
    @EJB
    private EndConsumerBean endConsumerBean;

    private EndConsumerDTO toDTO(EndConsumer endConsumer) {
        EndConsumerDTO endConsumerDTO =  new EndConsumerDTO(
                endConsumer.getUsername(),
                endConsumer.getPassword(),
                endConsumer.getName(),
                endConsumer.getEmail(),
                endConsumer.getAddress(),
                endConsumer.getPhoneNumber()
        );
        endConsumerDTO.setOrders(OrdersToDTOs(endConsumer.getOrders()));
        return endConsumerDTO;
    }

    private OrderDTO toDTO(Order order) {
        OrderDTO orderDTO =  new OrderDTO(
                order.getId(),
                order.getPayment_method(),
                order.getTotal_amount(),
                order.getShipping_cost(),
                order.getDelivery_date(),
                order.getOrder_date(),
                order.getDelivery_status()
        );
        orderDTO.setProducts(ProductsToDTOs(order.getProducts()));
        orderDTO.setPackages(PackagesToDTOs(order.getPackages()));
        if (order.getLogisticOperator() != null)
            orderDTO.setLogisticOperator(toDTO(order.getLogisticOperator()));
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

    private LogisticOperatorDTO toDTO(LogisticOperator logisticOperator) {
        return new LogisticOperatorDTO(
                logisticOperator.getUsername(),
                logisticOperator.getPassword(),
                logisticOperator.getName(),
                logisticOperator.getEmail(),
                logisticOperator.getCompany(),
                logisticOperator.getVehicleType(),
                logisticOperator.getLicensePlate(),
                logisticOperator.getContact()
        );
    }

    private List<EndConsumerDTO> toDTOs(List<EndConsumer> endConsumers) {
        return endConsumers.stream().map(this::toDTO).collect(Collectors.toList());
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
    public List<EndConsumerDTO> getAllEndConsumers() {
        return toDTOs(endConsumerBean.getAll());
    }

    @GET
    @Path("{username}")
    public Response getEndConsumerDetails(@PathParam("username") String username)
            throws MyEntityNotFoundException{
        EndConsumer endConsumer = endConsumerBean.find(username);

        return Response.ok(toDTO(endConsumer)).build();
    }

    @GET
    @Path("{username}/orders")
    public Response getEndConsumerOrders(@PathParam("username") String username)
            throws MyEntityNotFoundException{
        EndConsumer endConsumer = endConsumerBean.find(username);
        List<Order> orders = endConsumer.getOrders();
        if (orders == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("EndConsumer with username " + username + " has no orders")
                    .build();
        }
        return Response.ok(OrdersToDTOs(orders)).build();
    }

    @PUT
    @Path("/")
    public Response updateEndConsumer(EndConsumerDTO endConsumerDTO)
            throws MyEntityNotFoundException {
        EndConsumer existingEndConsumer = endConsumerBean.find(endConsumerDTO.getUsername());

        existingEndConsumer.setName(endConsumerDTO.getName());
        existingEndConsumer.setEmail(endConsumerDTO.getEmail());
        existingEndConsumer.setAddress(endConsumerDTO.getAddress());
        existingEndConsumer.setPhoneNumber(endConsumerDTO.getPhoneNumber());

        endConsumerBean.update(existingEndConsumer);

        return Response.ok().entity("EndConsumer updated successfully").build();
    }
}
