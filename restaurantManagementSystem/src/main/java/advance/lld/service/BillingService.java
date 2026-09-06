package advance.lld.service;

import advance.lld.model.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class BillingService {
    AtomicInteger billCounter = new AtomicInteger();
    private TableAllocationService tableAllocationService;

    public BillingService(TableAllocationService tableAllocationService) {
        this.tableAllocationService = tableAllocationService;
    }

    public Bill getBill(Order order, BillComponent billComponent) {
        synchronized (order.getTable().getLock()) {
            if (order.getBill() != null) {
                return order.getBill();
            }
            double totalAmount = billComponent.calculate();
            return new Bill("B-" + billCounter.incrementAndGet(), order, totalAmount);
        }
    }

}
