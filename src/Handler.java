import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import java.io.PrintWriter;

import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Handler {
    private String baseFilename;
    private Integer query;
    private Connection conn;
    private Statement search;

    public Handler(){
        this.baseFilename = "queryOut.txt";
        this.query = 0;

        this.conn = null;
        this.search = null;
    }

    public String search(HashMap<String, ArrayList<String>> include, HashMap<String, ArrayList<String>> exclude){
        StringBuilder results = new StringBuilder();

        // FIXME: debugging output
        System.out.println(Arrays.asList(include).toString());
        System.out.println(Arrays.asList(exclude).toString());

        // connect to database and write each row in the results of the query to the display string
        database_connect();
        ResultSet rs = database_search(/* FIXME */);
        
        while (rs.next()) {
            results.append(get_result_row(rs));
            results.append("\n");
        }

        return results.toString();
    }

    public String search_save(HashMap<String, ArrayList<String>> include, HashMap<String, ArrayList<String>> exclude){
        // unique name for the file so multiple queries get saved in different files
        String queryFilename = this.baseFilename + this.query.toString();
        PrintWriter writer = new PrintWriter(queryFilename, "UTF-8");

        // FIXME: debugging output
        System.out.println(Arrays.asList(include).toString());
        System.out.println(Arrays.asList(exclude).toString());

        // connect to database and write each row in the results of the query to the file
        database_connect();
        String sqlQuery = database_query_from_input(include, exclude);
        ResultSet rs = database_search(sqlQuery);

        while (rs.next()) {
            writer.println(get_result_row(rs));
        }

        // cleanup
        database_disconnect();
        writer.close();
        this.query++;
        return "Saved to file: " + queryFilename;
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
        }
    }

    private String database_query_from_input(HashMap<String, ArrayList<String>> include, HashMap<String, ArrayList<String>> exclude) {
        String sqlQuery = "SELECT tom hanks FROM movie1"; // FIXME: create a query string from the user input
        return sqlQuery;
    }

    private ResultSet database_search(String sqlQuery) {
        this.search = this.conn.createStatement();
        ResultSet rs = search.executeQuery(sqlQuery);
        return rs;
    }

    private void database_disconnect()) throws java.sql.SQLException {
        this.search.close();
        this.conn.close();
    }

    // does not check if resultset has next, do that in caller
    private String get_result_row(ResultSet rs) {
        StringBuilder row = new StringBuilder();

        int numberFields = rs.getMetaData().getColumnCount();
        for (int i = 0; i < numberFields; ++i) {
            row.append(rs.getString(i));
            row.append("\t");
        }

        return row.toString();
    }
}
