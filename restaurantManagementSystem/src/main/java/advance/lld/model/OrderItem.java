package advance.lld.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class OrderItem {
    String id;
    private MenuItem menuItem;
    private Integer quantity;
    private double price;
    private OrderItemStatus status;
    List<OrderItemObserver> observerList;

    public OrderItem(String id, MenuItem item, Integer quantity) {
        this.id = id;
        this.menuItem = item;
        this.quantity = quantity;
        this.status = OrderItemStatus.PLACED;
        observerList = new ArrayList<>();
    }

    public void startPreparing() {

        if (status != OrderItemStatus.PLACED) {
            throw new IllegalStateException(
                    "Item cannot move to PREPARING"
            );
        }
        status = OrderItemStatus.PREPARING;
    }

    public void markOrderItemPrepared() {

        if (status != OrderItemStatus.PREPARING) {
            throw new IllegalStateException(
                    "Item cannot move to PREPARING"
            );
        }
        status = OrderItemStatus.READY;
        notifyObserver();
    }

    public void addObserver(Waiter waiter) {
        observerList.add(waiter);
    }

    public void removeObserver() {

    }

    private void notifyObserver() {
        for (OrderItemObserver observer : observerList) {
            observer.updateOnOrderItem();
        }
    }

}
