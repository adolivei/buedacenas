package pt.ipleiria.estg.dei.ei.dae.daebackend.enums;

public enum SensorStatus {
	ACTIVE("Active"),
	INACTIVE("Inactive");

	private final String status;

	SensorStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return status;
	}
}
