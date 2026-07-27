package advanced.lld.models;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Cinema {
    String id;
    String name;
    String address;
    String city;
    List<Screen> screenList;

    public Cinema(String id, String name, String city, String address) {
        this.id = id;
        this.name = name;
        screenList = new ArrayList<>();
        this.address = address;
        this.city = city;
    }

    public void addScreen(Screen screen) {
        screenList.add(screen);
    }
}
