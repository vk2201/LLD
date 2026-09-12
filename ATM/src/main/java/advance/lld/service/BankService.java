package advance.lld.service;

import advance.lld.model.Account;
import advance.lld.model.Card;
import advance.lld.model.CardStatus;
import advance.lld.model.Transaction;
import advance.rert.fd.Main;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BankService {

    private final Map<String, Account> cardToAccountMap =
            new ConcurrentHashMap<>();

    private final Map<String, Card> cards =
            new ConcurrentHashMap<>();

    private final Map<String, List<Transaction>> transactions =
            new ConcurrentHashMap<>();

    public void addAccount(Account account) {
        cardToAccountMap.put(
                account.getAccountNumber(),
                account
        );
        transactions.putIfAbsent(
                account.getAccountNumber(),
                Collections.synchronizedList(new ArrayList<>())
        );
    }


    public void addCard(Card card) {

        cards.put(
                card.getCardNumber(),
                card
        );
    }

    private void validateCard(Card card) {

        if (card == null) {
            throw new IllegalArgumentException(
                    "Card is required."
            );
        }

        if (card.getStatus() != CardStatus.ACTIVE) {
            throw new IllegalStateException(
                    "Card is not active."
            );
        }
    }
    
    public boolean authenticateCard(Card card, int pin) {
        return true;
    }

    public void blockCard(Card card) {
        card.blockCard();
    }

    public BigDecimal getAccountBalance(Card card) {
        validateCard(card);
        Account account = cardToAccountMap.get(card.getCardNumber());
        return account.getBalance();
    }

    public void debit(BigDecimal amount, Card card) {
        validateCard(card);
        if(!cardToAccountMap.containsKey(card.getCardNumber())) {
            System.out.println("card is not linked to any account");
            throw new IllegalStateException("card does not exist in account");
        }
        Account account = cardToAccountMap.get(card.getCardNumber());
        account.debit(amount);
    }

    public void credit(BigDecimal amount, Card card) {
        validateCard(card);
        if(!cardToAccountMap.containsKey(card.getCardNumber())) {
            System.out.println("card is not linked to any account");
            throw new IllegalStateException("card does not exist in account");
        }
        Account account = cardToAccountMap.get(card.getCardNumber());
        account.credit(amount);
    }

    public void changePin(
            Card card,
            int newPin
    ) {
        validateCard(card);
        //can add validation for pin
        card.setPin(newPin);
        System.out.println(
                "PIN changed successfully."
        );
    }


    public void saveTransaction(Transaction transaction) {
        transactions
                .computeIfAbsent(
                        transaction.getAccountNumber(),
                        k -> Collections.synchronizedList(
                                new ArrayList<>()
                        )
                )
                .add(transaction);
    }


    public List<Transaction> getTransactions(String accountNumber) {
        return transactions.getOrDefault(
                accountNumber,
                Collections.emptyList()
        );
    }

    public boolean hasSufficientBalance(
            Card card,
            BigDecimal amount
    ) {
        validateCard(card);
        return card.getAccount()
                .getBalance()
                .compareTo(amount) >= 0;
    }
}
