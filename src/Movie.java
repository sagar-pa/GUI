import java.util.HashSet;
import java.util.ArrayList;
import java.util.Objects;

public class Movie {
    public int id;
    public String name;
    public ArrayList<String> genre;

    public String director;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return id == movie.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Movie(int id){
        genre = new ArrayList<String>();
        this.id = id;
    }

    public Movie(int id, String name){
        genre = new ArrayList<String>();
        this.id = id;
        this.name = name;
    }
}
