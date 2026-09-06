package advance.lld.model;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Payment {
    Long id;
    String txnId;
    Double amount;
    @Setter
    PaymentStatus status;

    public Payment(Long id, String txnId, Double amount) {
        this.id = id;
        this.txnId = txnId;
        this.amount = amount;
        status = PaymentStatus.PENDING;
    }
}
