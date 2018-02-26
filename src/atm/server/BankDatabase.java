package atm.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 * 
 * Represents the bank account information database
 *
 */
public class BankDatabase {

	// this ArrayList is used to keep the customer data in memory
	private ArrayList<AccountDTO> bankAccounts = new ArrayList<>();

	// constructor - port number is for the initialization of the multi-threaded
	// server
	public BankDatabase() {
		this.bankAccounts.addAll(Arrays.asList(new AccountDTO[] { new AccountDTO(3000000, 1234, 1001.23),
				new AccountDTO(3000001, 2341, 1.23), new AccountDTO(3000012, 3412, 1234567.93),
				new AccountDTO(3000123, 4123, 5000), new AccountDTO(3001234, 4321, 10000.21),
				new AccountDTO(3012345, 3214, 1000000), new AccountDTO(3123456, 2143, 9999999.99) }));
	}

	// should not be able to simply get full access to customer's bank accounts
	// outside of this "database"
	private AccountDTO getAccount(int customerId) {
		Iterator<AccountDTO> accounts = bankAccounts.iterator();
		AccountDTO tempAccount;
		while (accounts.hasNext()) {
			tempAccount = accounts.next();
			if (tempAccount.getCustomerId() == customerId)
				return tempAccount;
		}
		// no account with specified customerId found in the database
		return null;
	}

	// customer gets access to his/her bank account if successfully authenticated
	public boolean authenticateCustomer(int customerId, int pin) {
		AccountDTO customerAccount = getAccount(customerId);
		if (customerAccount != null)
			return customerAccount.getPin() == pin;
		// if execution reaches here, either customerAcc not found or pin is incorrect;
		return false;
	}

	public double getAccountBalance(int customerId) {
		return getAccount(customerId).getBalance();
	}

	public boolean withdraw(int customerId, double amount) {
		return getAccount(customerId).withdraw(amount);
	}

	public void deposit(int customerId, double amount) {
		getAccount(customerId).deposit(amount);
	}
}
