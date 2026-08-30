package advanced.lld.models;

import advanced.lld.enums.SeatType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Seat {
    private final String id;
    private final int row;
    private final int col;
    private SeatType seatType;

    public Seat(String id, int row, int col, SeatType seatType) {
        this.id = id;
        this.row = row;
        this.col = col;
        this.seatType = seatType;
    }
}
