package advance.lld.strategy;

import advance.lld.model.Payment;
import advance.lld.model.PaymentMethod;
import advance.lld.model.PaymentStatus;

import java.util.UUID;

public class CardPaymentStrategy implements PaymentStrategy{

    @Override
    public Payment process(Double amount, PaymentMethod paymentMethod) {
        System.out.println(amount + " payment processed through " + paymentMethod);
        Payment payment = new Payment(1L, UUID.randomUUID().toString(), amount);
        System.out.println(amount + " payment processed through " + paymentMethod);
        payment.setStatus(PaymentStatus.SUCCESS);
        return payment;
    }
}
