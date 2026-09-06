package advance.lld.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Order {
    String id;
    Table table;
    List<OrderItem> orderItemList;
    Bill bill;

    public Order(String id, Table table) {
        this.id = id;
        this.table = table;
        orderItemList = new ArrayList<>();
    }
}
