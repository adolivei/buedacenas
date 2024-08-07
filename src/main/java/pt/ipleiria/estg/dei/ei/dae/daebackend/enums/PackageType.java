package pt.ipleiria.estg.dei.ei.dae.daebackend.enums;

public enum PackageType {
	PRIMARY("Primary"),
	SECONDARY("Secondary"),
	TERTIARY("Tertiary");

	private final String type;

	PackageType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return type;
	}
}
