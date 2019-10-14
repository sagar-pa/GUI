import java.sql.ResultSet;
import java.util.Objects;
import org.jgrapht.*;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm.*;
import org.jgrapht.alg.interfaces.*;
import org.jgrapht.alg.shortestpath.*;
import org.jgrapht.graph.*;

import java.util.*;

class Vertex {
    int id;
    boolean isMovie;

    public Vertex(int id, boolean isMovie) {
        this.id = id;
        this.isMovie = isMovie;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vertex vertex = (Vertex) o;
        return id == vertex.id &&
                isMovie == vertex.isMovie;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, isMovie);
    }
}


public class GraphHandler {
    Handler subset;
    int actor1, actor2;
    ArrayList<Integer> exclude;


    public GraphHandler(Handler subset) {
        this.subset = subset;
        exclude= new ArrayList<>();
    }

    private void parseIds(String actor1, String actor2, ArrayList<String> exclude){
        try {
            this.actor1 = getMostPopularActor(actor1);
            this.actor2 = getMostPopularActor(actor2);
            this.exclude.clear();
            for (String actor : exclude){
                this.exclude.add(getMostPopularActor(actor));
            }
        } catch (Exception e){
            System.out.println("fuck");
            System.exit(1);
        }
    }

    public String search(String actor1, String actor2, ArrayList<String> exclude) throws java.sql.SQLException{
        parseIds(actor1,actor2,exclude);
        Graph<Vertex, DefaultEdge> CharacterGraph = null;
        CharacterGraph = new Multigraph<Vertex,DefaultEdge>(DefaultEdge.class);
        StringBuilder to_return = new StringBuilder();
        if(this.actor1 == -1 || this.actor2 == -1){
            return "Actors to find degrees of Separation for not found. \n";
        }
        if(this.exclude.contains(-1)){
            to_return.append("One or more of the actors to exclude not found. Ignoring them. \n");
        }
        int castId,movieId;
        ResultSet rs = subset.database_search("SELECT * FROM characters");
        while(rs.next()){
            if(!rs.getBoolean("isCrew")) {
                castId = rs.getInt("castid");
                movieId = rs.getInt("movieid");
                if(!this.exclude.contains(castId)){
                    Vertex v1 = new Vertex(castId,false);
                    Vertex v2 = new Vertex(movieId, true);
                    CharacterGraph.addVertex(v1);
                    CharacterGraph.addVertex(v2);
                    CharacterGraph.addEdge(v1,v2);
                }

            }

        }
        Vertex to_search1 = new Vertex(this.actor1,false);
        Vertex to_search2 = new Vertex(this.actor2,false);
        DijkstraShortestPath<Vertex, DefaultEdge> dijkstraAlg = new DijkstraShortestPath<>(CharacterGraph);
        GraphPath<Vertex, DefaultEdge> Path = dijkstraAlg.getPath(to_search1,to_search2);
        List<Vertex> vertices= Path.getVertexList();
        int separation = 0;
        for (Vertex v: vertices){
           if(v.isMovie){
               to_return.append(getMovieName(v.id) + ",  ");
               separation++;
           }
           else
           {
               to_return.append(getActorName(v.id) +",  ");
           }

        }
        to_return.append("\n The degrees of Separation between the two actors is: " + Integer.toString(separation)+ ".");
        CharacterGraph = null;
        return to_return.toString();

    }



    int getMostPopularActor(String actor) throws java.sql.SQLException{
        int mostPopularCastId = -1;
        int mostFrequent = -1;

        String sqlQuery = "SELECT castid, COUNT(1) FROM (characters LEFT JOIN movie1 ON characters.movieid = movie1.id) LEFT JOIN \"cast\" ON characters.castid = \"cast\".id WHERE \"cast\".name = 'REPLACEME' AND \"characters\".iscrew = false GROUP BY castid;";
        sqlQuery = sqlQuery.replaceAll("REPLACEME", actor);

        ResultSet rs = subset.database_search(sqlQuery);

        while (rs.next()) {
            int castId = rs.getInt("castid");
            int freq = rs.getInt("count");
            if(freq > mostFrequent) {
                mostFrequent = freq;
                mostPopularCastId = castId;
            }
        }

        return mostPopularCastId;
    }
    public String getMovieName(int id) throws java.sql.SQLException{
        String tosearch = "SELECT * FROM movie1 WHERE id=" + Integer.toString(id);
        String to_return = "default";
        try {
            ResultSet rs = subset.database_search(tosearch);

            while (rs.next()) {
                to_return = rs.getString("original_title");
                to_return = to_return + " (" + rs.getString("release_date").substring(0,4) + ")";
            }
        }
        catch (java.sql.SQLException e){
            System.out.println("FUck");
            e.printStackTrace();
            System.exit(2);
        }
        return to_return;
    }
    public String getActorName(int id) throws java.sql.SQLException{
        String tosearch = "SELECT name FROM \"cast\" WHERE id=" + Integer.toString(id);
        String to_return = "default2";
        try {
            ResultSet rs = subset.database_search(tosearch);

            while (rs.next()) {
                to_return = rs.getString("name");
            }
        }
        catch (java.sql.SQLException e){
            System.out.println("FUUck");
            e.printStackTrace();
            System.exit(2);
        }
        return to_return;

    }

}
