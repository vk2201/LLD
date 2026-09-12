package advance.lld.state;

import advance.lld.model.ATM;
import advance.lld.model.Card;
import advance.lld.model.CardStatus;

import java.math.BigDecimal;

public class CardInsertedState implements AtmState {
    @Override
    public void insertCard(ATM atm, Card card) {
        System.out.println("operation not allowed");
    }

    @Override
    public void authenticate(ATM atm, int pin) {
        Card card = atm.getCard();
        if (card == null) {
            System.out.println("No card inserted.");
            return;
        }
        if (card.getStatus() != CardStatus.ACTIVE) {
            System.out.println("Card is not active.");
            return;
        }
        boolean authenticated = atm.getBankService().authenticateCard(card, pin);
        if(authenticated) {
            atm.setAtmState(new AuthenticatedState());
            return;
        }
        System.out.println("authentication failed. try again");
        card.setFailedAttempts(card.getFailedAttempts() + 1);
        if(card.getFailedAttempts() > 3) {
            atm.getBankService().blockCard(card);
            atm.setCard(null);
        }
    }

    @Override
    public void withdraw(ATM atm, BigDecimal amount) {
        System.out.println("operation not allowed");
    }

    @Override
    public void deposit(ATM atm, BigDecimal amount) {
        System.out.println("operation not allowed");
    }

    @Override
    public void changePin(ATM atm, int newPin) {
        System.out.println("operation not allowed");
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
