package advanced.lld.models;

import advanced.lld.enums.BookingStatus;
import lombok.Getter;

@Getter
public class Booking {
    private final String id;
    private Double totalAmount;
    private BookingStatus status;
    private User user;
    private ShowSeat seat;
    private Show show;
    private Cinema cinema;
    private Payment payment;

    public Booking(String id) {
        this.id = id;
    }
}
