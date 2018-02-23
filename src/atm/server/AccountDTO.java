package atm.server;

/**
 * 
 * @class AccountDTO
 * @author Suthas G
 * @description Data Transfer Object which represents a bank account.
 * @date 2018-02-21
 *
 */
public class AccountDTO {
	// customerId synonymous with account number
	private final int customerId; // once acount is created, this cannot change
	private int pin; // PIN for authentication
	private double balance; // balance remaining in account

	// Constructor to initialize the attributes of the bank account
	public AccountDTO(int customerId, int pin, double startingBalance) {
		this.customerId = customerId;
		this.pin = pin;
		this.balance = startingBalance;
	}

	// Accessor and mutator methods
	public int getCustomerId() {
		return this.customerId;
	}

	public int getPin() {
		return this.pin;
	}

	public void setPin(int pin) {
		this.pin = pin;
	}

	public double getBalance() {
		return this.balance;
	}
	// NOTE: there is no direct mutator method for balance - need to use deposit and
	// withdraw operations to modify the balance attribute from outside this class!

	// Defining the operations of a bank account
	public void deposit(double amount) {
		this.balance += amount;
	}

	public boolean withdraw(double amount) {
		// can't withdraw more money than you own!
		if (this.balance < amount) {
			return false;
		}

		// successful withdrawal of the requested amount from this account
		this.balance -= amount;
		return true;
	}
}
