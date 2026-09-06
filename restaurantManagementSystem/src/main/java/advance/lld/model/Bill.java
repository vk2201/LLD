package advance.lld.model;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Bill {
    String id;
    BillComponent billComponent;
    List<Payment> payments;
    Order order;
    Double totalAmount;
    BillStatus status;

    public Bill(String id, Order order, double totalAmount) {
        this.id = id;
        this.order = order;
        this.status = BillStatus.OPEN;
        this.totalAmount = totalAmount;
        payments = new ArrayList<>();
    }

    public synchronized void addPayment(
            Payment payment) {

        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new IllegalStateException(
                    "Payment is not successful"
            );
        }
        payments.add(payment);
        Double newRemaining =
                getRemainingAmount();
        if (newRemaining.compareTo(0.0) == 0) {
            status = BillStatus.PAID;
        } else {
            status = BillStatus.PARTIALLY_PAID;
        }
    }

    public synchronized Double getRemainingAmount() {
        double paid = 0.0;

        for (Payment payment : payments) {
            if (payment.getStatus() == PaymentStatus.SUCCESS) {
                paid = paid + (payment.getAmount());
            }
        }
        return (totalAmount - (paid));
    }
}
