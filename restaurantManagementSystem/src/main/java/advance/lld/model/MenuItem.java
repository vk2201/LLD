package advance.lld.model;

import lombok.Getter;

import java.util.List;

@Getter
public class MenuItem {
    Long id;
    String name;
    Double price;
    Boolean isAvailable;

    public MenuItem(Long id, String name, Double price, Boolean isAvailable) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.isAvailable = isAvailable;
    }
}
