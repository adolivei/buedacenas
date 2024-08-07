package pt.ipleiria.estg.dei.ei.dae.daebackend.dtos;

import pt.ipleiria.estg.dei.ei.dae.daebackend.entities.EndConsumer;
import pt.ipleiria.estg.dei.ei.dae.daebackend.entities.LogisticOperator;
import pt.ipleiria.estg.dei.ei.dae.daebackend.enums.OrderDelivery;
import pt.ipleiria.estg.dei.ei.dae.daebackend.enums.PaymentMethod;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class OrderDTO implements Serializable {
	private int id;
	private Double shipping_cost;
	private String delivery_date;
	private String order_date;
	private Double total_amount;
	private OrderDelivery delivery_status;
	private PaymentMethod payment_method;
	private EndConsumerDTO endConsumer;
	private LogisticOperatorDTO logisticOperator;

	private List<ProductDTO> products = new LinkedList<>();;
	private List<PackageDTO> packages = new LinkedList<>();

	public OrderDTO() {
	}

	public OrderDTO(int id, PaymentMethod payment_method, EndConsumerDTO endConsumer, Double total_amount, Double shipping_cost, String delivery_date, String order_date, OrderDelivery delivery_status) {
		this.id = id;
		this.shipping_cost = shipping_cost;
		this.delivery_date = delivery_date;
		this.order_date = order_date;
		this.total_amount = total_amount;
		this.delivery_status = delivery_status;
		this.payment_method = payment_method;
		this.endConsumer = endConsumer;
		this.logisticOperator = null;
	}

	public OrderDTO(int id, PaymentMethod payment_method, Double total_amount, Double shipping_cost, String delivery_date, String order_date, OrderDelivery delivery_status) {
		this.id = id;
		this.shipping_cost = shipping_cost;
		this.delivery_date = delivery_date;
		this.order_date = order_date;
		this.total_amount = total_amount;
		this.delivery_status = delivery_status;
		this.payment_method = payment_method;
		logisticOperator = null;
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

	public EndConsumerDTO getEndConsumer() {
		return endConsumer;
	}

	public void setEndConsumer(EndConsumerDTO endConsumer) {
		this.endConsumer = endConsumer;
	}

	public List<ProductDTO> getProducts() {
		return products;
	}

	public void setProducts(List<ProductDTO> products) {
		this.products = products;
	}

	public List<PackageDTO> getPackages() {
		return packages;
	}

	public void setPackages(List<PackageDTO> packages) {
		this.packages = packages;
	}

	public LogisticOperatorDTO getLogisticOperator() {
		return logisticOperator;
	}

	public void setLogisticOperator(LogisticOperatorDTO logisticOperator) {
		this.logisticOperator = logisticOperator;
	}
}
