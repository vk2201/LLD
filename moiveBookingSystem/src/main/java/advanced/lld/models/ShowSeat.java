package advanced.lld.models;

import advanced.lld.enums.SeatStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ShowSeat {
    private Seat seat;
    private SeatStatus seatStatus;
    private double price;

    public ShowSeat(Seat seat, SeatStatus seatStatus, double price) {
        this.seat = seat;
        this.seatStatus = seatStatus;
        this.price = price;
    }

}
