package advance.lld.state;

import advance.lld.model.ATM;
import advance.lld.model.Card;

import java.math.BigDecimal;

public class AuthenticatedState implements AtmState {


    @Override
    public void insertCard(ATM atm, Card card) {
        System.out.println("not allowed");
    }

    @Override
    public void authenticate(ATM atm, int pin) {
        System.out.println("already authenticated");
    }

    @Override
    public void withdraw(ATM atm, BigDecimal amount) {
        atm.getWithdrawalService().withdraw(atm.getCard(), amount);
    }

    @Override
    public void deposit(ATM atm, BigDecimal amount) {
        Card card = atm.getCard();

        atm.getDepositService()
                .deposit(card, amount);
    }

    @Override
    public void changePin(ATM atm, int newPin) {

    }

    @Override
    public void balanceEnquiry(ATM atm) {
        System.out.println("balanceEnquiry is not allowed in idle state");

    }

    @Override
    public void ejectCard(ATM atm) {
        System.out.println("Card ejected.");
        atm.setCard(null);
        atm.setAtmState(new IdleState());
    }
}
