package advance.lld.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;

@Getter
public class Table {
    Long id;
    String tableName;
    TableStatus status;
    int capacity;
    Customer currentCustomer;
    @Setter
    Order activeOrder;
    List<Reservations> reservationsList;
    @Setter
    Waiter assignedWaiter;
    Object lock = new Object();

    public Table(Long id, String tableName, int capacity) {
        this.id = id;
        this.tableName = tableName;
        this.status = TableStatus.AVAILABLE;
        this.capacity = capacity;
    }

    public void addReservations(LocalDateTime from, LocalDateTime to, Customer customer, Table table) {
        Reservations reservations = new Reservations(1L, from, to, customer, table);
        if(this.reservationsList == null) {
            reservationsList = new ArrayList<>();
        }
        reservationsList.add(reservations);
    }

    public void outOfService() {

    }

    public void occupy(Customer customer, Waiter waiter) {

        if (status != TableStatus.AVAILABLE) {
            throw new IllegalStateException(
                    "Table is not available"
            );
        }

        this.currentCustomer = customer;
        this.assignedWaiter = waiter;
        this.status = TableStatus.OCCUPIED;
    }

    public void release() {
        this.assignedWaiter = null;
        this.activeOrder = null;
        this.currentCustomer = null;
        this.status = TableStatus.AVAILABLE;
    }

    public boolean canAccommodate(int guests) {
        return capacity >= guests;
    }

}
