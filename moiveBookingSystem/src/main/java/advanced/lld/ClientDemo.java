package advanced.lld;

import advanced.lld.enums.SeatType;
import advanced.lld.models.*;
import advanced.lld.service.BookingService;
import advanced.lld.service.SearchService;

import java.util.Collections;
import java.util.List;

public class ClientDemo {

    private SearchService searchService;
    private BookingService bookingService;

    public void demo() {

        Cinema cinema1 = new Cinema("1", "pvr 1", "Banglore","btm layout");
        Cinema cinema2 = new Cinema("1", "pvr 1", "Banglore","btm layout");

        Screen screen1 = new Screen("1", "screen1");
        screen1.addSeat(new Seat("1",1,1, SeatType.REACLINER));
        cinema1.addScreen(screen1);

        Screen screen2 = new Screen("2","screen2");
        screen2.addSeat(new Seat("1",1,1, SeatType.REACLINER));
        cinema2.addScreen(screen2);

        User user = new User("1", "vishal");
        Movie movie = new Movie();
        List<Show> showList = searchService.findShowByMovieAndCity(movie, "Banglore");

        //user select show , it will show available show seat
        System.out.println(showList.get(0).getShowSeats());

        Booking booking = bookingService.selectSeat(Collections.singletonList(showList.get(0).getShowSeats().get(0)));

        paymentService.makePayment(booking);

    }

}
