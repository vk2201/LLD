package advance.lld.model;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Reservations {
    Long id;
    LocalDateTime fromDateTime;
    LocalDateTime toDateTime;
    Customer customer;
    ReservationStatus status;
    private Table table;


    public Reservations(Long id, LocalDateTime from, LocalDateTime toDateTime,Customer customer, Table table) {
        this.id = id;
        this.fromDateTime = from;
        this.toDateTime = toDateTime;
        this.customer = customer;
        this.table = table;
    }

    public void checkIn() {

        if(ReservationStatus.CONFIRMED != status) {
            throw new IllegalStateException(
                    "Reservation cannot be checked in"
            );
        }
        status = ReservationStatus.CHECKED_IN;
    }
}
