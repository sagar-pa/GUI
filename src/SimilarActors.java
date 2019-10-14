import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import java.io.PrintWriter;

import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class SimilarActors {
    private String baseFilename;
    private Integer query;
    private Connection conn;
    private Statement search;

    public SimilarActors(Connection conn){
        if(conn == null) {
            System.out.println("SimilarActors: Connection is bad.");
        }
        this.conn = conn;
    }

    //given two movies, print two actors who are most similar and give the reasons
    void compute(String movie1, String movie2) throws java.sql.SQLException {

        ArrayList<Actor> actorsInMovie1 = getActorsInMovie(movie1);
        ArrayList<Actor> actorsInMovie2 = getActorsInMovie(movie2);

        System.out.println("Actor in movie 1");
        for(Actor a : actorsInMovie1){
            printActorName(a);
        }

        System.out.println("Actor in movie 2");
        for(Actor a : actorsInMovie2){
            printActorName(a);
        }

        System.out.println("Actor from movie 1: ");
        System.out.println("Actor from movie 2: ");
        System.out.println("Reasons: ");
    }

    void printActorName(Actor a) {
        System.out.println("castid " + a.castid + ": " + a.name);
    }

    //Returns a list of Actors who act in 'movie'
    ArrayList<Actor> getActorsInMovie(String movie) throws java.sql.SQLException {
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
        ArrayList<Actor> list = new ArrayList<Actor>();

        while (rs.next()) {
            int castId = rs.getInt("castid");
            String actor_name = rs.getString("actor_name");
            String character_name = rs.getString("character_name");

            Actor a = new Actor(castId, actor_name, character_name);
            list.add(a);
        }

        return list;
    }

    private ResultSet database_search(String sqlQuery) throws java.sql.SQLException {
        this.search = this.conn.createStatement();
        ResultSet rs = search.executeQuery(sqlQuery);
        return rs;
    }
}
