package advanced.lld.models;

import advanced.lld.enums.SeatStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
public class ShowSeat {
    private Seat seat;
    private SeatStatus seatStatus;
    private double price;
    private String lockedByUserId;
    private LocalDateTime lockExpiryTime;

    public ShowSeat(Seat seat, double price) {
        this.seat = seat;
        this.price = price;
        this.seatStatus = SeatStatus.AVAILABLE;
    }

    public void book() {
        this.seatStatus = SeatStatus.BOOKED;
        this.lockedByUserId = null;
        this.lockExpiryTime = null;
    }

    public void lock(String userId, LocalDateTime expiryTime) {
        this.seatStatus = SeatStatus.LOCKED;
        lockedByUserId = userId;
        this.lockExpiryTime = expiryTime;
    }

    public void unlock() {
        this.seatStatus = SeatStatus.AVAILABLE;
        lockedByUserId = null;
        lockExpiryTime = null;
    }

}
