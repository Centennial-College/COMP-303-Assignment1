package atm;

import java.io.Serializable;

public class AccountDto implements Serializable {
    public double amount;

    /**
     * Type of operationType requested. Could be one of:
     * balance: Get balance inquiry
     * deposit: Deposit or Withdraw money based on the value of amount field
     */
    public String operation;
}

