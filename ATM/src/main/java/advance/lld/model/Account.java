package advance.lld.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

public class Account {
    @Getter
    String accountNumber;
    @Setter
    BigDecimal balance;

    public Account(String accountNumber, BigDecimal balance) {
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    public synchronized BigDecimal getBalance() {
        return balance;
    }

    public synchronized void debit(BigDecimal amount) {

        if (balance.compareTo(amount) < 0) {
            throw new IllegalStateException(
                    "Insufficient account balance."
            );
        }

        balance = balance.subtract(amount);
    }

    public synchronized void credit(BigDecimal amount) {

        balance = balance.add(amount);
    }
}
