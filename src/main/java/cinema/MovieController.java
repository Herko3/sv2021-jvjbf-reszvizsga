package cinema;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/cinema")
public class MovieController {

    private MovieService service;

    public MovieController(MovieService service) {
        this.service = service;
    }

    @GetMapping
    public List<MovieDTO> getMovies(@RequestParam Optional<String> title) {
        return service.getMovies(title);
    }

    @GetMapping("{id}")
    public MovieDTO getMovieById(@PathVariable("id") long id) {
        return service.getMovieById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MovieDTO addMovie(@Valid @RequestBody CreateMovieCommand command) {
        return service.addMovie(command);
    }

    @PostMapping("{id}/reserve")
    @ResponseStatus(HttpStatus.CREATED)
    public MovieDTO reserveSeats(@PathVariable("id") long id, @RequestBody CreateReservationCommand command) {
        return service.reserveSeats(id, command);
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public MovieDTO updateStartTime(@PathVariable("id") long id, @RequestBody UpdateDateCommand command) {
        return service.updateStartTime(id, command);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAll() {
        service.deleteAll();
    }

    @ExceptionHandler(MovieNotFoundException.class)
    public ResponseEntity<Problem> handleNotFound(MovieNotFoundException e) {
        Problem problem = Problem.builder()
                .withType(URI.create("cinema/not-found"))
                .withTitle("Not Found")
                .withStatus(Status.NOT_FOUND)
                .withDetail(e.getMessage())
                .build();

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Problem> handleNotEnoughSpace(IllegalStateException e) {
        Problem problem = Problem.builder()
                .withType(URI.create("cinema/bad-reservation"))
                .withTitle("Not enough space")
                .withStatus(Status.BAD_REQUEST)
                .withDetail(e.getMessage())
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Problem> handleValidException(MethodArgumentNotValidException e) {
        List<Violation> violations =
                e.getBindingResult().getFieldErrors().stream()
                        .map(fe -> new Violation(fe.getField(), fe.getDefaultMessage(), fe.getRejectedValue()))
                        .toList();

        Problem problem = Problem.builder()
                .withType(URI.create("cinema/not-valid"))
                .withTitle("Validation Error")
                .withStatus(Status.BAD_REQUEST)
                .withDetail(e.getMessage())
                .with("violations", violations)
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);
    }
}
