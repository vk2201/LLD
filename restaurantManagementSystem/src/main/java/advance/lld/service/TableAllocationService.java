package advance.lld.service;

import advance.lld.model.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class TableAllocationService {

    public boolean reserveTable(Branch branch, Customer customer, LocalDateTime fromTime, LocalDateTime toTime, int requiredSeats) {
        for(Table table : branch.getTableList()) {
            boolean isEligibleTableForReservation = true;
            if(table.getCapacity() < requiredSeats) {
                continue;
            }
            synchronized (table.getLock()) {
                for (Reservations reservation : Optional.ofNullable(table.getReservationsList())
                        .orElse(Collections.emptyList())) {
                    if (reservation.getStatus() == ReservationStatus.CANCELLED || reservation.getStatus() == ReservationStatus.COMPLETED) {
                        continue;
                    }
                    LocalDateTime existingStart = reservation.getFromDateTime();
                    LocalDateTime existingEnd = reservation.getToDateTime();

                    boolean overlap =
                            fromTime.isBefore(existingEnd)
                                    && toTime.isAfter(existingStart);

                    if (overlap) {
                        isEligibleTableForReservation = false;
                        break;
                    }
                }
                if (isEligibleTableForReservation) {
                    table.addReservations(fromTime, toTime, customer, table);
                }
            }
        }
        return true;
    }

    public void checkIn(
            Reservations reservation,
            Waiter waiter) {

        Table table = reservation.getTable();

        synchronized (table.getLock()) {

            if (table.getStatus() != TableStatus.AVAILABLE) {
                throw new IllegalStateException(
                        "Table is currently occupied"
                );
            }
            reservation.checkIn();
            table.occupy(reservation.getCustomer(), waiter);
        }
    }


    public Table allocateWalkIn(
            Branch branch,
            Customer customer,
            int guestCount,
            Waiter waiter) {

        for (Table table : branch.getTableList()) {

            if (!table.canAccommodate(guestCount)) {
                continue;
            }

            synchronized (table.getLock()) {

                if (table.getStatus() != TableStatus.AVAILABLE) {
                    continue;
                }

                table.occupy(customer, waiter);
                return table;
            }
        }

        throw new IllegalStateException(
                "No table available for walk-in"
        );
    }


    public void releaseTable(Table table) {
        table.release();
    }


}
