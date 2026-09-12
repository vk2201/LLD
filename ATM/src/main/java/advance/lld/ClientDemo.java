package advance.lld;

import advance.lld.model.ATM;
import advance.lld.model.Account;
import advance.lld.model.Card;
import advance.lld.model.Denomination;
import advance.lld.service.BankService;
import advance.lld.service.DepositService;
import advance.lld.service.WithdrawalService;
import advance.lld.strategy.DispenseStrategy;
import advance.lld.strategy.GreedyDispenseStrategy;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;

public class ClientDemo {
    public void demo() {
        Account account = new Account(
                "ACC1001",
                new BigDecimal("10000")
        );

        Card card = new Card(
                "CARD1001",
                1234,
                account
        );

        BankService bankService = new BankService();

        bankService.addAccount(account);
        bankService.addCard(card);


        // ============================================================
        // 2. CREATE CASH DISPENSER
        // ============================================================

        Map<Denomination, Long> inventory = new EnumMap<>(Denomination.class);

        inventory.put(Denomination.FIVE_HUNDRED, 10L);
        inventory.put(Denomination.TWO_HUNDRED, 10L);
        inventory.put(Denomination.HUNDRED, 20L);
        inventory.put(Denomination.FIFTY, 20L);
        inventory.put(Denomination.TWENTY, 20L);
        inventory.put(Denomination.TEN, 20L);

        DispenseStrategy dispenseStrategy =
                new GreedyDispenseStrategy();

        CashDispenser cashDispenser =
                new CashDispenser(dispenseStrategy, inventory);


        // ============================================================
        // 3. CREATE SERVICES
        // ============================================================

        WithdrawalService withdrawalService =
                new WithdrawalService(bankService, cashDispenser);

        DepositService depositService =
                new DepositService(bankService);


        // ============================================================
        // 4. CREATE ATM
        // ============================================================
        ATM atm = new ATM(cashDispenser,
                bankService,
                withdrawalService,
                depositService
        );


        // ============================================================
        // 5. ATM FLOW
        // ============================================================

        System.out.println("\n========== INSERT CARD ==========");

        atm.insertCard(card);

        System.out.println("\n========== AUTHENTICATE ==========");

        atm.authenticate(1234);


        System.out.println("\n========== BALANCE INQUIRY ==========");

        atm.balanceEnquiry();


        System.out.println("\n========== WITHDRAW 1500 ==========");

        atm.withdraw(new BigDecimal("1500"));


        System.out.println("\n========== BALANCE AFTER WITHDRAWAL ==========");

        atm.balanceEnquiry();


        System.out.println("\n========== DEPOSIT 2000 ==========");

        atm.deposit(new BigDecimal("2000"));


        System.out.println("\n========== BALANCE AFTER DEPOSIT ==========");

        atm.balanceEnquiry();


        System.out.println("\n========== CHANGE PIN ==========");

        atm.pinChange(5678);


        System.out.println("\n========== TRY OLD PIN ==========");

        atm.ejectCard();

        atm.insertCard(card);

        atm.authenticate(1234);


        System.out.println("\n========== TRY NEW PIN ==========");

        atm.authenticate(5678);


        System.out.println("\n========== TRANSACTION HISTORY ==========");

        atm.printTransactions();


        System.out.println("\n========== EJECT CARD ==========");

        atm.ejectCard();
    }
}
