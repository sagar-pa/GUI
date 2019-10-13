import java.sql.ResultSet;

public class GraphHandler {
    Handler subset;

    public GraphHandler(Handler subset) {
        this.subset = subset;
    }


    int getMostPopularActor(String actor) throws java.sql.SQLException{
        int mostPopularCastId = -1;
        int mostFrequent = -1;
        StringBuilder results = new StringBuilder();

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
