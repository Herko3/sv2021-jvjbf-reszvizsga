package cinema;

public class MovieNotFoundException extends IllegalArgumentException  {

    public MovieNotFoundException(String s) {
        super(s);
    }
}
