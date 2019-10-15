import java.util.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class SimilarActors {
    private String baseFilename;
    private Integer query;
    private Connection conn;
    private Statement search;
    private HashMap<Integer, ArrayList<String>> genres;
    private HashMap<Integer, ArrayList<Integer>> actorsInMovie; //maps an actor castid to a movieid
    private Handler handler;

    public SimilarActors(Handler handler){
        this.handler = handler;

        genres = new HashMap<Integer, ArrayList<String>>();
        actorsInMovie = new HashMap<Integer, ArrayList<Integer>>();
    }

    private void getAllCharacters() throws java.sql.SQLException{
        String sqlQuery = "SELECT * FROM characters;";

        ResultSet rs = handler.databaseSearch(sqlQuery);

        while (rs.next()) {
            if(rs.getBoolean("iscrew" ) == true) {
                continue;
            }

            int movieId = rs.getInt("movieid");
            int castId = rs.getInt("castid");
            ArrayList<Integer> alist = actorsInMovie.get(movieId);
            if(alist == null){
                alist = new ArrayList<Integer>();
                alist.add(castId);
                actorsInMovie.put(movieId, alist);
            } else {
                alist.add(castId);
            }
        }
    }

    private void getAllGenres() throws java.sql.SQLException{
        String sqlQuery = "SELECT * FROM genres;";

        ResultSet rs = handler.databaseSearch(sqlQuery);

        while (rs.next()) {
            int movieId = rs.getInt("movie_id");
            String genre_name = rs.getString("name");
            ArrayList<String> list = genres.get(movieId);
            if(list == null){
                list = new ArrayList<String>();
                list.add(genre_name);
                genres.put(movieId, list);
            } else {
                list.add(genre_name);
            }
        }
    }

    /*
    * Given two movies, output the two most similar actors.
    */
    String search(String movie1, String movie2) throws java.sql.SQLException {

        if(genres.isEmpty()) {
            getAllGenres();
        }
        if(actorsInMovie.isEmpty()){
            getAllCharacters();
        }

        HashSet<Actor> actorsInMovie1 = getActorsInMovie(movie1);
        HashSet<Actor> actorsInMovie2 = getActorsInMovie(movie2);

        for(Actor a : actorsInMovie1){
            computeActorStats(a);
        }
        for(Actor a : actorsInMovie2){
            computeActorStats(a);
        }

        double maxScore = -1;
        Actor mostSimilarActor1 = null;
        Actor mostSimilarActor2 = null;

        for(Actor a1 : actorsInMovie1) {
            for(Actor a2 : actorsInMovie2) {
                double score = getSimilarityScore(a1, a2);
                if(score > maxScore) {
                    maxScore = score;
                    mostSimilarActor1 = a1;
                    mostSimilarActor2 = a2;
                }
            }
        }

        String output = getSimilarityReasons(mostSimilarActor1, mostSimilarActor2);

        System.out.println(output);
        return output;
    }

    String getSimilarityReasons(Actor a1, Actor a2) throws java.sql.SQLException{

        a1.name = getActorName(a1.castid);
        a2.name = getActorName(a2.castid);

        String output = a1.name + " and " + a2.name + " are the most similar actors. ";

        int mutualMovies = 0;
        //Number of mutual movies worked on + number of common movie genres
        if(!a1.moviesActedIn.isEmpty() && !a2.moviesActedIn.isEmpty()){
            Set<Movie> commonMovies = new HashSet<Movie>(a1.moviesActedIn);
            commonMovies.retainAll(a2.moviesActedIn);
            mutualMovies += commonMovies.size();
            output += "They have acted in " + mutualMovies + " mutual movies! ";

            String genre = getMostFrequentGenre(a1);
            if(genre.equals(getMostFrequentGenre(a2))){
                output += "Both of their most popular genres are " + genre + "! ";
            }
        }

        int mutualCostars = 0;
        //Number of mutual actors worked with
        if(!a1.actorsWorkedWith.isEmpty() && !a2.actorsWorkedWith.isEmpty()){
            Set<Actor> commonActors = new HashSet<Actor>(a1.actorsWorkedWith);
            commonActors.retainAll(a2.actorsWorkedWith);
            mutualCostars += commonActors.size();
            output += "They have " + mutualCostars + " mutual costars! ";
        }

        return output;
    }

    double getSimilarityScore(Actor a1, Actor a2) {
        double points = 0;

        int mutualMovies = 0; //each movie is worth 30 points
        boolean sameMostFrequentGenre = false; // worth 40 points
        int mutualCostars = 0; //worth mutualcostars/7 points

        //Number of mutual movies worked on + number of common movie genres
        if(!a1.moviesActedIn.isEmpty() && !a2.moviesActedIn.isEmpty()){
            Set<Movie> commonMovies = new HashSet<Movie>(a1.moviesActedIn);
            commonMovies.retainAll(a2.moviesActedIn);
            mutualMovies += commonMovies.size();

            if(getMostFrequentGenre(a1).equals(getMostFrequentGenre(a2))){
                sameMostFrequentGenre = true;
            }
        }

        //Number of mutual actors worked with
        if(!a1.actorsWorkedWith.isEmpty() && !a2.actorsWorkedWith.isEmpty()){
            Set<Actor> commonActors = new HashSet<Actor>(a1.actorsWorkedWith);
            commonActors.retainAll(a2.actorsWorkedWith);
            mutualCostars += commonActors.size();
        }

        points = (mutualMovies * 30) + (mutualCostars/7.0);
        if(sameMostFrequentGenre){
            points += 40;
        }
        return points; //number of points here is your total score
    }

    String getMostFrequentGenre(Actor a){
        if(a.moviesActedIn.isEmpty()) return null;

        HashMap<String, Integer> map = new HashMap<String, Integer>();
        String mostFrequentGenre = null;
        int max = -1;

        for(Movie m : a.moviesActedIn){
            for(String s : m.genre){
                Integer freq = map.get(s);
                if(freq == null){
                    map.put(s, 1);
                    if(mostFrequentGenre == null){
                        max = 1;
                        mostFrequentGenre = s;
                    }
                }
                else {
                    map.put(s, freq+1);
                    if(freq+1 > max){
                        max = freq+1;
                        mostFrequentGenre = s;
                    }
                }
            }
        }

        return mostFrequentGenre;
    }

    //Returns a list of Actors who act in 'movie'
    HashSet<Actor> getActorsInMovie(String movie) throws java.sql.SQLException {
        //try to just access the actor id through character table.
        String sqlQuery =
                "SELECT c.castid AS castid, \"cast\".name AS actor_name, c.name AS character_name " +
                "FROM characters AS c " +
                "INNER JOIN (SELECT id from movie1 WHERE original_title = 'REPLACEME') AS m " +
                    "ON c.movieid = m.id " +
                "INNER JOIN \"cast\" " +
                    "ON \"cast\".id = c.castid " +
                "WHERE c.iscrew = false;";

        sqlQuery = sqlQuery.replaceAll("REPLACEME", movie);

        ResultSet rs = handler.databaseSearch(sqlQuery);
        HashSet<Actor> list = new HashSet<Actor>();

        while (rs.next()) {
            int castId = rs.getInt("castid");

            Actor a = new Actor(castId);
            list.add(a);
        }

        return list;
    }

    //Returns a list of Actors who act in 'movie'
    HashSet<Actor> getActorsIDsInMovie(int movieid) throws java.sql.SQLException {
        ArrayList<Integer> alist = actorsInMovie.get(movieid);
        if(alist == null){
            return null;
        }

        HashSet<Actor> list = new HashSet<Actor>();
        for(Integer i : alist) {
            list.add(new Actor(i));
        }

        return list;
    }

    private void computeActorStats(Actor actor) throws java.sql.SQLException{
        //Movies Acted In
        String sqlQuery = "SELECT m.id AS movieid, m.original_title AS movie_title " +
                "FROM movie1 as m " +
                "INNER JOIN characters AS c " +
                "ON c.movieid = m.id " +
                "WHERE castid = REPLACEME;";
        sqlQuery = sqlQuery.replaceAll("REPLACEME", Integer.toString(actor.castid));

        ResultSet rs = handler.databaseSearch(sqlQuery);

        while (rs.next()) {
            int movieId = rs.getInt("movieid");
            //String movie_name = rs.getString("movie_title");

            //Movie m = new Movie(movieId, movie_name);
            Movie m = new Movie(movieId);
            m.genre = genres.get(m.id); //get genres
            actor.actorsWorkedWith.addAll(getActorsIDsInMovie(m.id));
            actor.actorsWorkedWith.remove(actor); //remove the self actor from actorsWorkedWith
            actor.moviesActedIn.add(m);
        }
    }

    private String getActorName(int castid) throws java.sql.SQLException{
        //Movies Acted In
        String sqlQuery = "SELECT name AS actor_name FROM \"cast\" WHERE id = " + castid + ";";

        ResultSet rs = handler.databaseSearch(sqlQuery);

        if (rs.next()) {
            return rs.getString("actor_name");
        }

        return null;
    }
}
