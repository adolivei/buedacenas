package pt.ipleiria.estg.dei.ei.dae.daebackend.ejbs;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.hibernate.Hibernate;
import pt.ipleiria.estg.dei.ei.dae.daebackend.dtos.ProductDTO;
import pt.ipleiria.estg.dei.ei.dae.daebackend.entities.*;
import pt.ipleiria.estg.dei.ei.dae.daebackend.entities.Package;
import pt.ipleiria.estg.dei.ei.dae.daebackend.enums.OrderDelivery;
import pt.ipleiria.estg.dei.ei.dae.daebackend.enums.PaymentMethod;
import pt.ipleiria.estg.dei.ei.dae.daebackend.enums.ProductStatus;
import pt.ipleiria.estg.dei.ei.dae.daebackend.exceptions.MyConstraintViolationException;
import pt.ipleiria.estg.dei.ei.dae.daebackend.exceptions.MyEntityExistsException;
import pt.ipleiria.estg.dei.ei.dae.daebackend.exceptions.MyEntityNotFoundException;
import pt.ipleiria.estg.dei.ei.dae.daebackend.exceptions.MyIllegalArgumentException;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Stateless
public class OrderBean {
	@PersistenceContext
	private EntityManager entityManager;

	public boolean exists(int id) {
		Query query = entityManager.createQuery(
				"SELECT COUNT(o.id) FROM Order o WHERE o.id = :id",
				Long.class
		);
		query.setParameter("id", id);
		return (Long)query.getSingleResult() > 0L;
	}

	//String endConsumerUsername
	public Order create(PaymentMethod payment_method, String endConsumerUsername)
			throws MyEntityExistsException, MyConstraintViolationException, MyEntityNotFoundException {
		EndConsumer endConsumer = entityManager.find(EndConsumer.class, endConsumerUsername);
		if (endConsumer == null) {
			throw new MyEntityNotFoundException(
					"End consumer with id '" + endConsumerUsername + "' not found");		}
		try {
			Order order = new Order(payment_method, endConsumer);
			if (exists(order.getId())) {
				throw new MyEntityExistsException(
						"Order with id '" + order.getId() + "' already exists");
			}
			entityManager.persist(order);
			entityManager.flush();
			return order;
		} catch (ConstraintViolationException e) {
			throw new MyConstraintViolationException(e);
		}
	}

	public List<Order> getAll() {
		List<Order> orders = entityManager.createNamedQuery("getAllOrders", Order.class).getResultList();
		for (Order order : orders) {
			Hibernate.initialize(order.getProducts());
			Hibernate.initialize(order.getPackages());
		}
		return orders;
	}


	public Order find(int id)
			throws MyEntityNotFoundException{
		Order order = entityManager.find(Order.class, id);

		if (order == null) {
			throw new MyEntityNotFoundException(
					"Order with id '" + id + "' not found");
		}
		Hibernate.initialize(order.getProducts());
		Hibernate.initialize(order.getPackages());
		return order;
	}

	public void update(int id, double shipping_cost, String delivery_date, String order_date, double total_amount, PaymentMethod payment_method)
			throws MyEntityNotFoundException, MyConstraintViolationException{
		Order order = entityManager.find(Order.class, id);
		if (order == null) {
			throw new MyEntityNotFoundException(
					"Order with id '" + id + "' not found");
		}

		entityManager.lock(order, LockModeType.OPTIMISTIC);
		try {
			order.setShipping_cost(shipping_cost);
			order.setDelivery_date(delivery_date);
			order.setOrder_date(order_date);
			order.setTotal_amount(total_amount);
			order.setPayment_method(payment_method);
		} catch (ConstraintViolationException e) {
			throw new MyConstraintViolationException(e);
		}
	}

	public void delete(int id)
			throws MyEntityNotFoundException, MyConstraintViolationException{
		Order order = entityManager.find(Order.class, id);
		if (order == null) {
			throw new MyEntityNotFoundException(
					"Order with id '" + id + "' not found");
		}
		try{
			entityManager.remove(order);
		} catch (ConstraintViolationException e) {
			throw new MyConstraintViolationException(e);
		}
	}

	public void addPackageToOrder(int orderID, int packageID)
			throws MyEntityNotFoundException, MyConstraintViolationException{
		Order order = entityManager.find(Order.class, orderID);
		if (order == null) {
			throw new MyEntityNotFoundException(
					"Order with id '" + orderID + "' not found");
		}
		Package pack = entityManager.find(Package.class, packageID);
		if (pack == null) {
			throw new MyEntityNotFoundException(
					"Package with id '" + packageID + "' not found");
		}
		try {
			Hibernate.initialize(order.getPackages());
			Hibernate.initialize(pack.getOrders());
			entityManager.lock(order, LockModeType.OPTIMISTIC);
			entityManager.lock(pack, LockModeType.OPTIMISTIC);
			order.addPackage(pack);
			pack.addOrder(order);
		} catch (ConstraintViolationException e) {
			throw new MyConstraintViolationException(e);
		}
	}

	public void removePackageFromOrder(int orderID, int packageID)
			throws MyEntityNotFoundException, MyConstraintViolationException{
		Order order = entityManager.find(Order.class, orderID);
		if (order == null) {
			throw new MyEntityNotFoundException(
					"Order with id '" + orderID + "' not found");
		}
		Package pack = entityManager.find(Package.class, packageID);
		if (pack == null) {
			throw new MyEntityNotFoundException(
					"Package with id '" + packageID + "' not found");
		}
		try {
			Hibernate.initialize(order.getPackages());
			Hibernate.initialize(pack.getOrders());
			entityManager.lock(order, LockModeType.OPTIMISTIC);
			entityManager.lock(pack, LockModeType.OPTIMISTIC);
			order.removePackage(pack);
			pack.removeOrder(order);
		} catch (ConstraintViolationException e) {
			throw new MyConstraintViolationException(e);
		}
	}

	public void addProductToOrder(int orderID, long productCode)
			throws MyEntityNotFoundException, MyConstraintViolationException{
		Order order = entityManager.find(Order.class, orderID);
		if (order == null) {
			throw new MyEntityNotFoundException(
					"Order with id '" + orderID + "' not found");
		}
		Product product = entityManager.find(Product.class, productCode);
		if (product == null) {
			throw new MyEntityNotFoundException(
					"Product with id '" + productCode + "' not found");
		}
		try {
			Hibernate.initialize(order.getProducts());
			Hibernate.initialize(order.getTotal_amount());
			Hibernate.initialize(product.getOrder());
			Hibernate.initialize(product.getStatus());
			Hibernate.initialize(product.getUnit_price());
			entityManager.lock(order, LockModeType.OPTIMISTIC);
			entityManager.lock(product, LockModeType.OPTIMISTIC);
			product.setStatus(ProductStatus.SOLD);
			order.addProduct(product);
			var currentAmount = order.getTotal_amount();
			order.setTotal_amount(Math.round((currentAmount + product.getUnit_price()) * 100.0) / 100.0);
			// shipping_cost will be 1% of total amount
			order.setShipping_cost(Math.round(0.01 * order.getTotal_amount() * 100.0) / 100.0);
			product.setOrder(order);

			var packs = entityManager.createQuery(("SELECT p FROM Package p"), Package.class)
					.getResultList();

			for (var pack : packs) {
				if (pack.getProducts().contains(product)) {
					entityManager.lock(order, LockModeType.OPTIMISTIC);
					entityManager.lock(pack, LockModeType.OPTIMISTIC);
					order.addPackage(pack);
					pack.addOrder(order);
				}
			}
		} catch (ConstraintViolationException e) {
			throw new MyConstraintViolationException(e);
		}
	}

	public void removeProductFromOrder(int orderID, long productCode)
			throws MyEntityNotFoundException, MyConstraintViolationException{
		Order order = entityManager.find(Order.class, orderID);
		if (order == null) {
			throw new MyEntityNotFoundException(
					"Order with id '" + orderID + "' not found");
		}
		Product product = entityManager.find(Product.class, productCode);
		if (product == null) {
			throw new MyEntityNotFoundException(
					"Product with id '" + productCode + "' not found");
		}
		try {
			Hibernate.initialize(order.getProducts());
			Hibernate.initialize(order.getTotal_amount());
			Hibernate.initialize(product.getOrder());
			Hibernate.initialize(product.getStatus());
			Hibernate.initialize(product.getUnit_price());
			entityManager.lock(order, LockModeType.OPTIMISTIC);
			entityManager.lock(product, LockModeType.OPTIMISTIC);
			order.removeProduct(product);
			product.setStatus(ProductStatus.AVAILABLE);
			var currentAmount = order.getTotal_amount();
			order.setTotal_amount(Math.round((currentAmount - product.getUnit_price()) * 100.0) / 100.0);
			order.setShipping_cost(Math.round(0.01 * order.getTotal_amount() * 100.0) / 100.0);
			product.setOrder(null);

			var packs = entityManager.createQuery(("SELECT p FROM Package p"), Package.class)
					.getResultList();

			for (var pack : packs) {
				if (pack.getProducts().contains(product)) {
					entityManager.lock(order, LockModeType.OPTIMISTIC);
					entityManager.lock(pack, LockModeType.OPTIMISTIC);
					order.removePackage(pack);
					pack.removeOrder(order);
				}
			}
		} catch (ConstraintViolationException e) {
			throw new MyConstraintViolationException(e);
		}
	}

	public void addLogicticOperatorToOrder(int orderID, String username)
			throws MyEntityNotFoundException, MyConstraintViolationException{
		Order order = entityManager.find(Order.class, orderID);
		if (order == null) {
			throw new MyEntityNotFoundException(
					"Order with id '" + orderID + "' not found");
		}
		LogisticOperator logisticOperator = entityManager.find(LogisticOperator.class, username);
		if (logisticOperator == null) {
			throw new MyEntityNotFoundException(
					"Logictic Operator with username '" + username + "' not found");
		}
		try {
			Hibernate.initialize(order.getLogisticOperator());
			Hibernate.initialize(logisticOperator.getOrders());
			entityManager.lock(order, LockModeType.OPTIMISTIC);
			entityManager.lock(logisticOperator, LockModeType.OPTIMISTIC);
			order.setLogisticOperator(logisticOperator);
			order.setDelivery_status(OrderDelivery.ACCEPTED);
			logisticOperator.addOrder(order);
		} catch (ConstraintViolationException e) {
			throw new MyConstraintViolationException(e);
		}
	}

	public void removeLogicticOperatorFromOrder(int orderID, String username)
			throws MyEntityNotFoundException, MyConstraintViolationException{
		Order order = entityManager.find(Order.class, orderID);
		if (order == null) {
			throw new MyEntityNotFoundException(
					"Order with id '" + orderID + "' not found");
		}
		LogisticOperator logisticOperator = entityManager.find(LogisticOperator.class, username);
		if (logisticOperator == null) {
			throw new MyEntityNotFoundException(
					"Logictic Operator with username '" + username + "' not found");
		}
		try {
			Hibernate.initialize(order.getLogisticOperator());
			Hibernate.initialize(logisticOperator.getOrders());
			entityManager.lock(order, LockModeType.OPTIMISTIC);
			entityManager.lock(logisticOperator, LockModeType.OPTIMISTIC);
			order.setLogisticOperator(null);
			order.setDelivery_status(OrderDelivery.PENDING);
			logisticOperator.removeOrder(order);
		} catch (ConstraintViolationException e) {
			throw new MyConstraintViolationException(e);
		}
	}

	public void setDeliveryStatus(int id, OrderDelivery status)
			throws MyEntityNotFoundException, MyConstraintViolationException, MyIllegalArgumentException{
		Order order = entityManager.find(Order.class, id);
		if (order == null) {
			throw new MyEntityNotFoundException(
					"Order with id '" + id + "' not found");
		}
		if (!status.equals(OrderDelivery.PENDING) && order.getLogisticOperator() == null) {
			throw new MyIllegalArgumentException
					("Orders with assigned logistic operator cannot be PENDING");
		}
		try {
			Hibernate.initialize(order.getDelivery_status());
			Hibernate.initialize(order.getDelivery_date());
			entityManager.lock(order, LockModeType.OPTIMISTIC);
			if (status.equals(OrderDelivery.DELIVERED)) {
				order.setDelivery_date(new Date().toString());
			}
			if (!status.equals(OrderDelivery.DELIVERED)) {
				order.setDelivery_date(null);
			}
			order.setDelivery_status(status);
		} catch (ConstraintViolationException e) {
			throw new MyConstraintViolationException(e);
		}
	}

	public void setShippingCost(int id, Double cost)
			throws MyEntityNotFoundException, MyConstraintViolationException, MyIllegalArgumentException{

		Order order = entityManager.find(Order.class, id);
		if (order == null) {
			throw new MyEntityNotFoundException(
					"Order with id '" + id + "' not found");
		}
		if (cost == null || cost < 0) {
			throw new MyIllegalArgumentException
					("Shipping cost can't be negative or null");
		}
		try {
			Hibernate.initialize(order.getShipping_cost());
			entityManager.lock(order, LockModeType.OPTIMISTIC);
			order.setShipping_cost(cost);
		} catch (ConstraintViolationException e) {
			throw new MyConstraintViolationException(e);
		}
	}
}
