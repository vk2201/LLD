package advance.lld.model;

public class Chef extends Employee {

    public void prepareOrderItem(OrderItem orderItem) {
        System.out.println("preparing order item " + orderItem.getMenuItem().getName());
    }

}
