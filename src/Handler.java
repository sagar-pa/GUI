import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import java.io.PrintWriter;

import java.sql.*;


public class Handler {
    private String baseFilename;
    private Integer query;
    private Connection conn;
    private Statement search;
    private GraphHandler graphHandler;

    public Handler(){
        this.baseFilename = "queryOut";
        this.query = 0;
        this.conn = null;
        this.search = null;
        graphHandler = new GraphHandler(this);
    }

    public String search(Integer questionNum, ArrayList<ArrayList<String>> input) throws java.sql.SQLException {
        database_connect();

        if (questionNum ==1){
            ArrayList<String> to_exclude = new ArrayList<String>();
            String actor1 = input.get(0).get(0);
            String actor2 = input.get(0).get(1);
            if(input.size() > 1){
                to_exclude = input.get(1);
            }
            return graphHandler.search(actor1,actor2,to_exclude);
        }
        else if (questionNum ==2){
            ArrayList<String> to_exclude = new ArrayList<String>();
            int year1 = Integer.parseInt(input.get(0).get(0));
            int year2 = Integer.parseInt(input.get(0).get(1));
            if(input.size() > 1){
                to_exclude = input.get(1);
            }
            return "Search " + Integer.toString(year1) +" " + Integer.toString(year2) + " " + to_exclude.toString();
        }
        else {
            String movie1 = input.get(0).get(0);
            String movie2 = input.get(0).get(1);
            return "Search " + movie1 + " " +movie2;
        }


    }

    public String search_save(Integer questionNum, ArrayList<ArrayList<String>> input) throws java.sql.SQLException {
        if (questionNum ==1){
            ArrayList<String> to_exclude = new ArrayList<String>();
            String actor1 = input.get(0).get(0);
            String actor2 = input.get(0).get(1);
            if(input.size() > 1){
                to_exclude = input.get(1);
            }
            return "Save " + actor1 + " " + actor2 + " " + to_exclude.toString();
        }
        else if (questionNum ==2){
            ArrayList<String> to_exclude = new ArrayList<String>();
            int year1 = Integer.parseInt(input.get(0).get(0));
            int year2 = Integer.parseInt(input.get(0).get(1));
            if(input.size() > 1){
                to_exclude = input.get(1);
            }
            return "Save " + Integer.toString(year1) +" " + Integer.toString(year2) + " " + to_exclude.toString();
        }
        else {
            String movie1 = input.get(0).get(0);
            String movie2 = input.get(0).get(1);
            return "Save " + movie1 + " " +movie2;
        }
    }

    private void database_connect() {
        String driver_name = "org.postgresql.Driver";
        String url = "jdbc:postgresql://db-315.cse.tamu.edu/mikechacko_db";
        String user = "mikechacko";
        String password = "studentpwd";

        try {
            Class.forName(driver_name);
            this.conn = DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    //given an actor, return the most popular castid. returns -1 on error


    private String database_query_from_input(HashMap<String, ArrayList<String>> include, HashMap<String, ArrayList<String>> exclude) throws java.sql.SQLException {
        //for actor only
        String sqlQuery = "SELECT original_title FROM (characters LEFT JOIN movie1 ON characters.movieid = movie1.id) LEFT JOIN \"cast\" ON characters.castid = \"cast\".id WHERE \"cast\".name = ";
        ArrayList<String> actors = include.get("Actor");
        if(actors == null) return null; //no actors found

        String actor = actors.get(0);
        actor = actor.replaceAll("'", "''"); //format apostrophes
        actor = "'" + actor + "';"; //surround with apostrophes and finish with semicolon

        sqlQuery += actor;

        return sqlQuery;
    }

    public ResultSet database_search(String sqlQuery) throws java.sql.SQLException {
        this.search = this.conn.createStatement();
        ResultSet rs = search.executeQuery(sqlQuery);
        return rs;
    }

    private void database_disconnect() throws java.sql.SQLException {
        this.search.close();
        this.conn.close();
    }

    // does not check if resultset has next, do that in caller
    public String get_result_row(ResultSet rs) throws java.sql.SQLException {
        StringBuilder row = new StringBuilder();

        int numberFields = rs.getMetaData().getColumnCount();
        for (int i = 1; i <= numberFields; ++i) {
            row.append(rs.getString(i));
            row.append("\t");
        }

        return row.toString();
    }
}
