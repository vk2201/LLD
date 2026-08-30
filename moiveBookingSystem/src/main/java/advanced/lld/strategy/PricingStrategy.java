package advanced.lld.strategy;

import advanced.lld.models.Seat;
import advanced.lld.models.Show;
import advanced.lld.models.ShowSeat;

import java.util.List;

public interface PricingStrategy {

    public double calculatePrice(Show show, Seat seat);

}
