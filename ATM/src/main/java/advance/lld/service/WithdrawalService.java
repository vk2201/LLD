package advance.lld.service;

import advance.lld.CashDispenser;
import advance.lld.model.*;
import advance.rert.fd.Main;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class WithdrawalService {

    private final BankService bankService;

    private final CashDispenser cashDispenser;


    public WithdrawalService(
            BankService bankService,
            CashDispenser cashDispenser
    ) {
        this.bankService = bankService;
        this.cashDispenser = cashDispenser;
    }
    
    public void withdraw(Card card, BigDecimal amount) {

        if (!isValidAmount(amount)) return;

        boolean isAmountDispensable = cashDispenser.canDispense(amount);
            if (isAmountDispensable) {
                BigDecimal balance = bankService.getAccountBalance(card);
                if (balance.compareTo(amount) >= 0) {
                    bankService.debit(amount, card);
                    try {
                        cashDispenser.dispense(amount);
                    } catch (Exception ex) {
                        bankService.credit(amount, card);
                        System.out.println("amount credited back to account. withdrawal failed");
                    }
                }
            }
    }

    private static boolean isValidAmount(BigDecimal amount) {
        if (amount == null ||
                amount.compareTo(BigDecimal.ZERO) <= 0) {

            System.out.println(
                    "Withdrawal amount must be positive."
            );

            return false;
        }

        if (amount.remainder(BigDecimal.TEN)
                .compareTo(BigDecimal.ZERO) != 0) {

            System.out.println(
                    "Amount must be a multiple of ₹10."
            );

            return false;
        }
        return true;
    }

    private void recordFailedTransaction(
            Card card,
            TransactionType type,
            BigDecimal amount
    ) {
        recordTransaction(
                card,
                type,
                amount,
                TransactionStatus.FAILED
        );
    }


    private void recordTransaction(
            Card card,
            TransactionType type,
            BigDecimal amount,
            TransactionStatus status
    ) {

        Transaction transaction =
                new Transaction(
                        UUID.randomUUID().toString(),
                        card.getAccount().getAccountNumber(),
                        type,
                        amount,
                        status,
                        LocalDateTime.now()
                );

        bankService.saveTransaction(transaction);
    }
}
