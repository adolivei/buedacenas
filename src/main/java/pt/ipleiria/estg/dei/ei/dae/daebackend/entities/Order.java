package pt.ipleiria.estg.dei.ei.dae.daebackend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import pt.ipleiria.estg.dei.ei.dae.daebackend.enums.OrderDelivery;
import pt.ipleiria.estg.dei.ei.dae.daebackend.enums.PaymentMethod;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Table(
		name = "orders",
		uniqueConstraints = @UniqueConstraint(columnNames = {"id"})
)
@Entity
@NamedQueries({
		@NamedQuery(
				name = "getAllOrders",
				query = "SELECT o FROM Order o ORDER BY o.order_date" // JPQL
		)
})
public class Order extends Versionable implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private Double shipping_cost;
	private String delivery_date;
	@NotNull
	private String order_date;
	@NotNull
	private Double total_amount;
	@NotNull
	@Enumerated(EnumType.STRING)
	private OrderDelivery delivery_status;
	@NotNull
	@Enumerated(EnumType.STRING)
	private PaymentMethod payment_method;

	@ManyToMany(mappedBy = "orders", fetch = FetchType.LAZY)
	private List<Package> packages;
	@OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private List<Product> products;
	@ManyToOne
	@JoinColumn(name = "end_consumer_username")
	private EndConsumer endConsumer;
	@ManyToOne
	@JoinColumn(name = "logistic_operator_username")
	private LogisticOperator logisticOperator;

	public Order() {}

	public Order(PaymentMethod payment_method, EndConsumer endConsumer) {
		this.shipping_cost = null;
		this.delivery_date = null;
		this.order_date = new Date().toString();
		this.total_amount = (double) 0;
		this.delivery_status = OrderDelivery.PENDING;
		this.payment_method = payment_method;
		this.endConsumer = endConsumer;
		logisticOperator = null;
		packages = new LinkedList<>();
		products = new LinkedList<>();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Double getShipping_cost() {
		return shipping_cost;
	}

	public void setShipping_cost(Double shipping_cost) {
		this.shipping_cost = shipping_cost;
	}

	public String getDelivery_date() {
		return delivery_date;
	}

	public void setDelivery_date(String delivery_date) {
		this.delivery_date = delivery_date;
	}

	public String getOrder_date() {
		return order_date;
	}

	public void setOrder_date(String order_date) {
		this.order_date = order_date;
	}

	public Double getTotal_amount() {
		return total_amount;
	}

	public void setTotal_amount(Double total_amount) {
		this.total_amount = total_amount;
	}

	public OrderDelivery getDelivery_status() {
		return delivery_status;
	}

	public void setDelivery_status(OrderDelivery delivery_status) {
		this.delivery_status = delivery_status;
	}

	public PaymentMethod getPayment_method() {
		return payment_method;
	}

	public void setPayment_method(PaymentMethod payment_method) {
		this.payment_method = payment_method;
	}

	public EndConsumer getEndConsumer() {
		return endConsumer;
	}

	public void setEndConsumer(EndConsumer endConsumer) {
		this.endConsumer = endConsumer;
	}

	public LogisticOperator getLogisticOperator() {
		return logisticOperator;
	}

	public void setLogisticOperator(LogisticOperator logisticOperator) {
		this.logisticOperator = logisticOperator;
	}

	public List<Product> getProducts() {
		return products;
	}

	public List<Package> getPackages() {
		return packages;
	}

	public void addPackage(Package pack) {
		if (pack == null) throw new IllegalArgumentException("Package cannot be null");
		packages.add(pack);
	}

	public void removePackage(Package pack) {
		if (pack == null) throw new IllegalArgumentException("Package cannot be null");
		if (!packages.contains(pack)) throw new IllegalArgumentException("The order does not contain the package with the ID: " + pack.getId());
		packages.remove(pack);
	}

	public void addProduct(Product product) {
		if (product == null) throw new IllegalArgumentException("Product cannot be null");
		products.add(product);
	}

	public void removeProduct(Product product) {
		if (product == null) throw new IllegalArgumentException("Product cannot be null");
		if (!products.contains(product)) throw new IllegalArgumentException("The order does not contain the product with the code: " + product.getCode());
		products.remove(product);
	}

}
