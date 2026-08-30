package advanced.lld.models;

import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@ToString
public class Show {
    private final String id;
    private Movie movie;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<ShowSeat> showSeats;
    private Screen screen;
    private final Object lock = new Object();

    public Show(String id, Movie movie, Screen screen, LocalDateTime startTime, LocalDateTime endTime, List<ShowSeat> showSeats) {
        this.id = id;
        this.movie = movie;
        this.startTime = startTime;
        this.endTime = endTime;
        this.showSeats = showSeats;
        this.screen = screen;
    }

    public void addShowSeat(ShowSeat showSeat) {
        if(Objects.isNull(showSeats)) {
            showSeats = new ArrayList<>();
        }
        showSeats.add(showSeat);
    }
}
