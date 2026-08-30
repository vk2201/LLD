package advanced.lld.strategy;

import advanced.lld.models.Payment;

public class UpiPaymentStrategy implements PaymentStrategy {

    public boolean pay() {
        //call payment gateway and return response;
        return true;
    }
}
