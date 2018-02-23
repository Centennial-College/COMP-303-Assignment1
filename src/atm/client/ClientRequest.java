package atm.client;

import java.io.Serializable;

import atm.shared.Operations;

/*
 * This is the object that the client will send to the server.
 * 
 * Server application will receive customer ID, password/pin, amount and
 * operation (either deposit or withdraw), find customer, update account amount
 * and respond back to client with updated balance.
 */
public class ClientRequest implements Serializable {
	private int customerId;
	private int pin;
	private double amount;
	// private char operation;
	private Operations operation;

	// outside of this class only grant access to static methods
	public static ClientRequest authenticate(int id, int pin) {
		return new ClientRequest(id, pin, 0, Operations.AUTHENTICATE);
	}

	public static ClientRequest balanceInquiry(int id, int pin) {
		return new ClientRequest(id, pin, 0, Operations.BALANCE_INQUIRY);
	}

	public static ClientRequest deposit(int id, int pin, double amt) {
		return new ClientRequest(id, pin, amt, Operations.DEPOSIT);
	}

	public static ClientRequest withdraw(int id, int pin, double amt) {
		return new ClientRequest(id, pin, amt, Operations.WITHDRAW);
	}

	// constructor - not to be called outside of this class
	private ClientRequest(int id, int pin, double amt, Operations operation) {
		this.customerId = id;
		this.pin = pin;
		this.amount = amt;
		this.operation = operation;
	}

	public int getCustomerId() {
		return customerId;
	}

	public int getPin() {
		return pin;
	}

	public double getAmount() {
		return amount;
	}

	public Operations getOperation() {
		return operation;
	}

	@Override
	public String toString() {
		if (operation == Operations.AUTHENTICATE)
			return String.format("Customer ID: %s%nPIN: %s%nOperation: %s%n", this.customerId, this.pin,
					this.operation);

		return String.format("Customer ID: %s%nPIN: %s%nAmount %.2f%nOperation: %s%n", this.customerId, this.pin,
				this.amount, this.operation);
	}
}
