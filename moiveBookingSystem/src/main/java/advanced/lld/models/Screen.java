package advanced.lld.models;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Screen {
    private String id;
    private String name;
    private List<Seat> seatList;

    public Screen(String id, String name) {
        this.id = id;
        this.name = name;
        seatList = new ArrayList<>();
    }

    public void addSeat(Seat seat) {
        seatList.add(seat);
    }

}
