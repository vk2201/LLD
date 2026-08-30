package advanced.lld.service;

import advanced.lld.models.Seat;
import advanced.lld.models.Show;
import advanced.lld.models.ShowSeat;
import advanced.lld.strategy.PricingStrategy;

public class PricingService {

    private final PricingStrategy pricingStrategy;

    public PricingService(
            PricingStrategy pricingStrategy) {

        this.pricingStrategy = pricingStrategy;
    }

    public void initializeShowSeats(Show show) {
        System.out.println("initialize seats for show " + show.getId());
        for (Seat seat :
                show.getScreen().getSeats()) {

            double price =
                    pricingStrategy.calculatePrice(
                            show,
                            seat);

            ShowSeat showSeat =
                    new ShowSeat(
                            seat,
                            price);

            show.addShowSeat(showSeat);
        }
    }
}
