package advanced.lld.models;

import advanced.lld.enums.PaymentMethod;
import advanced.lld.enums.PaymentStatus;
import lombok.Getter;

import java.util.Date;

@Getter
public class Payment {
    private final String id;
    private final String txnId;
    private final Double amount;
    private final Date createdAt;
    private Date updatedAt;
    private final PaymentMethod paymentMethod;
    private PaymentStatus status;

    public Payment(String id, Double amount, PaymentMethod paymentMethod, String txnId, PaymentStatus status) {
        this.id = id;
        this.amount = amount;
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.paymentMethod = paymentMethod;
        this.txnId = txnId;
        this.status = status;
    }

    public void updateStatus(PaymentStatus status) {
        this.status = status;
        this.updatedAt = new Date();
    }



}
