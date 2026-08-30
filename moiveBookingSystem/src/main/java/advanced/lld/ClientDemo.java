package advanced.lld;

import advanced.lld.enums.*;
import advanced.lld.models.*;
import advanced.lld.service.*;
import advanced.lld.strategy.DefaultPricingStrategy;
import advanced.lld.strategy.PaymentStrategy;
import advanced.lld.strategy.PricingStrategy;
import advanced.lld.strategy.UpiPaymentStrategy;

import java.time.LocalDateTime;
import java.util.*;

public class ClientDemo {

    private final SeatLockService seatLockService = new SeatLockService();

    public void demo() {
        BookingService bookingService = new BookingService(seatLockService);
        PricingStrategy pricingStrategy = new DefaultPricingStrategy();
        PricingService pricingService = new PricingService(pricingStrategy);

        System.out.println("creating cinema ");
        Cinema cinema1 = new Cinema("1", "pvr 1", "Banglore","btm layout");
        Cinema cinema2 = new Cinema("1", "pvr 1", "Banglore","btm layout");

        System.out.println("creating screen ");
        Screen screen1 = new Screen("1", "screen1", cinema1);
        screen1.addSeat(new Seat("A1",1,1, SeatType.REACLINER));
        screen1.addSeat(new Seat("A2",1,2, SeatType.BASIC));
        screen1.addSeat(new Seat("A3",1,3, SeatType.BASIC));


        Screen screen2 = new Screen("2","screen2", cinema2);
        screen2.addSeat(new Seat("A1",1,1, SeatType.REACLINER));

        cinema1.addScreen(screen1);
        cinema2.addScreen(screen2);

        System.out.println("creating user");
        User user = new User("1", "vishal");
        System.out.println("creating movie");
        Movie movie = new Movie("1", "Batman", "180");

        List<ShowSeat> showSeatList = new ArrayList<>();

        Show show = new Show("s1",
                movie, screen1,
                LocalDateTime.of(2026, 8, 29, 18,0,0),
                LocalDateTime.of(2026, 8, 29, 21,0,0),
                showSeatList);


        // can create a pricing service which will initializePrice

        pricingService.initializeShowSeats(show);


        SearchService searchService = new SearchService(Collections.singletonList(show), Arrays.asList(cinema1,cinema2));

        List<Show> showList = searchService.findShowByMovieAndCity(movie, "Banglore");

        Show selectedShow = showList.getFirst();

        System.out.println("selected show by user "+ selectedShow.getId());

        System.out.println("seats "+ selectedShow.getShowSeats());

        List<String> selectedSeatIds = Collections.singletonList("A1");

        System.out.println("selected seats by user "+ selectedSeatIds);

        seatLockService.lockSeats(selectedSeatIds, selectedShow, user);

        Booking booking = bookingService.createBooking(user, selectedSeatIds, selectedShow);

        System.out.println("booking is created with status " + booking.getStatus() );

        Map<PaymentMethod, PaymentStrategy> paymentMethodPaymentStrategyMap
                = Map.of(PaymentMethod.UPI, new UpiPaymentStrategy());

        PaymentService paymentService = new PaymentService(paymentMethodPaymentStrategyMap, bookingService);
        Payment payment = paymentService.pay(booking, PaymentMethod.UPI);

        System.out.println("payment status " + payment.getStatus());
        System.out.println("booking status " + booking.getStatus());

    }

}
