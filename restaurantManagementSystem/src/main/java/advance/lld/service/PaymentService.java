package advance.lld.service;

import advance.lld.model.Bill;
import advance.lld.model.Payment;
import advance.lld.model.PaymentMethod;
import advance.lld.strategy.PaymentStrategy;

import java.util.Map;

public class PaymentService {
    //can use strategy
    Map<PaymentMethod, PaymentStrategy> paymentMethodPaymentStrategyMap;
    BillingService billingService;
    public PaymentService(BillingService billingService, Map<PaymentMethod, PaymentStrategy> paymentMethodPaymentStrategyMap) {
        this.billingService = billingService;
        this.paymentMethodPaymentStrategyMap = paymentMethodPaymentStrategyMap;
    }

    public Payment makePayment(Bill bill, PaymentMethod method, Double amount) {

        if (amount.compareTo(bill.getRemainingAmount()) > 0) {
            throw new IllegalArgumentException(
                    "Payment exceeds remaining bill amount"
            );
        }
        Payment payment = paymentMethodPaymentStrategyMap.get(method).process(amount, method);
        bill.addPayment(payment);
        return  payment;
    }

}
