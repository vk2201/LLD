package advance.lld.model;

import lombok.Getter;

import java.util.List;

@Getter
public class Menu {
    List<MenuItem> menuItemList;

    public Menu(List<MenuItem> menuItemList) {
        this.menuItemList = menuItemList;
    }

    public void addMenu() {

    }
}
