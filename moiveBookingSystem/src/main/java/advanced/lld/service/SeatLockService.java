package advanced.lld.service;

import advanced.lld.enums.SeatStatus;
import advanced.lld.models.Show;
import advanced.lld.models.ShowSeat;
import advanced.lld.models.User;

import java.time.LocalDateTime;
import java.util.List;

public class SeatLockService {

    public boolean lockSeats(List<String> selectedSeatIds, Show show, User user) {

        synchronized (show.getLock()) {
            List<ShowSeat> seatsToLock =
                    show.getShowSeats()
                            .stream()
                            .filter(showSeat ->
                                    selectedSeatIds.contains(
                                            showSeat.getSeat()
                                                    .getId()))
                            .toList();

            if (seatsToLock.size()
                    != selectedSeatIds.size()) {

                throw new RuntimeException(
                        "Invalid seats");
            }

            boolean allAvailable =
                    seatsToLock.stream()
                            .allMatch(showSeat ->
                                    showSeat.getSeatStatus()
                                            == SeatStatus.AVAILABLE);

            if (!allAvailable) {

                throw new RuntimeException(
                        "Some seats are not available");
            }

            LocalDateTime expiryTime =
                    LocalDateTime.now()
                            .plusMinutes(5);

            for (ShowSeat showSeat : seatsToLock) {
                showSeat.lock(user.getUserId(), expiryTime);
                System.out.println("seat " + showSeat.getSeat().getId() + " is " + showSeat.getSeatStatus());
            }
            return true;
        }
    }

    public void unlockSeats(Show show, List<ShowSeat> showSeatList) {
        //Use the same synchronization boundary for every operation that modifies seat state.
        synchronized (show.getLock()) {
            for (ShowSeat showSeat : showSeatList) {
                if (showSeat.getSeatStatus().equals(SeatStatus.LOCKED)) {
                    showSeat.unlock();
                }
            }
        }
    }

}
