package advanced.lld.service;

import advanced.lld.enums.BookingStatus;
import advanced.lld.enums.PaymentMethod;
import advanced.lld.enums.PaymentStatus;
import advanced.lld.models.Booking;
import advanced.lld.models.Payment;
import advanced.lld.strategy.PaymentStrategy;

import java.util.Map;
import java.util.UUID;

public class PaymentService {

    private final Map<PaymentMethod, PaymentStrategy> paymentMethodPaymentStrategyMap;
    private BookingService bookingService;

    public PaymentService(Map<PaymentMethod, PaymentStrategy> paymentMethodPaymentStrategyMap,
                          BookingService bookingService) {
        this.paymentMethodPaymentStrategyMap = paymentMethodPaymentStrategyMap;
        this.bookingService = bookingService;
    }

    public Payment pay(Booking booking, PaymentMethod paymentMethod) {
        PaymentStrategy paymentStrategy = paymentMethodPaymentStrategyMap.get(paymentMethod);
        String paymentId = UUID.randomUUID().toString();

        Payment payment = new Payment(paymentId, booking.getTotalAmount(),
                paymentMethod, paymentId, PaymentStatus.PENDING);
        boolean isSuccess= paymentStrategy.pay();
        if(isSuccess) {
            payment.updateStatus(PaymentStatus.SUCCESS);
            bookingService.confirmBooking(booking);
        } else {
            payment.updateStatus(PaymentStatus.FAILED);
            bookingService.failBooking(booking);
        }
        return payment;
    }

}
