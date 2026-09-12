package advance.lld.service;

import advance.lld.model.Card;
import advance.lld.model.Transaction;
import advance.lld.model.TransactionStatus;
import advance.lld.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class DepositService {

    private BankService bankService;

    public DepositService(
            BankService bankService
    ) {
        this.bankService = bankService;
    }


    public void deposit(Card card, BigDecimal amount) {
        if (amount == null ||
                amount.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Deposit amount must be positive.");
            return;
        }

        bankService.credit(amount, card);

        Transaction transaction =
                new Transaction(
                        UUID.randomUUID().toString(),
                        card.getAccount().getAccountNumber(),
                        TransactionType.DEPOSIT,
                        amount,
                        TransactionStatus.SUCCESS,
                        LocalDateTime.now()
                );
        bankService.saveTransaction(transaction);
        System.out.println("₹" + amount + " deposited successfully.");
    }
}
