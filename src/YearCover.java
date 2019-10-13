import java.util.HashSet;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Map;

import java.sql.ResultSet;


public class YearCover {
    //private ArrayList<Integer> ids_by_year;
    private TreeMap<Integer, Integer> id_by_year;
    private HashMap<Integer, String> name_by_id;
    private Integer istart_year = 0;
    private Integer iend_year = 0;

    public YearCover(String start_year, String end_year, ResultSet active_years) {
        this.istart_year = Integer.valueOf(start_year);
        this.iend_year = Integer.valueOf(end_year);
        //this.ids_by_year = new ArrayList<Integer>(this.iend_year - this.istart_year + 1);
        this.name_by_id = new HashMap<Integer, String>();
        this.id_by_year = new TreeMap<Integer, Integer>();

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
                    name_by_id.put(active_years.getInt("id"), active_years.getString("name"));
                }
                active_years_by_actor.get(active_years.getInt("id")).add(active_years.getInt("year"));
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        System.out.println("built subsets"); // FIXME

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
                        System.out.println("new max year actor selected: " + entry.toString());
                    }
                }
            }

            // FIXME FIXME FIXME if the id is still -1 because that year isnt in our database, update everything as needed and continue or throw exception... FIXME FIXME FIXME

            // remove that actor from the available actors and add to the actorverse and the printable list of ids by year
            HashSet<Integer> completedyears = active_years_by_actor.remove(maxyearsactorid);
            actorverse.addAll(completedyears);
            for (Integer year : completedyears) {
                //this.ids_by_year.set(year - this.istart_year, maxyearsactorid);
                //this.ids_by_year.add(year - this.istart_year, maxyearsactorid);
                this.id_by_year.put(year, maxyearsactorid);
            }
            //System.out.println(this.ids_by_year);
            System.out.println("Ids by year: " + this.id_by_year.toString()); // FIXME
            System.out.println("actorverse: " + actorverse.toString()); // FIXME
            System.out.println("yearverse: " + yearniverse.toString()); // FIXME

            // update actors so they only have uncovered years
            for (Map.Entry<Integer, HashSet<Integer>> entry : active_years_by_actor.entrySet()) {
                entry.getValue().removeAll(completedyears);
                //System.out.println("remaining: " + entry.getValue()); // FIXME
            }
            System.out.println("updateed maps"); //  FIXME
            System.out.println("------"); // FIXME
        }
    }

    public String toString() {
        StringBuilder cover = new StringBuilder();
        //for (Integer i = 0; i < this.ids_by_year.size(); ++i) {
        for (Integer year : this.id_by_year.keySet()) {
            //Integer year = Integer.sum(Integer.valueOf(i), this.istart_year);
            //cover.append(year.toString()  + ": " + this.ids_by_year.get(i).toString() + '\n');
            cover.append(year.toString() + ": "+ this.name_by_id.get(this.id_by_year.get(year)).toString() + '\n');
        }
        return cover.toString();
    }
}
