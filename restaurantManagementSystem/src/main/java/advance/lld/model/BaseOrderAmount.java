package advance.lld.model;

import java.math.BigDecimal;

public class BaseOrderAmount implements BillComponent {

    private Order order;

    public BaseOrderAmount(Order order) {
        this.order = order;
    }

    @Override
    public Double calculate() {
        Double total = 0.0;

        for (OrderItem item : order.getOrderItemList()) {

            Double itemTotal =
                    item.getMenuItem()
                            .getPrice() * item.getQuantity();

            total = total + itemTotal;
        }
        return total;
    }

}
