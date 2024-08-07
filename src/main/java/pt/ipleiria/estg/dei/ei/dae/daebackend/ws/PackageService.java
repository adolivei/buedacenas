package pt.ipleiria.estg.dei.ei.dae.daebackend.ws;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pt.ipleiria.estg.dei.ei.dae.daebackend.dtos.*;
import pt.ipleiria.estg.dei.ei.dae.daebackend.ejbs.PackageBean;
import pt.ipleiria.estg.dei.ei.dae.daebackend.ejbs.ProductBean;
import pt.ipleiria.estg.dei.ei.dae.daebackend.entities.*;
import pt.ipleiria.estg.dei.ei.dae.daebackend.entities.Package;
import pt.ipleiria.estg.dei.ei.dae.daebackend.exceptions.MyConstraintViolationException;
import pt.ipleiria.estg.dei.ei.dae.daebackend.exceptions.MyEntityExistsException;
import pt.ipleiria.estg.dei.ei.dae.daebackend.exceptions.MyEntityNotFoundException;
import pt.ipleiria.estg.dei.ei.dae.daebackend.security.Authenticated;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Path("packages")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@Authenticated
@RolesAllowed({"Manufacturer", "LogisticOperator", "EndConsumer"})
public class PackageService {

    @EJB
    private PackageBean packageBean;
    @EJB
    private ProductBean productBean;

    private PackageDTO toDTO(Package pack) {
        PackageDTO packageDTO = new PackageDTO(
                pack.getId(),
                pack.getType(),
                pack.getMaterial(),
                pack.getWeight(),
                pack.getDimensions()
        );
        packageDTO.setOrders(ordersToDTOs(pack.getOrders()));
        packageDTO.setProducts(ProductsToDTOs(pack.getProducts()));
        return packageDTO;
    }

    private OrderDTO toDTO(Order order) {
        OrderDTO orderDTO = new OrderDTO(
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
        if (order.getLogisticOperator() != null)
            orderDTO.setLogisticOperator(toDTO(order.getLogisticOperator()));
        return orderDTO;
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

    private SensorDTO toDTO(Sensor sensor) {
        SensorDTO  sensorDTO = new SensorDTO(
                sensor.getId(),
                sensor.getType(),
                sensor.getLocation(),
                sensor.getTimestamp(),
                sensor.getStatus()
        );
        sensorDTO.setMeasurements(measurementsToDTOs(sensor.getMeasurements()));
        return sensorDTO;
    }

    private MeasurementDTO measurementToDTO(Measurement measurement) {
        return new MeasurementDTO(
                measurement.getId(),
                measurement.getType(),
                measurement.getValue(),
                measurement.getUnit(),
                measurement.getSensor().getId()
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

    private List<PackageDTO> toDTOs(List<Package> packages) {
        return packages.stream().map(this::toDTO).collect(Collectors.toList());
    }

    private List<ProductDTO> ProductstoDTOs(List<Product> products) {
        return products.stream().map(this::toDTO).collect(Collectors.toList());
    }

    private List<SensorDTO> sensorsToDTOs(List<Sensor> sensors) {
        return sensors.stream().map(this::toDTO).collect(Collectors.toList());
    }

    private List<MeasurementDTO> measurementsToDTOs(List<Measurement> measurements) {
        return measurements.stream().map(this::measurementToDTO).collect(Collectors.toList());
    }

    private List<OrderDTO> ordersToDTOs(List<Order> orders) {
        return orders.stream().map(this::toDTO).collect(Collectors.toList());
    }

    private List<ProductDTO> ProductsToDTOs(List<Product> products) {
        return products.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @GET
    @Path("/")
    public List<PackageDTO> getAllPackages() {
        return toDTOs(packageBean.getAll());
    }

    @GET
    @Path("{id}")
    public Response getPackageDetails(@PathParam("id") int id)
            throws MyEntityNotFoundException{
        Package pack = packageBean.find(id);

        return Response.ok(toDTO(pack)).build();
    }

    @POST
    @Path("/")
    public Response createNewPackage(PackageDTO packageDTO)
            throws MyEntityExistsException, MyConstraintViolationException {
        Package newPackage = packageBean.create(
                packageDTO.getType(),
                packageDTO.getMaterial(),
                packageDTO.getWeight(),
                packageDTO.getDimensions()
        );

        return Response.status(Response.Status.CREATED).entity(toDTO(newPackage)).build();
    }

    @PUT
    @Path("/")
    public Response updatePackage(PackageDTO packageDTO)
            throws MyEntityNotFoundException, MyConstraintViolationException{
        Package existingPackage = packageBean.find(packageDTO.getId());

        existingPackage.setType(packageDTO.getType());
        existingPackage.setMaterial(packageDTO.getMaterial());
        existingPackage.setWeight(packageDTO.getWeight());
        existingPackage.setDimensions(packageDTO.getDimensions());

        packageBean.updatePackage(existingPackage);

        return Response.ok().entity("Package updated successfully").build();
    }

    @DELETE
    @Path("/{id}")
    public Response deletePackage(@PathParam("id") int id)
            throws MyEntityNotFoundException{

        packageBean.deletePackage(id);

        return Response.ok().entity("Package deleted successfully").build();
    }

    @GET
    @Path("{id}/sensors")
    public Response getSensorsInPackage(@PathParam("id") int id)
            throws MyEntityNotFoundException{
        Package pack = packageBean.getPackageSensors(id);
        List<SensorDTO> dtos = sensorsToDTOs(pack.getSensors());

        return Response.ok(dtos).build();
    }

    @DELETE
    @Path("{packageId}/sensors/{sensorId}/remove")
    public Response removeSensorFromPackage(@PathParam("packageId") int packageId, @PathParam("sensorId") long sensorId) {
        try {
            packageBean.unenrollSensorInPackage(packageId, sensorId);
            return Response.ok("Sensor removed from package successfully").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error removing sensor from package").build();
        }
    }

    @GET
    @Path("{id}/products/")
    public Response getProductsFromPackage(@PathParam("id") int id)
            throws MyEntityNotFoundException{
        var pack = packageBean.find(id);
        return Response.ok(ProductsToDTOs(pack.getProducts())).build();
    }

    @GET
    @Path("{id}/products/{code}")
    public Response getProductsFromPackage(@PathParam("id") int id, @PathParam("code") long code)
            throws MyEntityNotFoundException{
        var pack = packageBean.find(id);
        var products = pack.getProducts();
        for (var p : products) {
            if (p.getCode() == code) {
                return Response.ok(toDTO(p)).build();
            }
        }
        return Response.status(Response.Status.NOT_FOUND)
                .entity("Product with code " + code + " not found in package with id: " + id)
                .build();
    }

    @PATCH
    @Path("{id}/products/{code}/add")
    public Response addProductToPackage(@PathParam("id") int id, @PathParam("code") long code)
            throws MyEntityNotFoundException{
        packageBean.addProductToPackage(code,id);
        return Response.ok("Product successfully added").build();
    }

    @PATCH
    @Path("{id}/products/{code}/remove")
    public Response removeProductFromPackage(@PathParam("id") int id, @PathParam("code") long code)
            throws MyEntityNotFoundException{
        var pack = packageBean.find(id);
        var products = pack.getProducts();
        for (var p : products) {
            if (p.getCode() == code) {
                packageBean.removeProductFromPackage(code, id);
                return Response.ok("Product successfully removed").build();
            }
        }
        return Response.status(Response.Status.NOT_FOUND)
                .entity("Product with code " + code + " not found in package with id: " + id)
                .build();
    }
}