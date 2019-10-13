import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

import java.sql.ResultSet;


public class YearCover {
    private ArrayList<Integer> ids_by_year;
    Integer istart_year = 0;
    Integer iend_year = 0;

    public YearCover(String start_year, String end_year, ResultSet active_years) {
        this.ids_by_year = new ArrayList<Integer>();
        this.istart_year = Integer.valueOf(start_year);
        this.iend_year = Integer.valueOf(end_year);

        // build the universes
        HashSet<Integer> yearniverse = new HashSet<Integer>();
        for (Integer i = this.istart_year; i <= this.iend_year; ++i) {
            yearniverse.add(i);
        }
        HashSet<Integer> actorverse = new HashSet<Integer>();

        // build the subsets
        HashMap<Integer, HashSet<Integer>> active_years_by_actor = new HashMap<>();
        try {
            while (active_years.next()) {
                if (null == active_years_by_actor.get(active_years.getInt("id"))) {
                    active_years_by_actor.put(active_years.getInt("id"), new HashSet<Integer>());
                }
                active_years_by_actor.get(active_years.getInt("id")).add(active_years.getInt("year"));
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

        // iteratively...
        while (!actorverse.equals(yearniverse)) {
            // select the actor with the most uncovered years
            Integer maxyearsactorid = -1;
            Integer maxyears = -1;

            for (Map.Entry<Integer, HashSet<Integer>> entry : active_years_by_actor.entrySet()) {
                if (entry.getValue().size() > maxyears) {
                    maxyearsactorid = entry.getKey();
                }
            }

            // add that actor to the actorverse and the printable list of ids by year
            actorverse.addAll(active_years_by_actor.get(maxyearsactorid));
            HashSet<Integer> years = active_years_by_actor.get(maxyearsactorid);
            for (Integer year : years) {
                this.ids_by_year.set(year - this.istart_year, maxyearsactorid);
            }

            // update actors so they only have uncovered years
            for (Map.Entry<Integer, HashSet<Integer>> entry : active_years_by_actor.entrySet()) {
                entry.getValue().removeAll(active_years_by_actor.get(maxyearsactorid));
            }
        }
    }

    public String toString() {
        StringBuilder cover = new StringBuilder();
        for (Integer i = 0; i < this.ids_by_year.size(); ++i) {
            Integer year = Integer.sum(Integer.valueOf(i), this.istart_year);
            cover.append(year.toString()  + ": " + this.ids_by_year.get(i).toString() + '\n');
        }
        return cover.toString();
    }
}
