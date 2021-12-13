import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;

public class GUI {
    //region fields
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
        setupPanels(sql);
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

    private void setupPanels(SQL sql){
        panels.add(setupQueryPanel(sql));
        panels.add(setupTablePanel(sql));
        panels.add(setupResultantPanel());
        for (JPanel panel: panels) {
            panel.setBackground(darkColor);
            panel.setLayout(new FlowLayout());
            panel.setPreferredSize(new Dimension(prefWidth, prefHeight));
            panel.setMinimumSize(new Dimension(minWidth, minHeight));
            frame.add(panel, BorderLayout.CENTER);
        }
    }

    private JPanel setupQueryPanel(SQL sql){
        JPanel panel = new JPanel();
        TitledBorder titleBorder = BorderFactory.createTitledBorder("SQL Queries");
        titleBorder.setTitleColor(lightestColor);
        panel.setBorder(titleBorder);

        // create components
        JLabel label = new JLabel("Select a Query:\t");
        label.setForeground(lightestColor);
        JComboBox<String> queryDropDown = new JComboBox<>(sql.getQueries());
        JButton button = new JButton("Run Query");

        // add components
        panel.add(label);
        panel.add(queryDropDown);
        panel.add(button);

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

    private JPanel setupTablePanel(SQL sql){
        JPanel panel = new JPanel();
        TitledBorder titleBorder = BorderFactory.createTitledBorder("Table Queries");
        titleBorder.setTitleColor(lightestColor);
        panel.setBorder(titleBorder);

        // create components
        JLabel tableLabel = new JLabel("Table:\t");
        tableLabel.setForeground(lightestColor);
        JComboBox<String> tableDropDown = new JComboBox<>(sql.getTableNames());
        tableDropDown.setSelectedIndex(0); // set initial index for dropdown
        int selectedTable = tableDropDown.getSelectedIndex();
        JLabel attributeLabel = new JLabel("\tAttributes:\t");
        attributeLabel.setForeground(lightestColor);
        JComboBox<String> attributeDropDown = new JComboBox<>(sql.getTableAttributes());

        // add components
        panel.add(tableLabel);
        panel.add(tableDropDown);
        panel.add(attributeLabel);
        panel.add(attributeDropDown);

        return panel;
    }
    //endregion setup methods

}
