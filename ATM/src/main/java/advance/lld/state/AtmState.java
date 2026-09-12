package advance.lld.state;

import advance.lld.model.ATM;
import advance.lld.model.Card;

import java.math.BigDecimal;

public interface AtmState {
    public void insertCard(ATM atm, Card card);
    public void authenticate(ATM atm, int pin);
    public void withdraw(ATM atm, BigDecimal amount);
    public void deposit(ATM atm, BigDecimal amount);
    public void changePin(ATM atm, int newPin);
    public void balanceEnquiry(ATM atm);
    public void ejectCard(ATM atm);
}
