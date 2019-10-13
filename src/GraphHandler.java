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
    List<Integer> exclude;
    Graph<Vertex, DefaultEdge> CharacterGraph;

    public GraphHandler(Handler subset) {
        this.subset = subset;
        CharacterGraph = new Multigraph<Vertex,DefaultEdge>(DefaultEdge.class);
        exclude= new ArrayList<Integer>();
    }

    private void parseIds(String actor1, String actor2, ArrayList<String> exclude){
        try {
            this.actor1 = getMostPopularActor(actor1);
            this.actor2 = getMostPopularActor(actor2);
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
        StringBuilder to_return = new StringBuilder();
        if(this.actor1 == -1 || this.actor2 == -1){
            return "Actors to find degrees of Separation for not found.";
        }
        if(this.exclude.contains(-1)){
            to_return.append("One or more of the actors to exclude not found. Ignoring them.");
        }
        int castId,movieId;
        ResultSet rs = subset.database_search("SELECT * FROM characters");
        while(rs.next()){
            if(!rs.getBoolean("isCrew")) {
                castId = rs.getInt("castid");
                movieId = rs.getInt("movieid");
                if(!exclude.contains(castId)){
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
        to_return.append(Path.toString());
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

}
