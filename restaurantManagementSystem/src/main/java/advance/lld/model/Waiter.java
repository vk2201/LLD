package advance.lld.model;

public class Waiter extends Employee implements OrderItemObserver {

    public void updateOnOrderItem() {
        //pick and serve order;
    }

}
