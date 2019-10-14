import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.awt.event.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;

public class Interface extends JFrame implements ActionListener{
    private static ArrayList<ArrayList<String>> Input;
    private static JPanel[] q1Panels,q2Panels,q3Panels;
    private static JTextField[] q1In, q2In, q3In;
    private static JPanel navBar,searBar;
    private static JFrame mainFrame;
    private static Handler sqlhandler;
    private static JOptionPane output;
    private static int currentQ;

    public static void main (String[] args){
        Interface s = new Interface();
        sqlhandler = new Handler();
        navBar = new JPanel();
        searBar = new JPanel();

        Input = new ArrayList<ArrayList<String>>();

        // Initialize arrays
        q1In = new JTextField[3];
        q2In = new JTextField[3];
        q3In = new JTextField[2];
        q1Panels = new JPanel[4];
        q2Panels = new JPanel[4];
        q3Panels = new JPanel[3];

        for(int i=0;i<2; i++){
            q3In[i] = new JTextField(16);
        }

        for(int i=0;i<3; i++){
            q3Panels[i] = new JPanel();
            q1In[i] = new JTextField(16);
            q2In[i] = new JTextField(16);
        }

        for(int i=0;i<4; i++){
            q1Panels[i] = new JPanel();
            q2Panels[i] = new JPanel();
        }

        // create add and remove buttons
        JButton loadQ1 = new JButton("Degrees of Separation");
        JButton loadQ2 = new JButton("Smallest Cover");
        JButton loadQ3 = new JButton("Most Similar Actors");
        JButton searchButton = new JButton("Search");
        JButton saveButton = new JButton("Search & Save to file");

        // add action listener
        loadQ1.addActionListener(s);
        loadQ2.addActionListener(s);
        loadQ3.addActionListener(s);
        searchButton.addActionListener(s);
        saveButton.addActionListener(s);


        navBar.add(loadQ1);
        navBar.add(loadQ2);
        navBar.add(loadQ3);
        navBar.setBackground(new Color(150,158,152));
        searBar.add(searchButton);
        searBar.add(saveButton);

        //Question 1 panels initialization
        Map<TextAttribute, Integer> fontAttributes = new HashMap<TextAttribute, Integer>();
        fontAttributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        Font titleFont = new Font("Times New Roman", Font.BOLD,14).deriveFont(fontAttributes);
        JLabel q1Title = new JLabel("Degrees of Separation between 2 actors");
        q1Title.setFont(titleFont);
        q1Panels[0].add(q1Title);
        q1Panels[1].add(new JLabel("1st Actor "));
        q1Panels[1].add(q1In[0]);
        q1Panels[2].add(new JLabel("2nd Actor "));
        q1Panels[2].add(q1In[1]);
        q1Panels[3].add(new JLabel("Actors to exclude (separated by comma) "));
        q1Panels[3].add(q1In[2]);

        //Question 2 panels initialization
        JLabel q2Title = new JLabel("Smallest number of Actors/Directors that cover given time");
        q2Title.setFont(titleFont);
        q2Panels[0].add(q2Title);
        q2Panels[1].add(new JLabel("Starting year "));
        q2Panels[1].add(q2In[0]);
        q2Panels[2].add(new JLabel("Ending year "));
        q2Panels[2].add(q2In[1]);
        q2Panels[3].add(new JLabel("Actors/Directors to exclude (separated by comma) "));
        q2Panels[3].add(q2In[2]);
        //Question 3 panels initialization
        JLabel q3Title = new JLabel("Most similar actors between two movies");
        q3Title.setFont(titleFont);
        q3Panels[0].add(q3Title);
        q3Panels[1].add(new JLabel("1st Movie "));
        q3Panels[1].add(q3In[0]);
        q3Panels[2].add(new JLabel("2nd Movie "));
        q3Panels[2].add(q3In[1]);


        mainFrame = new JFrame("Movia");
        mainFrame.setLayout(new FlowLayout());

        // Start with question 1
        currentQ = 1;
        mainFrame.add(navBar);
        for (JPanel panel:q1Panels){
            mainFrame.add(panel);
        }
        mainFrame.add(searBar);

        // Show to user
        mainFrame.setSize(450, 250);
        mainFrame.setVisible(true);

    }
    public void actionPerformed(ActionEvent e)
    {
        String s = e.getActionCommand();
        try {
            if(s.equals("Degrees of Separation")){
                Input.clear();
                currentQ = 1;
                mainFrame.getContentPane().removeAll();
                mainFrame.getContentPane().invalidate();
                mainFrame.add(navBar);
                for (JPanel panel:q1Panels){
                    mainFrame.add(panel);
                }
                mainFrame.add(searBar);
                mainFrame.getContentPane().revalidate();
                mainFrame.repaint();
                mainFrame.setVisible(true);
            }
            else if(s.equals("Smallest Cover")){
                Input.clear();
                currentQ = 2;
                mainFrame.getContentPane().removeAll();
                mainFrame.getContentPane().invalidate();
                mainFrame.add(navBar);
                for (JPanel panel:q2Panels){
                    mainFrame.add(panel);
                }
                mainFrame.add(searBar);
                mainFrame.getContentPane().revalidate();
                mainFrame.repaint();
                mainFrame.setVisible(true);
            }

            else if(s.equals("Most Similar Actors")){
                Input.clear();
                currentQ = 3;
                mainFrame.getContentPane().removeAll();
                mainFrame.getContentPane().invalidate();
                mainFrame.add(navBar);
                for (JPanel panel:q3Panels){
                    mainFrame.add(panel);
                }
                mainFrame.add(searBar);
                mainFrame.getContentPane().revalidate();
                mainFrame.repaint();
                mainFrame.setVisible(true);
            }
            else if (s.equals("Search")) {
                boolean success = parse();
                if (success){
                    String to_display = sqlhandler.search(currentQ,Input);
                    output.showMessageDialog(null,to_display);
                }
            }
            else {
                boolean success = parse();
                if(success) {
                    String to_display = sqlhandler.search_save(currentQ,Input);
                    output.showMessageDialog(null, to_display);
                }
            }
        } catch (Exception except) {
            output.showMessageDialog(null,"Try again");
        }
    }
    private boolean parse(){
        // Just to assert
        Input.clear();
        if(currentQ == 1){
            if(q1In[0].getText().isBlank() || q1In[1].getText().isBlank()){
                output.showMessageDialog(null,"Please enter the 2 required " +
                        "actors to find Degrees of separation for.");
                return false;
            }
            Input.add(new ArrayList<String>());
            Input.get(0).add(q1In[0].getText().replaceAll("'","''"));
            Input.get(0).add(q1In[1].getText().replaceAll("'","''"));
            if (!q1In[2].getText().isBlank()){
                String to_exclude[]= q1In[2].getText().split(",");
                Input.add(new ArrayList<String>());
                for(String temp:to_exclude){
                    Input.get(1).add(temp.replaceAll("'","''"));
                }
            }
        }
        else if(currentQ == 2) {
            if ((q2In[0].getText().length() < 4) || (q2In[1].getText().length() < 4)) {
                output.showMessageDialog(null, "Please enter 2 valid years required " +
                        "to find smallest cover set for.");
                return false;
            }
            Input.add(new ArrayList<String>());
            try{
                Integer small = Integer.parseInt(q2In[0].getText());
                Integer big = Integer.parseInt(q2In[1].getText());
                if (small > big){
                    Integer temp = small;
                    small = big;
                    big = temp;
                }
                Input.get(0).add(small.toString());
                Input.get(0).add(big.toString());
                if (!q2In[2].getText().isBlank()){
                    String to_exclude[]= q2In[2].getText().split(",");
                    Input.add(new ArrayList<String>());
                    for(String temp:to_exclude){
                        Input.get(1).add(temp.replaceAll("'","''"));
                    }
                }

            } catch (NumberFormatException e) {
                output.showMessageDialog(null, "Please enter 2 valid years required " +
                        "to find smallest cover set for.");
                return false;
            }
        }

        else {
            if ((q3In[0].getText().isBlank()) || (q3In[1].getText().isBlank())) {
                output.showMessageDialog(null, "Please enter 2 valid movies required " +
                        "to find the most similar actors between them.");
                return false;
            }
            Input.add(new ArrayList<String>());
            Input.get(0).add(q3In[0].getText().replaceAll("'","''"));
            Input.get(0).add(q3In[1].getText().replaceAll("'","''"));
        }
        return true;
    }

}
