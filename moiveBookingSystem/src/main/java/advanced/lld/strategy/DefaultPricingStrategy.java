package advanced.lld.strategy;

import advanced.lld.enums.SeatType;
import advanced.lld.models.Seat;
import advanced.lld.models.Show;
import advanced.lld.models.ShowSeat;

import java.util.List;

public class DefaultPricingStrategy implements PricingStrategy {

    @Override
    public double calculatePrice(Show show, Seat seat) {

        return switch (seat.getSeatType()) {

            case SeatType.BASIC -> 200;

            case SeatType.REACLINER -> 350;
        };

    }
}
