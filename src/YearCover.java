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

    public String search(Integer startYear, Integer endYear, ArrayList<String> toExclude) throws java.sql.SQLException {
        // make sure years requested are contiguous in the database
        if (startYear < 1890 || endYear > 2018) {
            return "Start year must be >= 1890 and end year must be <= 2018";
        }

        // make query
        String sqlQuery = "SELECT * FROM active_years WHERE year IS NOT NULL AND year >= "
            + startYear.toString() + " AND year <= " + endYear.toString();
        ResultSet activeYears = this.handler.databaseSearch(sqlQuery);

        // setup variables for the algorithm
        HashMap<Integer, String> nameById = new HashMap<Integer, String>();
        TreeMap<Integer, Integer> idByYear = new TreeMap<Integer, Integer>();

        // build the universes
        HashSet<Integer> yearniverse = new HashSet<Integer>();
        for (Integer i = startYear; i <= endYear; ++i) {
            yearniverse.add(i);
        }
        HashSet<Integer> actorverse = new HashSet<Integer>();

        // build the subsets
        HashMap<Integer, HashSet<Integer>> activeYearsByActor = new HashMap<>();
        HashSet<String> exclusions = new HashSet<String>(toExclude);
        while (activeYears.next()) {
             if (null == activeYearsByActor.get(activeYears.getInt("id"))) {
                 if (!exclusions.contains(activeYears.getString("name"))) {
                      activeYearsByActor.put(activeYears.getInt("id"), new HashSet<Integer>());
                      nameById.put(activeYears.getInt("id"), activeYears.getString("name"));
                 }
             }
             if(activeYearsByActor.get(activeYears.getInt("id")) != null)
             activeYearsByActor.get(activeYears.getInt("id")).add(activeYears.getInt("year"));
        }
        // iteratively...
        Integer maxYearsActorId = -1;
        Integer maxYears = -1;
        Integer entryYears = -1;
        while (!actorverse.equals(yearniverse)) {
            // select the actor with the most uncovered years
            maxYearsActorId = -1;
            maxYears = -1;

            for (Map.Entry<Integer, HashSet<Integer>> entry : activeYearsByActor.entrySet()) {
                entryYears = entry.getValue().size();
                if (entry.getKey() != 0) { // sometimes there is an error with the keys
                    if (entryYears > maxYears) {
                        maxYears = entryYears;
                        maxYearsActorId = entry.getKey();
                    }
                }
            }

            // remove that actor from the available actors and add to the actorverse and the printable list of ids by year
            HashSet<Integer> completedYears = activeYearsByActor.remove(maxYearsActorId);
            actorverse.addAll(completedYears);
            for (Integer year : completedYears) {
                idByYear.put(year, maxYearsActorId);
            }

            // update actors so they only have uncovered years
            for (Map.Entry<Integer, HashSet<Integer>> entry : activeYearsByActor.entrySet()) {
                entry.getValue().removeAll(completedYears);
            }
        }

        // return a string representation for the GUI
        StringBuilder cover = new StringBuilder();
        for (Integer year : idByYear.keySet()) {
            cover.append(year.toString() + ": " + nameById.get(idByYear.get(year)).toString() + '\n');
        }
        return cover.toString();
    }
}
