import java.util.ArrayList;
import java.util.HashMap;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;

public class Interface extends JFrame implements ItemListener, ActionListener{
    private static String droplist[] = {"Select Field","Actor", "Movie", "Genre"};
    private static String droplist2[] = {"Select Action","Include", "Exclude"};
    private static HashMap<String, ArrayList<String>> include;
    private static HashMap<String, ArrayList<String>> exclude;
    static JFrame f;
    // label
    static JComboBox drop1, drop2, drop3, inc1, inc2, inc3;
    // textfield to add and delete items
    static JTextField in1, in2, in3;
    private static Handler sqlhandler;
    private static String[][] Actions;
    private static JOptionPane output;

    public static void main (String[] args){
        Interface s = new Interface();
        include = new HashMap<>();
        sqlhandler = new Handler();
        include.put("Actor",new ArrayList<String>());
        include.put("Movie",new ArrayList<String>());
        include.put("Genre",new ArrayList<String>());
        exclude = new HashMap<>();
        exclude.put("Actor",new ArrayList<String>());
        exclude.put("Movie",new ArrayList<String>());
        exclude.put("Genre",new ArrayList<String>());

        JButton b = new JButton("Search");
        JButton b2 = new JButton("Search & Save to file");
        drop1 = new JComboBox(droplist);
        drop2 = new JComboBox(droplist);
        drop3 = new JComboBox(droplist);
        inc1 = new JComboBox(droplist2);
        inc2 = new JComboBox(droplist2);
        inc3 = new JComboBox(droplist2);
        Actions = new String[3][2];
        for (int i= 0; i<3; i++){
            for (int j =0;j<2;j++){
                Actions[i][j] = "default";
            }
        }
        // create textfield
        in1 = new JTextField(16);
        in2 = new JTextField(16);
        in3 = new JTextField(16);
        // create add and remove buttons
        // add action listener
        b.addActionListener(s);
        b2.addActionListener(s);

        // add ItemListener
        drop1.addItemListener(s);
        drop2.addItemListener(s);
        drop3.addItemListener(s);
        inc1.addItemListener(s);
        inc2.addItemListener(s);
        inc3.addItemListener(s);
        // create labels
        // create a new panel
        JPanel p = new JPanel();
        JPanel p2 = new JPanel();
        JPanel p3 = new JPanel();
        JPanel p4 = new JPanel();

        p.add(drop1);
        p.add(in1);
        p.add(inc1);

        p2.add(drop2);
        p2.add(in2);
        p2.add(inc2);

        p3.add(drop3);
        p3.add(in3);
        p3.add(inc3);
        // add combobox to panel
        p4.add(b);
        p4.add(b2);
        f = new JFrame("frame");
        f.setLayout(new FlowLayout());

        // add panel to frame
        f.add(p);
        f.add(p2);
        f.add(p3);
        f.add(p4);

        // set the size of frame
        f.setSize(500, 300);

        f.show();

    }
    public void actionPerformed(ActionEvent e)
    {
        parse();
        String s = e.getActionCommand();
        try {
            if (s.equals("Search")) {
                String to_display = sqlhandler.search(include,exclude);
                output.showMessageDialog(null,to_display);
                // f.setContentPane(output);
                //  f.repaint();
                //  f.revalidate();
            }
            else {
                String to_display = sqlhandler.search_save(include,exclude);

                output.showMessageDialog(null,to_display);
                //    f.setContentPane(output);
                //  f.repaint();
                //  f.revalidate();
            }
        } catch (Exception except) {
            except.printStackTrace();
        }
    }
    public void parse(){
        include.get("Actor").clear();
        include.get("Movie").clear();
        include.get("Genre").clear();
        exclude.get("Actor").clear();
        exclude.get("Movie").clear();
        exclude.get("Genre").clear();
        for(int i=0; i<3; i++){
                String to_add;
                if(i == 0)
                    to_add = in1.getText();
                else if (i==1)
                    to_add = in2.getText();
                else
                    to_add = in3.getText();
                if(Actions[i][0].equalsIgnoreCase("Actor") && Actions[i][1].equalsIgnoreCase("Include")){
                    include.get("Actor").add(to_add);
                }
                else if (Actions[i][0].equalsIgnoreCase("Actor") && Actions[i][1].equalsIgnoreCase("Exclude")){
                    exclude.get("Actor").add(to_add);
                }
                else if (Actions[i][0].equalsIgnoreCase("Movie") && Actions[i][1].equalsIgnoreCase("Include")){
                    include.get("Movie").add(to_add);
                }
                else if (Actions[i][0].equalsIgnoreCase("Movie") && Actions[i][1].equalsIgnoreCase("Exclude")){
                    exclude.get("Movie").add(to_add);
                }
                else if (Actions[i][0].equalsIgnoreCase("Genre") && Actions[i][1].equalsIgnoreCase("Include")){
                    include.get("Genre").add(to_add);
                }
                else if (Actions[i][0].equalsIgnoreCase("Genre") && Actions[i][1].equalsIgnoreCase("Exclude")){
                    exclude.get("Genre").add(to_add);
                }
        }

    }
    public void itemStateChanged(ItemEvent e)
    {
        // if the state combobox is changed
        if (e.getSource() == drop1) {
            Actions[0][0] = drop1.getSelectedItem().toString();
        }
        else if(e.getSource() == inc1){
            Actions[0][1] = inc1.getSelectedItem().toString();
        }
        else if(e.getSource() == drop2){
            Actions[1][0] = drop2.getSelectedItem().toString();
        }
        else if(e.getSource() == inc2){
            Actions[1][1] = inc2.getSelectedItem().toString();
        }
        else if(e.getSource() == drop3){
            Actions[2][0] = drop3.getSelectedItem().toString();
        }
        else if(e.getSource() == inc3){
            Actions[2][1] = inc3.getSelectedItem().toString();
        }

    }
}
