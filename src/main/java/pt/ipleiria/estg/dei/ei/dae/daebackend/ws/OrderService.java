package pt.ipleiria.estg.dei.ei.dae.daebackend.ws;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pt.ipleiria.estg.dei.ei.dae.daebackend.dtos.*;
import pt.ipleiria.estg.dei.ei.dae.daebackend.ejbs.LogisticOperatorBean;
import pt.ipleiria.estg.dei.ei.dae.daebackend.ejbs.OrderBean;
import pt.ipleiria.estg.dei.ei.dae.daebackend.ejbs.PackageBean;
import pt.ipleiria.estg.dei.ei.dae.daebackend.ejbs.ProductBean;
import pt.ipleiria.estg.dei.ei.dae.daebackend.entities.*;
import pt.ipleiria.estg.dei.ei.dae.daebackend.entities.Package;
import pt.ipleiria.estg.dei.ei.dae.daebackend.enums.OrderDelivery;
import pt.ipleiria.estg.dei.ei.dae.daebackend.enums.PackageType;
import pt.ipleiria.estg.dei.ei.dae.daebackend.enums.ProductStatus;
import pt.ipleiria.estg.dei.ei.dae.daebackend.exceptions.MyConstraintViolationException;
import pt.ipleiria.estg.dei.ei.dae.daebackend.exceptions.MyEntityExistsException;
import pt.ipleiria.estg.dei.ei.dae.daebackend.exceptions.MyEntityNotFoundException;
import pt.ipleiria.estg.dei.ei.dae.daebackend.exceptions.MyIllegalArgumentException;
import pt.ipleiria.estg.dei.ei.dae.daebackend.security.Authenticated;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Path("orders")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@Authenticated
@RolesAllowed({"LogisticOperator","EndConsumer"})
public class OrderService {
	@EJB
	private OrderBean orderBean;
	@EJB
	private ProductBean productBean;
	@EJB
	private PackageBean packageBean;
	@EJB
	private LogisticOperatorBean logisticOperatorBean;

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

	private List<OrderDTO> toDTOs(List<Order> orders) {
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
	public List<OrderDTO> getAllOrders() {
		return toDTOs(orderBean.getAll());
	}

	@GET
	@Path("{id}")
	public Response getOrderDetails(@PathParam("id") int id)
			throws MyEntityNotFoundException{
		Order order = orderBean.find(id);
		return Response.ok(toDTO(order)).build();
	}

	@POST
	@Path("/")
	public Response createNewOrder(OrderDTO orderDTO)
			throws MyEntityNotFoundException, MyEntityExistsException, MyConstraintViolationException {
		Order newOrder = orderBean.create(
				orderDTO.getPayment_method(),
				orderDTO.getEndConsumer().getUsername()
		);
		return Response.status(Response.Status.CREATED).entity(toDTO(newOrder)).build();
	}

	@PUT
	@Path("/")
	public Response updateOrder(OrderDTO updatedOrderDTO)
			throws MyEntityNotFoundException, MyConstraintViolationException{
		Order order = orderBean.find(updatedOrderDTO.getId());

		orderBean.update(
				updatedOrderDTO.getId(),
				updatedOrderDTO.getShipping_cost(),
				updatedOrderDTO.getDelivery_date(),
				updatedOrderDTO.getOrder_date(),
				updatedOrderDTO.getTotal_amount(),
				updatedOrderDTO.getPayment_method()
		);
		return Response.ok(toDTO(order)).build();
	}

	@DELETE
	@Path("{id}")
	public Response deleteOrder(@PathParam("id") int id)
			throws MyEntityNotFoundException, MyConstraintViolationException{
		Order order = orderBean.find(id);

		var packages = order.getPackages();
		if (!packages.isEmpty()) {
			for (var pack : packages) {
				orderBean.removePackageFromOrder(order.getId(), pack.getId());
			}
		}
		var products = order.getProducts();
		if (!products.isEmpty()) {
			for (var product : products) {
				orderBean.removeProductFromOrder(order.getId(), product.getCode());
			}
		}
		orderBean.delete(id);
		return Response.status(Response.Status.OK)
				.entity("Order successfully deleted")
				.build();
	}

	@PATCH
	@Path("/{id}/logisticOperator/{username}/add")
	public Response addLogisticOperatorToOrder(@PathParam("id") int id, @PathParam("username") String username)
			throws MyEntityNotFoundException, MyConstraintViolationException{
		Order order = orderBean.find(id);
		if (order.getProducts().isEmpty()) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity("The order has no products, it can't be accepted")
					.build();
		}
		orderBean.addLogicticOperatorToOrder(id, username);
		return Response.status(Response.Status.OK)
				.entity("Logistic Operator successfully added")
				.build();
	}

	@PATCH
	@Path("/{id}/logisticOperator/{username}/remove")
	public Response removeLogisticOperatorToOrder(@PathParam("id") int id, @PathParam("username") String username)
			throws MyEntityNotFoundException, MyConstraintViolationException{
		Order order = orderBean.find(id);
		var logisticOperator = logisticOperatorBean.find(username);
		var currentLogisticOperator = logisticOperatorBean.find(order.getLogisticOperator().getUsername());
		if (currentLogisticOperator == null || !currentLogisticOperator.getUsername().equals(logisticOperator.getUsername())) {
			return Response.status(Response.Status.FORBIDDEN)
					.entity("ERROR_CURRENT_LOGISTIC_OPERATOR_NOT_THE_SAME")
					.build();
		}
		orderBean.removeLogicticOperatorFromOrder(id, username);
		return Response.status(Response.Status.OK)
				.entity("Logistic Operator successfully removed")
				.build();
	}

	@PATCH
	@Path("/{id}/products/{code}/add")
	public Response addProductToOrder(@PathParam("id") int id, @PathParam("code") long code)
			throws MyEntityNotFoundException, MyConstraintViolationException {
		var product = productBean.find(code);
		if (product.getStatus().equals(ProductStatus.SOLD)) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity("That product is SOLD")
					.build();
		}
		productBean.setStatus(code, ProductStatus.SOLD);
		orderBean.addProductToOrder(id, code);
		return Response.status(Response.Status.OK)
				.entity("Product successfully added")
				.build();
	}

	@PATCH
	@Path("/{id}/products/{code}/remove")
	public Response removeProductFromOrder(@PathParam("id") int id, @PathParam("code") long code)
			throws MyEntityNotFoundException, MyConstraintViolationException{
		Order order = orderBean.find(id);
		var products = order.getProducts();
		for (var p : products) {
			if (p.getCode() == code) {
				productBean.setStatus(code, ProductStatus.AVAILABLE);
				orderBean.removeProductFromOrder(id, code);
				return Response.status(Response.Status.OK)
						.entity("Product successfully removed")
						.build();
			}
		}
		return Response.status(Response.Status.NOT_FOUND)
				.entity("ERROR_FINDING_PRODUCT_CODE " + code)
				.build();
	}

	@GET
	@Path("/{id}/products")
	public Response getProductsFromOrder(@PathParam("id") int id)
			throws MyEntityNotFoundException{
		Order order = orderBean.find(id);
		return Response.ok(ProductsToDTOs(order.getProducts())).build();
	}

	@GET
	@Path("/{id}/products/{code}")
	public Response getProductFromOrder(@PathParam("id") int id, @PathParam("code") long code)
			throws MyEntityNotFoundException{
		Order order = orderBean.find(id);
		for (var product : order.getProducts()) {
			if (product.getCode() == code) {
				return Response.ok(toDTO(product))
						.build();
			}
		}
		return Response.status(Response.Status.NOT_FOUND)
				.entity("ERROR_FINDING_PRODUCT: " + code)
				.build();
	}

	@GET
	@Path("/{id}/packages")
	public Response getPackagesFromOrder(@PathParam("id") int id)
			throws MyEntityNotFoundException{
		Order order = orderBean.find(id);
		return Response.ok(PackagesToDTOs(order.getPackages()))
				.build();
	}

	@GET
	@Path("/{id}/packages/{packID}")
	public Response getPackageFromOrder(@PathParam("id") int id, @PathParam("packID") int packID)
			throws MyEntityNotFoundException{
		Order order = orderBean.find(id);
		for (var pack : order.getPackages()) {
			if (pack.getId() == packID) {
				return Response.ok(toDTO(pack))
						.build();
			}
		}
		return Response.status(Response.Status.NOT_FOUND)
				.entity("ERROR_FINDING_PACKAGE: " + packID)
				.build();
	}

	@PATCH
	@Path("/{id}/packages/{packageId}/add")
	public Response addPackageToOrder(@PathParam("id") int id, @PathParam("packageId") int packageId)
			throws MyEntityNotFoundException, MyConstraintViolationException{
		Package pack = packageBean.find(packageId);

		if (pack.getType().equals(PackageType.PRIMARY)) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity("Primary Packages cannot be directly added to orders, you need to buy the corresponding product")
					.build();
		}
		orderBean.addPackageToOrder(id, packageId);
		return Response.status(Response.Status.OK)
				.entity("Package successfully added")
				.build();
	}

	@PATCH
	@Path("/{id}/packages/{packageId}/remove")
	public Response removePackageFromOrder(@PathParam("id") int id, @PathParam("packageId") int packageId)
			throws MyEntityNotFoundException, MyConstraintViolationException {
		orderBean.removePackageFromOrder(id, packageId);
		return Response.status(Response.Status.OK)
				.entity("Package successfully removed")
				.build();
	}

	@PATCH
	@Path("/{id}/deliver")
	public Response markOrderAsDelivered(@PathParam("id") int id)
			throws MyEntityNotFoundException, MyConstraintViolationException, MyIllegalArgumentException {
		orderBean.setDeliveryStatus(id, OrderDelivery.DELIVERED);
		return Response.status(Response.Status.OK)
				.entity("Order successfully delivered")
				.build();
	}

	@PATCH
	@Path("/{id}/shipped")
	public Response markOrderAsShipped(@PathParam("id") int id)
			throws MyEntityNotFoundException, MyConstraintViolationException, MyIllegalArgumentException{
		orderBean.setDeliveryStatus(id, OrderDelivery.SHIPPED);
		return Response.status(Response.Status.OK)
				.entity("Order successfully delivered")
				.build();
	}

	// the shipping cost will automatically be 1% of the total order amount
//	@PATCH
//	@Path("/{id}/shippingCost")
//	public Response setShippingCost(@PathParam("id") int id, OrderDTO orderDTO) {
//		Order order = orderBean.find(id);
//		if (order == null) {
//			return Response.status(Response.Status.NOT_FOUND)
//					.entity("ERROR_FINDING_ORDER_ID: " + id)
//					.build();
//		}
//		orderBean.setShippingCost(id, orderDTO.getShipping_cost());
//		return Response.status(Response.Status.OK)
//				.entity("Shipping Cost successfully changed")
//				.build();
//	}


}
