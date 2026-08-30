package advanced.lld.service;

import advanced.lld.models.Cinema;
import advanced.lld.models.Movie;
import advanced.lld.models.Show;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SearchService {

    List<Show> shows;
    List<Cinema> listOfCinemas;

    public SearchService(List<Show> shows, List<Cinema> listOfCinemas) {
        this.shows = shows;
        this.listOfCinemas = listOfCinemas;
    }

    public List<Show> findShowByMovieAndCity(Movie movie, String city) {
        System.out.println("find show for movie " +  movie.getName() + " in city " + city);
         return shows.stream()
                .filter(show -> show.getMovie().equals(movie))
                .filter(show -> show.getScreen().getCinema().getCity().equalsIgnoreCase(city))
                .toList();
    }

    public List<Show> findShowByCinema(Cinema cinema) {
        return shows.stream()
                .filter(show -> show.getScreen().getCinema().getId().equalsIgnoreCase(cinema.getId()))
                .collect(Collectors.toList());
    }
}
