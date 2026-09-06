package advance.lld.service;

import advance.lld.model.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class OrderService {

    private final AtomicInteger orderCounter =
            new AtomicInteger();
    private final AtomicInteger orderItemCounter =
            new AtomicInteger();

    public Order getOrCreateOrder(Waiter waiter, Table table) {

        synchronized (table.getLock()) {
            if (table.getStatus() != TableStatus.OCCUPIED) {
                throw new IllegalStateException(
                        "Table is not occupied"
                );
            }
            if(!Objects.isNull(table.getActiveOrder())) {
                return table.getActiveOrder();
            }
            table.setAssignedWaiter(waiter);
            Order order = new Order("O-" + orderCounter.incrementAndGet(), table);
            table.setActiveOrder(order);
            return order;
        }
    }

    public void addOrderItem(Order order, List<OrderItem> orderItemList) {
        if(order == null) {
            throw new IllegalStateException("");
        }
        order.getOrderItemList().addAll(orderItemList);
    }

    public OrderItem addItem(
            Order order,
            MenuItem menuItem,
            int quantity,
            Waiter waiter) {

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be positive"
            );
        }

        OrderItem item = new OrderItem("OI-" + orderItemCounter.incrementAndGet(), menuItem, quantity);
        item.addObserver(waiter);
        order.getOrderItemList().add(item);
        return item;
    }

    public void markOrderItemReady(OrderItem orderItem) {
        System.out.println("order item ready");
        orderItem.markOrderItemPrepared();
    }

    public void prepareOrderItem(OrderItem orderItem, Chef chef) {
        System.out.println("preparing order item");
        orderItem.startPreparing();
    }

}
