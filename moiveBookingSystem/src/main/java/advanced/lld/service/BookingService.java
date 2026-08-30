package advanced.lld.service;

import advanced.lld.enums.BookingStatus;
import advanced.lld.enums.SeatStatus;
import advanced.lld.models.Booking;
import advanced.lld.models.Show;
import advanced.lld.models.ShowSeat;
import advanced.lld.models.User;
import advanced.lld.strategy.PricingStrategy;

import java.util.List;
import java.util.UUID;

public class BookingService {

    private final SeatLockService seatLockService;

    public BookingService(SeatLockService seatLockService) {
        this.seatLockService = seatLockService;
    }

    public Booking createBooking(User user, List<String> selectedSeatIds,
                              Show selectedShow) {
        List<ShowSeat> selectedSeats =
                selectedShow.getShowSeats()
                        .stream()
                        .filter(showSeat ->
                                selectedSeatIds.contains(
                                        showSeat.getSeat()
                                                .getId())).toList();
        String id = "B" + UUID.randomUUID();
        return new Booking(id, user, selectedShow, selectedSeats);
    }

    public void confirmBooking(Booking booking) {
        synchronized (booking.getShow().getLock()) {
            boolean allSeatsLocked = booking.getShowSeats()
                    .stream()
                    .allMatch(showSeat ->
                            showSeat.getSeatStatus() == SeatStatus.LOCKED);

            if (!allSeatsLocked) {
                throw new RuntimeException(
                        "All seats must be locked before confirming booking"
                );
            }

            for (ShowSeat showSeat : booking.getShowSeats()) {
                showSeat.book();
            }
            booking.setStatus(BookingStatus.CONFIRMED);
        }
    }

    public void failBooking(Booking booking) {
        synchronized (booking.getShow().getLock()) {
            booking.setStatus(BookingStatus.FAILED);
            seatLockService.unlockSeats(booking.getShow(), booking.getShowSeats());
        }
    }
}
