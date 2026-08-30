package advanced.lld.models;

import advanced.lld.enums.BookingStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Booking {
    private final String id;
    private Double totalAmount;
    private BookingStatus status;
    private User user;
    private List<ShowSeat> showSeats;
    private Show show;
    private Payment payment;

    public Booking(String id, User user, Show show, List<ShowSeat> showSeats) {

        this.id = id;
        this.user = user;
        this.show = show;
        this.showSeats = showSeats;

        this.totalAmount =
                showSeats.stream()
                        .mapToDouble(
                                ShowSeat::getPrice)
                        .sum();

        this.status = BookingStatus.PAYMENT_PENDING;
    }

}
