package cinema;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class MovieService {

    private List<Movie> movies = new ArrayList<>();

    private ModelMapper mapper;
    private AtomicLong idGenerator = new AtomicLong();

    public MovieService(ModelMapper mapper) {
        this.mapper = mapper;
    }

    public List<MovieDTO> getMovies(Optional<String> title) {
        return movies.stream()
                .filter(m -> title.isEmpty() || m.getTitle().equalsIgnoreCase(title.get()))
                .map(m -> mapper.map(m, MovieDTO.class))
                .toList();
    }

    public MovieDTO getMovieById(long id) {
        return mapper.map(findById(id), MovieDTO.class);
    }

    private Movie findById(long id) {
        return movies.stream()
                .filter(m -> m.getId() == id)
                .findAny()
                .orElseThrow(() -> new MovieNotFoundException("No movie with id: " + id));

    }

    public MovieDTO addMovie(CreateMovieCommand command) {
        Movie movie = new Movie(idGenerator.incrementAndGet(), command.getTitle(), command.getDate(), command.getSpaces());
        movies.add(movie);

        return mapper.map(movie, MovieDTO.class);
    }

    public MovieDTO reserveSeats(long id, CreateReservationCommand command) {
        Movie movie = findById(id);

        movie.reserveSeats(command.getReserve());

        return mapper.map(movie, MovieDTO.class);
    }

    public MovieDTO updateStartTime(long id, UpdateDateCommand command) {
        Movie movie = findById(id);
        movie.setDate(command.getDate());

        return mapper.map(movie,MovieDTO.class);
    }

    public void deleteAll() {
        movies.clear();
        idGenerator = new AtomicLong();
    }
}
