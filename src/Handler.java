import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Handler {
    public Handler(){

    }
    public String search(HashMap<String, ArrayList<String>> include, HashMap<String, ArrayList<String>> exclude){
        System.out.println(Arrays.asList(include).toString());
        System.out.println(Arrays.asList(exclude).toString());
        return "Output";
    }
    public String search_save(HashMap<String, ArrayList<String>> include, HashMap<String, ArrayList<String>> exclude){
        String filename = "[TO ASSIGN]";
        System.out.println(Arrays.asList(include).toString());
        System.out.println(Arrays.asList(exclude).toString());
        return "Saved to file: " + filename;
    }
}
