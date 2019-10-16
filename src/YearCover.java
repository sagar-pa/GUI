import java.util.HashSet;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Map;

import java.util.ArrayList;

import java.sql.ResultSet;


public class YearCover {
    private Handler handler = null;
    private Integer startYear = -1;
    private Integer endYear = -1;
    private HashSet<String> exclusions = null;

    private static final Integer NO_ACTOR_ID = -2;

    public YearCover(Handler handler) {
        this.handler = handler;
        this.exclusions = new HashSet<String>();
    }

    public String search(Integer inputStartYear, Integer inputEndYear, ArrayList<String> toExclude) throws java.sql.SQLException {
        // make sure years requested are contiguous in the database
        if (inputStartYear < 1890 || inputEndYear > 2018) {
            return "Start year must be >= 1890 and end year must be <= 2018";
        }

        // clear exclusions if the years are different than prior queries
        if (startYear != inputStartYear || endYear != inputEndYear) {
            exclusions.clear();
            startYear = inputStartYear;
            endYear = inputEndYear;
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
        exclusions.addAll(toExclude);
        while (activeYears.next()) {
             if (null == activeYearsByActor.get(activeYears.getInt("id"))) {
                 if (!exclusions.contains(activeYears.getString("name"))) {
                      activeYearsByActor.put(activeYears.getInt("id"), new HashSet<Integer>());
                      nameById.put(activeYears.getInt("id"), activeYears.getString("name"));
                 }
             }
             if (activeYearsByActor.get(activeYears.getInt("id")) != null) {
                 activeYearsByActor.get(activeYears.getInt("id")).add(activeYears.getInt("year"));
             }
        }
        // add a special ID to represent no actor in the case a year can't be covered
        nameById.put(YearCover.NO_ACTOR_ID, "No non-excluded actors cover this year");

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
            // if the largest sub-cover is 0, a year cover can't be built for this range + exclusions
            if (completedYears.isEmpty()) {
                HashSet<Integer> uncoverableYears = new HashSet<Integer>(yearniverse);
                yearniverse.removeAll(actorverse);
                for (Integer uncoverableYear : uncoverableYears) {
                    idByYear.put(uncoverableYear, YearCover.NO_ACTOR_ID);
                }
                // stop trying to cover if a year cover can't be built for this range + exclusions
                break;
            }
            actorverse.addAll(completedYears);
            for (Integer year : completedYears) {
                idByYear.put(year, maxYearsActorId);
            }
            // also add that actor to the exclusions so the next query gives a different year cover
            exclusions.add(nameById.get(maxYearsActorId));

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
