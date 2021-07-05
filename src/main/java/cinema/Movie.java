package cinema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movie {

    private long id;
    private String title;
    private LocalDateTime date;
    private int spaces;
    private int freeSpaces;

    public Movie(long id, String title, LocalDateTime date, int spaces) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.spaces = spaces;
        freeSpaces = spaces;
    }

    public void reserveSeats(int number){
        if(freeSpaces<number){
            throw new IllegalStateException("Less spaces than needed");
        }

        freeSpaces -= number;
    }
}
