import java.util.HashSet;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Map;

import java.util.ArrayList;

import java.sql.ResultSet;


public class YearCover {
    private Handler handler = null;

    public YearCover(Handler handler) {
        this.handler = handler;
    }

    public String search(Integer start_year, Integer end_year, ArrayList<String> to_exclude) throws java.sql.SQLException {
        // make sure years requested are contiguous in the database
        if (start_year < 1890 || end_year > 2018) {
            return "Start year must be >= 1890 and end year must be <= 2018";
        }

        // make query
        //this.handler.database_connect();
        String sql_query = "SELECT * FROM active_years WHERE year IS NOT NULL AND year >= "
            + start_year.toString() + " AND year <= " + end_year.toString();
        ResultSet active_years = this.handler.databaseSearch(sql_query);

        // setup varjables for the algorithm
        HashMap<Integer, String> name_by_id = new HashMap<Integer, String>();
        TreeMap<Integer, Integer> id_by_year = new TreeMap<Integer, Integer>();

        // build the universes
        HashSet<Integer> yearniverse = new HashSet<Integer>();
        for (Integer i = start_year; i <= end_year; ++i) {
            yearniverse.add(i);
        }
        HashSet<Integer> actorverse = new HashSet<Integer>();

        // build the subsets
        // FIXME: exclude stuff
        HashMap<Integer, HashSet<Integer>> active_years_by_actor = new HashMap<>();
        HashSet<String> exclusions = new HashSet<String>(to_exclude);
        try {
            while (active_years.next()) {
                if (null == active_years_by_actor.get(active_years.getInt("id"))) {
                    if (!exclusions.contains(active_years.getString("name"))) {
                        active_years_by_actor.put(active_years.getInt("id"), new HashSet<Integer>());
                        name_by_id.put(active_years.getInt("id"), active_years.getString("name"));
                    }
                }
                active_years_by_actor.get(active_years.getInt("id")).add(active_years.getInt("year"));
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

        // iteratively...
        Integer maxyearsactorid = -1;
        Integer maxyears = -1;
        Integer entryyears = -1;
        while (!actorverse.equals(yearniverse)) {
            // select the actor with the most uncovered years
            maxyearsactorid = -1;
            maxyears = -1;

            for (Map.Entry<Integer, HashSet<Integer>> entry : active_years_by_actor.entrySet()) {
                entryyears = entry.getValue().size();
                if (entry.getKey() != 0) { // sometimes there is an error with the keys
                    if (entryyears > maxyears) {
                        maxyears = entryyears;
                        maxyearsactorid = entry.getKey();
                    }
                }
            }

            // remove that actor from the available actors and add to the actorverse and the printable list of ids by year
            HashSet<Integer> completedyears = active_years_by_actor.remove(maxyearsactorid);
            actorverse.addAll(completedyears);
            for (Integer year : completedyears) {
                id_by_year.put(year, maxyearsactorid);
            }

            // update actors so they only have uncovered years
            for (Map.Entry<Integer, HashSet<Integer>> entry : active_years_by_actor.entrySet()) {
                entry.getValue().removeAll(completedyears);
            }
        }

        // cleanup
        //this.handler.database_disconnect();
        // return a string representation for the GUI
        StringBuilder cover = new StringBuilder();
        for (Integer year : id_by_year.keySet()) {
            cover.append(year.toString() + ": " + name_by_id.get(id_by_year.get(year)).toString() + '\n');
        }
        return cover.toString();
    }
}
