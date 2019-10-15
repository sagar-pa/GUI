import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.sql.*;


public class Handler {
    private String baseFilename;
    private Integer query;
    private Connection conn;
    private Statement search;
    private GraphHandler graphHandler;
    private SimilarActors similarActors;
    private YearCover yearCover;

    public Handler(){
        this.baseFilename = "queryOut";
        this.query = 0;
        this.conn = null;
        this.search = null;
        graphHandler = new GraphHandler(this);
        similarActors = new SimilarActors(this);
        yearCover = new YearCover(this);
        databaseConnect();
    }

    public String search(Integer questionNum, ArrayList<ArrayList<String>> input) throws java.sql.SQLException {
        if (questionNum ==1){
            ArrayList<String> to_exclude = new ArrayList<String>();
            to_exclude.clear();
            String actor1 = input.get(0).get(0);
            String actor2 = input.get(0).get(1);
            if(input.size() > 1){
                to_exclude = input.get(1);
            }
            return graphHandler.search(actor1,actor2,to_exclude);
        }
        else if (questionNum ==2){
            ArrayList<String> toExclude = new ArrayList<String>();
            int year1 = Integer.parseInt(input.get(0).get(0));
            int year2 = Integer.parseInt(input.get(0).get(1));
            if(input.size() > 1){
                toExclude = input.get(1);
            }
            return yearCover.search(year1,year2,toExclude);
        }
        else {
            String movie1 = input.get(0).get(0);
            String movie2 = input.get(0).get(1);
            return similarActors.search(movie1, movie2);
        }
    }

    public String searchSave(Integer questionNum, ArrayList<ArrayList<String>> input) throws java.sql.SQLException {
        String output;
        if (questionNum ==1){
            ArrayList<String> to_exclude = new ArrayList<String>();
            to_exclude.clear();
            String actor1 = input.get(0).get(0);
            String actor2 = input.get(0).get(1);
            if(input.size() > 1){
                to_exclude = input.get(1);
            }
            output = graphHandler.search(actor1,actor2,to_exclude);
            this.baseFilename = "degreesQuery" + Integer.toString(this.query) +".txt";
        }
        else if (questionNum ==2){
            ArrayList<String> to_exclude = new ArrayList<String>();
            int year1 = Integer.parseInt(input.get(0).get(0));
            int year2 = Integer.parseInt(input.get(0).get(1));
            if(input.size() > 1){
                to_exclude = input.get(1);
            }
            output = yearCover.search(year1,year2,to_exclude);
            this.baseFilename = "coverQuery" + Integer.toString(this.query) +".txt";
        }
        else {
            String movie1 = input.get(0).get(0);
            String movie2 = input.get(0).get(1);
            output = similarActors.search(movie1, movie2);
            this.baseFilename = "similarQuery" + Integer.toString(this.query) +".txt";
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(this.baseFilename));
            writer.write(output);
            writer.close();
            this.query++;
            return "Saved to: " + this.baseFilename;
        }
        catch(Exception e) {
            return "Error while saving to file. Please check write permissions.";
        }
    }

    private void databaseConnect() {
        String driverName = "org.postgresql.Driver";
        String url = "jdbc:postgresql://db-315.cse.tamu.edu/mikechacko_db";
        String user = "mikechacko";
        String password = "studentpwd";

        System.out.println("Attempting to connect to database...");

        try {
            Class.forName(driverName);
            this.conn = DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    public ResultSet databaseSearch(String sqlQuery) throws java.sql.SQLException {
        this.search = this.conn.createStatement();
        ResultSet rs = search.executeQuery(sqlQuery);
        return rs;
    }

    public void databaseDisconnect() throws java.sql.SQLException {
        this.search.close();
        this.conn.close();
    }
}
