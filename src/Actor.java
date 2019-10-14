import java.lang.reflect.Array;
import java.util.ArrayList;

public class Actor {
    //identifying characteristics
    public int castid;
    public String name = null;
    public String character_name = null;

    //similarity qualifiers
    public int numberOfMovies;
    public String mostCommonGenre;
    public ArrayList<String> directorsWorkedWith;
    public ArrayList<Actor> actorsWorkedWith;
    //also acted in movies with


    public Actor(int id, String name, String character){
        this.castid = id;
        this.name = name;
        this.character_name = character;
    }
}
