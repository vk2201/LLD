package advance.lld.model;

import advance.rert.fd.Main;
import lombok.Getter;
import lombok.Setter;

@Getter
public class Card {
    private final String cardNumber;
    @Setter
    private int pin;
    private CardStatus status;
    @Setter
    private int failedAttempts;
    private final Account account;


    public Card(String cardNumber, int pin, Account account) {
        this.cardNumber = cardNumber;
        this.pin = pin;
        this.failedAttempts = 0;
        this.account = account;
    }

    public void blockCard() {
        this.status = CardStatus.BLOCKED;
    }
}
