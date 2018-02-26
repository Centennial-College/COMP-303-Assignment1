package atm.server;

import java.io.Serializable;

import atm.shared.Operations;

/**
 *
 * Server Response Object sent from BankServer to AtmClient containing updated balance from operations.
 *
 */
public class ServerResponse implements Serializable {
	private Operations operation;
	private boolean operationSuccess;
	private double updatedBalance;
	private double requestedAmount;
	private String errorMessage;

	public ServerResponse() {
	}

	public Operations getOperation() {
		return operation;
	}

	public void setOperation(Operations operation) {
		this.operation = operation;
	}

	public boolean isOperationSuccess() {
		return operationSuccess;
	}

	public void setOperationSuccess(boolean operationSuccess) {
		this.operationSuccess = operationSuccess;
	}

	public double getUpdatedBalance() {
		return updatedBalance;
	}

	public void setUpdatedBalance(double updatedBalance) {
		this.updatedBalance = updatedBalance;
	}

	public double getRequestedAmount() {
		return requestedAmount;
	}

	public void setRequestedAmount(double requestedAmount) {
		this.requestedAmount = requestedAmount;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@Override
	public String toString() {
		switch (this.getOperation()) {
		case AUTHENTICATE:
			if (this.isOperationSuccess())
				return "You have successfully logged in!";
			return this.getErrorMessage();
		case BALANCE_INQUIRY:
			return String.format("Your balance is currently: $%.2f", this.getUpdatedBalance());
		case DEPOSIT:
			return String.format("You have successfully deposited $%.2f%nYour udpated balance is now: $%.2f",
					this.getRequestedAmount(), this.getUpdatedBalance());
		case WITHDRAW:
			if (this.isOperationSuccess())
				return String.format("You have successfully withdrawn $%.2f%nYour udpated balance is now: $%.2f",
						this.getRequestedAmount(), this.getUpdatedBalance());
			return this.getErrorMessage();
		}
		// don't need default case since operations are passed in by code, not user
		// input, won't be error

		return "";
	}
}
