package advance.lld.strategy;

import advance.lld.model.Payment;
import advance.lld.model.PaymentMethod;

public interface PaymentStrategy {
    public Payment process(Double amount, PaymentMethod paymentMethod);
}
