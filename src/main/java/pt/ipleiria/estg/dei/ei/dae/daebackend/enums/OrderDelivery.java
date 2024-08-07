package pt.ipleiria.estg.dei.ei.dae.daebackend.enums;

public enum OrderDelivery {
	PENDING("Pending"),
	SHIPPED("Shipped"),
	DELIVERED("Delivered"),
	ACCEPTED("Accepted by logistics operator");

	private final String status;

	OrderDelivery(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return status;
	}
}
