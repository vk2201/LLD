package advanced.lld.models;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Screen {
    private String id;
    private String name;
    private Cinema cinema;
    private List<Seat> seats;

    public Screen(String id, String name, Cinema cinema) {
        this.id = id;
        this.name = name;
        this.cinema = cinema;
        seats = new ArrayList<>();
    }

    public void addSeat(Seat seat) {
        seats.add(seat);
    }

}
