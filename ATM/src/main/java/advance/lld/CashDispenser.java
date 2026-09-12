package advance.lld;

import advance.lld.model.Denomination;
import advance.lld.strategy.DispenseStrategy;

import java.math.BigDecimal;
import java.util.Map;

public class CashDispenser {
    Map<Denomination, Long> denominationsInventory;
    DispenseStrategy dispenseStrategy;

    public CashDispenser(DispenseStrategy dispenseStrategy, Map<Denomination, Long> denominationsInventory) {
        this.dispenseStrategy = dispenseStrategy;
        this.denominationsInventory = denominationsInventory;
    }

    public void addInventory() {

    }

    public boolean canDispense(BigDecimal amount) {
        dispenseStrategy.calculate(amount, denominationsInventory);
        return true;
    }

    public synchronized void dispense(BigDecimal amount) throws Exception {
        Map<Denomination, Long> requiredInventory = dispenseStrategy.calculate(amount, denominationsInventory);
        deductInventory(requiredInventory);
        try {
            System.out.println("physically cash dispensed");
        } catch (Exception ex) {
            //updateInventory back inventory as there is hardware failure before dispensing cash.
            throw new Exception(ex);
        }

    }

    private void deductInventory(Map<Denomination, Long> requiredInventory) throws Exception {
        for (Map.Entry<Denomination, Long> entry : requiredInventory.entrySet()) {
            if(!denominationsInventory.containsKey(entry.getKey())
                    || denominationsInventory.get(entry.getKey()) > entry.getValue()) {
                throw new Exception("inventory is not present but cash dispensed");
            }
            denominationsInventory.put(entry.getKey(), denominationsInventory.get(entry.getKey()) - entry.getValue());
        }
    }
}
