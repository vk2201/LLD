package advance.lld.model;

import advance.lld.CashDispenser;
import advance.lld.service.BankService;
import advance.lld.service.DepositService;
import advance.lld.service.WithdrawalService;
import advance.lld.state.AtmState;
import advance.lld.state.IdleState;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
public class ATM {

    private final BankService bankService;
    private final WithdrawalService withdrawalService;
    private final DepositService depositService;

    @Setter
    private AtmState atmState;
    @Setter
    private Card card;
    private final CashDispenser cashDispenser;

    public ATM(CashDispenser cashDispenser, BankService bankService,
               WithdrawalService withdrawalService, DepositService depositService) {
        atmState = new IdleState();
        this.cashDispenser = cashDispenser;
        this.bankService = bankService;
        this.withdrawalService = withdrawalService;
        this.depositService = depositService;
    }

    public void insertCard(advance.lld.model.Card card) {
        atmState.insertCard(this, card);
    }

    public void authenticate(int pin) {
        atmState.authenticate(this, pin);
    }

    public void withdraw(BigDecimal amount) {
        atmState.withdraw(this, amount);
    }

    public void deposit(BigDecimal amount) {
        atmState.deposit(this, amount);
    }

    public void pinChange(int pin) {
        atmState.changePin(this,pin);
    }

    public void balanceEnquiry() {
        atmState.balanceEnquiry(this);
    }

    public void ejectCard() {
        atmState.ejectCard(this);
    }

    public void printTransactions() {
        if (card == null) {
            System.out.println("No card inserted.");
            return;
        }
        List<Transaction> transactions =
                bankService.getTransactions(card.getAccount().getAccountNumber());
        if (transactions.isEmpty()) {
            System.out.println("No transactions.");
            return;
        }
        for (Transaction transaction : transactions) {
            System.out.println(transaction);
        }
    }

}
