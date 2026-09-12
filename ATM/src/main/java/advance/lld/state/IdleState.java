package advance.lld.state;

import advance.lld.model.ATM;
import advance.lld.model.Card;
import com.sun.jdi.request.InvalidRequestStateException;

import java.math.BigDecimal;

public class IdleState implements AtmState {
    @Override
    public void insertCard(ATM atm, Card card) {
        if(atm.getCard() != null) {
            throw new InvalidRequestStateException("some card is already inserted in idle state");
        }
        atm.setCard(card);
        atm.setAtmState(new CardInsertedState());
    }

    @Override
    public void authenticate(ATM atm, int pin) {
        System.out.println("authenticate is not allowed in idle state");
    }

    @Override
    public void withdraw(ATM atm, BigDecimal amount) {
        System.out.println("withdraw is not allowed in idle state");

    }

    @Override
    public void deposit(ATM atm, BigDecimal amount) {
        System.out.println("deposit is not allowed in idle state");

    }

    @Override
    public void changePin(ATM atm, int newPin) {
        System.out.println("changePin is not allowed in idle state");

    }

    @Override
    public void balanceEnquiry(ATM atm) {
        System.out.println("balanceEnquiry is not allowed in idle state");

    }

    @Override
    public void ejectCard(ATM atm) {
        System.out.println("No Card to eject.");
    }
}
