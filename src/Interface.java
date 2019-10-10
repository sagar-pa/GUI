import java.util.ArrayList;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;

public class Interface extends JFrame implements ActionListener{
    static ArrayList<ArrayList<String>> q1Input;
    static String[] q2Input;
    static String[] q3Input;
    static JPanel[] q1Panels,q2Panels,q3Panels;
    static JTextField[] q1In, q2In, q3In;
    static JPanel navBar,searBar;
    static JFrame mainFrame;
    private static Handler sqlhandler;
    private static JOptionPane output;
    private int currentQ;

    public static void main (String[] args){
        Interface s = new Interface();
        sqlhandler = new Handler();
        navBar = new JPanel();
        searBar = new JPanel();

        q1Input = new ArrayList<ArrayList<String>>();
        q2Input = new String[2];
        q3Input = new String[2];
             // create textfield
        q1In = new JTextField[3];
        q2In = new JTextField[2];
        q3In = new JTextField[2];
        q1Panels = new JPanel[3];
        q2Panels = new JPanel[2];
        q3Panels = new JPanel[2];

        // create add and remove buttons
        // add action listener
        JButton loadQ1 = new JButton("Degrees of Separation");
        JButton loadQ2 = new JButton("Smallest Cover");
        JButton loadQ3 = new JButton("Most Similar Actors");
        JButton searchButton = new JButton("Search");
        JButton saveButton = new JButton("Search & Save to file");

        loadQ1.addActionListener(s);
        loadQ2.addActionListener(s);
        loadQ3.addActionListener(s);
        searchButton.addActionListener(s);
        saveButton.addActionListener(s);


        navBar.add(loadQ1);
        navBar.add(loadQ2);
        navBar.add(loadQ3);
        searBar.add(searchButton);
        searBar.add(saveButton);
        // add combobox to panel
        mainFrame = new JFrame("frame");
        mainFrame.setLayout(new FlowLayout());

        // add panel to frame
        mainFrame.add(navBar);
        mainFrame.add(searBar);

        // set the size of frame
        mainFrame.setSize(500, 300);

        mainFrame.show();

    }
    public void actionPerformed(ActionEvent e)
    {
        parse();
        String s = e.getActionCommand();
        try {
            if (s.equals("Search")) {
                String to_display = "pass";
                output.showMessageDialog(null,to_display);
                // f.setContentPane(output);
                //  f.repaint();
                //  f.revalidate();
            }
            else {
                String to_display = "pass";

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


    }

}
