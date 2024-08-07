package pt.ipleiria.estg.dei.ei.dae.daebackend.enums;

public enum PaymentMethod {
	CREDIT_CARD("Credit Card"),
	DEBIT_CARD("Debit Card"),
	PAYPAL("PayPal"),
	CASH("Cash"),
	BANK_TRANSFER("Bank Transfer");

	private final String methodName;

	PaymentMethod(String methodName) {
		this.methodName = methodName;
	}

	@Override
	public String toString() {
		return methodName;
	}
}
