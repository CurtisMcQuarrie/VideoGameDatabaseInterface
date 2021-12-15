import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

public class GUI implements ActionListener, ItemListener {
    //region fields
    private SQL sql;
    // border fields
    private int prefWidth = 600;
    private int prefHeight = 200;
    private int minWidth = 90;
    private int minHeight = 30;
    // frame fields
    private JFrame frame;
    // panel fields
    private ArrayList<JPanel> panels;
    // colors
    private Color darkestColor = new Color(13, 27, 42);
    private Color darkColor = new Color(27, 38, 59);
    private Color middleColor = new Color(65, 90, 119);
    private Color lightColor = new Color(119,141,169);
    private Color lightestColor = new Color(224,255,221);
    // jObjects
    JButton queryExecuteButton;
    JButton tableExecuteButton;
    JComboBox<String> queryDropdown;
    JComboBox<String> tableDropdown;
    JList<String> attributesDropdown;
    //endregion fields

    //region constructors
    public GUI(){
        frame = new JFrame("Group 73 Video Game DB Interface");
        panels = new ArrayList<>();
    }

    public GUI(String frameTitle){
        frame = new JFrame(frameTitle);
        panels = new ArrayList<>();
    }
    //endregion constructors

    //region main methods
    public void initialize(SQL sql){
        this.sql = sql;
        setupPanels();
        setupFrame();
    }

    public void run(){
        frame.pack();
        frame.setVisible(true);
    }
    //endregion main methods

    //region setup methods
    private void setupFrame() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBackground(darkestColor);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.PAGE_AXIS));
    }

    private void setupPanels(){
        panels.add(setupQueryPanel());
        panels.add(setupTablePanel());
        panels.add(setupResultantPanel());
        for (JPanel panel: panels) {
            panel.setBackground(darkColor);
            panel.setLayout(new FlowLayout());
            panel.setPreferredSize(new Dimension(prefWidth, prefHeight));
            panel.setMinimumSize(new Dimension(minWidth, minHeight));
            frame.add(panel, BorderLayout.CENTER);
        }
    }

    private JPanel setupQueryPanel(){
        JPanel panel = new JPanel();
        TitledBorder titleBorder = BorderFactory.createTitledBorder("SQL Queries");
        titleBorder.setTitleColor(lightestColor);
        panel.setBorder(titleBorder);

        // create components
        JLabel label = new JLabel("Select a Query:\t");
        label.setForeground(lightestColor);
        queryDropdown = new JComboBox<>(sql.getQueries());
        queryExecuteButton = new JButton("Run Query");

        // add components
        panel.add(label);
        panel.add(queryDropdown);
        panel.add(queryExecuteButton);

        return panel;
    }

    private JPanel setupResultantPanel(){
        JPanel panel = new JPanel();
        TitledBorder titleBorder = BorderFactory.createTitledBorder("Resultant Table From Query");
        titleBorder.setTitleColor(lightestColor);
        panel.setBorder(titleBorder);

        // create components
        JTable table = new JTable();
        table.setForeground(lightestColor);
        JScrollPane scrollPane = new JScrollPane(table);
        // add button

        // add components
        panel.add(table);
        panel.add(scrollPane);

        return panel;
    }

    private JPanel setupTablePanel(){
        JPanel panel = new JPanel();
        TitledBorder titleBorder = BorderFactory.createTitledBorder("Table Queries");
        titleBorder.setTitleColor(lightestColor);
        panel.setBorder(titleBorder);

        // create components
        JLabel tableLabel = new JLabel("Table:\t");
        tableLabel.setForeground(lightestColor);
        tableDropdown = new JComboBox<>(sql.getTableNames());
        tableDropdown.setSelectedIndex(0); // set initial index for dropdown
        tableDropdown.addItemListener(this);
        JLabel attributeLabel = new JLabel("\tAttributes:\t");
        attributeLabel.setForeground(lightestColor);
        attributesDropdown = new JList<>(sql.getTableAttributes());
        tableExecuteButton = new JButton("Run Query");

        // add components
        panel.add(tableLabel);
        panel.add(tableDropdown);
        panel.add(attributeLabel);
        panel.add(attributesDropdown);
        panel.add(new JScrollPane(attributesDropdown, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
        panel.add(tableExecuteButton);

        return panel;
    }
    //endregion setup methods

    //region actions
    private void updateAttributesDropdown(){
        DefaultListModel<String> model = new DefaultListModel();
        String[] newAttributes = sql.updateAttributes(tableDropdown.getSelectedIndex());
        for (int index = 0; index < newAttributes.length; index++) {
            model.add(index, newAttributes[index]);
        }
        attributesDropdown.setModel(model);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int getQueryIndex = queryDropdown.getSelectedIndex();
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == tableDropdown){
            updateAttributesDropdown();
        }
    }
    //endregion actions
}
/*
 @Override
    public void actionPerformed(ActionEvent e) {
       Object getQuery=  queryDropDown.getSelectedItem();
        if(getQuery.toString().equals("1. Find all the E-rated strategy games that have been developed by developers located in Canada with a critic score worse than 60%.")){
            System.out.println("1");
        }else{
            if(getQuery.toString().equals("2.Find the highest selling game in North America that has a score of above 50% for both user and critic and was made by a publisher that has published over 10 games.")){
                System.out.println("it works 2");
            }else{
                if(getQuery.toString().equals("3.Find the game names, publisher names, and developer names of the game which has the lowest average player base across all regions.")){
                    System.out.println("it works 3");
                }else{
                    if(getQuery.toString().equals("4.Find all the games that have Japan sales as their highest sales numbers and were developed in Japan.")){
                        System.out.println("it works 4");
                    }else{
                        if(getQuery.toString().equals("5.Find how many players in Europe are playing a game with a user score above 80%? Display alongside with the gameName and genre .")){
                            System.out.println("5");
                        }else{
                            if(getQuery.toString().equals("6.Find all the countries that have developed the most games with an average critic and user score of over 80%.")){
                                System.out.println("6");
                            }else{
                                if(getQuery.toString().equals("7.Show all the publishers that published a game that is within the top 50 by user scores and is in the Shooter genre.")){
                                    System.out.println("7");
                                }else{
                                    if(getQuery.toString().equals("8.Show each “SoulCalibur” game with over 1 mil NA players.")){
                                        System.out.println("8");
                                    }else{
                                        if(getQuery.toString().equals("9.Find the developers that released a game before 2000 and placed it in the top 50 for global sales. Display the dev name, release year, number of global players, critic score and user score.")){
                                            System.out.println("9");
                                        }else{
                                            if(getQuery.toString().equals("10.Find all video games that are on only one platform whose total player count across all regions is greater than a million.")){
                                                System.out.println("10");

                                            }else{
                                                if(getQuery.toString().equals("11.Find all the warnings for each game rating.")){
                                                    System.out.println("11");
                                                }else{
                                                    if(getQuery.toString().equals("12.Find the 5 worst pokemon game made according to user and critic score.")){
                                                        System.out.println("12");
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    //endregion setup methods

 */