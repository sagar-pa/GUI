import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import java.io.PrintWriter;

import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;

public class SimilarActors {
    private String baseFilename;
    private Integer query;
    private Connection conn;
    private Statement search;
    private HashMap<Integer, ArrayList<String>> genres;

    public SimilarActors(Connection conn){
        if(conn == null) {
            System.out.println("SimilarActors: Connection is bad.");
        }
        this.conn = conn;

        try {
            genres = new HashMap<Integer, ArrayList<String>>();
            getAllGenres();
        }
        catch (Exception e){
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void getAllGenres() throws java.sql.SQLException{
        String sqlQuery = "SELECT * FROM genres;";

        ResultSet rs = database_search(sqlQuery);

        while (rs.next()) {
            int movie_id = rs.getInt("movie_id");
            String genre_name = rs.getString("name");
            ArrayList<String> list = genres.get(movie_id);
            if(list == null){
                list = new ArrayList<String>();
                list.add(genre_name);
                genres.put(movie_id, list);
            } else {
                list.add(genre_name);
            }
        }
    }

    //given two movies, print two actors who are most similar and give the reasons
    void compute(String movie1, String movie2) throws java.sql.SQLException {

        HashSet<Actor> actorsInMovie1 = getActorsInMovie(movie1);
        HashSet<Actor> actorsInMovie2 = getActorsInMovie(movie2);

        System.out.println("Actor in movie 1");
        for(Actor a : actorsInMovie1){
            System.out.println("Computing Stats for: " + a.name);
            computeActorStats(a);
            a.print();
        }

        System.out.println("Actor in movie 2");
        for(Actor a : actorsInMovie2){
            System.out.println("Computing Stats for: " + a.name);
            computeActorStats(a);
            a.print();
        }

        System.out.println("Actor from movie 1: ");
        System.out.println("Actor from movie 2: ");
        System.out.println("Reasons: ");
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

        ResultSet rs = database_search(sqlQuery);
        HashSet<Actor> list = new HashSet<Actor>();

        while (rs.next()) {
            int castId = rs.getInt("castid");
            String actor_name = rs.getString("actor_name");
            String character_name = rs.getString("character_name");

            Actor a = new Actor(castId, actor_name, character_name);
            list.add(a);
        }

        return list;
    }

    //Returns a list of Actors who act in 'movie'
    HashSet<Actor> getActorsInMovie(int movieid) throws java.sql.SQLException {
        String sqlQuery =
                "SELECT c.castid AS castid, \"cast\".name AS actor_name, c.name AS character_name " +
                        "FROM characters AS c " +
                        "INNER JOIN (SELECT id from movie1 WHERE id = 'REPLACEME') AS m " +
                        "ON c.movieid = m.id " +
                        "INNER JOIN \"cast\" " +
                        "ON \"cast\".id = c.castid " +
                        "WHERE c.iscrew = false;";

        sqlQuery = sqlQuery.replaceAll("REPLACEME", Integer.toString(movieid));

        ResultSet rs = database_search(sqlQuery);
        HashSet<Actor> list = new HashSet<Actor>();

        while (rs.next()) {
            int castId = rs.getInt("castid");
            String actor_name = rs.getString("actor_name");
            String character_name = rs.getString("character_name");

            Actor a = new Actor(castId, actor_name, character_name);
            list.add(a);
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

        ResultSet rs = database_search(sqlQuery);

        while (rs.next()) {
            int movieId = rs.getInt("movieid");
            String movie_name = rs.getString("movie_title");

            Movie m = new Movie(movieId, movie_name);


            actor.moviesActedIn.add(m);
        }

        for(Movie m : actor.moviesActedIn){
            m.genre = genres.get(m.id);
            /*
            //get movie genres
            sqlQuery = "SELECT name AS genre FROM genres WHERE movie_id = " + Integer.toString(m.id) + ";";

            ResultSet rs2 = database_search(sqlQuery);
            while(rs2.next()){
                String genre = rs2.getString(1);
                m.genre.add(genre);

            }
            */
        }

        /*
        for(Movie m : actor.moviesActedIn){
            actor.actorsWorkedWith.addAll(getActorsInMovie(m.id));
        }
        */
        System.out.println("done");
    }

    private ResultSet database_search(String sqlQuery) throws java.sql.SQLException {
        this.search = this.conn.createStatement();
        ResultSet rs = search.executeQuery(sqlQuery);
        return rs;
    }
}
