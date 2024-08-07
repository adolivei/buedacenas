package pt.ipleiria.estg.dei.ei.dae.daebackend.enums;

public enum ProductStatus {
	AVAILABLE("Available"),
	SOLD("Sold");

	private final String status;

	ProductStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return status;
	}
}
