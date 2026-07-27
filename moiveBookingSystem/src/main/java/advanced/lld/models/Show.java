package advanced.lld.models;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class Show {
    private final String id;
    private Movie movie;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<ShowSeat> showSeats;
    private Screen screen;

    public Show(String id, Movie movie, LocalDateTime startTime, LocalDateTime endTime, List<ShowSeats> showSeats) {
        this.id = id;
        this.movie = movie;
        this.startTime = startTime;
        this.endTime = endTime;
        this.showSeats = showSeats;
    }

}
