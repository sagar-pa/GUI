import java.util.HashSet;
import java.util.Objects;

public class Actor {
    //identifying characteristics
    public int castid;
    public String name = null;
    public String character_name = null;

    //similarity qualifiers
    public HashSet<String> directorsWorkedWith;
    public HashSet<Movie> moviesActedIn;
    public HashSet<Actor> actorsWorkedWith;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Actor actor = (Actor) o;
        return castid == actor.castid;
    }

    @Override
    public int hashCode() {
        return Objects.hash(castid);
    }

    public Actor(int id){
        directorsWorkedWith = new HashSet<String>();
        moviesActedIn = new HashSet<Movie>();
        actorsWorkedWith = new HashSet<Actor>();
        this.castid = id;
    }

    public Actor(int id, String name, String character){
        directorsWorkedWith = new HashSet<String>();
        moviesActedIn = new HashSet<Movie>();
        actorsWorkedWith = new HashSet<Actor>();

        this.castid = id;
        this.name = name;
        this.character_name = character;
    }

    public void print() {
        System.out.println("castid " + this.castid + ": " + this.name);
    }
}
