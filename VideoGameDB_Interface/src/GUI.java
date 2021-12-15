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
    JComboBox<String> attributesDropDown;
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
        attributesDropDown = new JComboBox<>(sql.getTableAttributes());
        tableExecuteButton = new JButton("Run Query");

        // add components
        panel.add(tableLabel);
        panel.add(tableDropdown);
        panel.add(attributeLabel);
        panel.add(attributesDropDown);
        panel.add(tableExecuteButton);

        return panel;
    }
    //endregion setup methods

    //region actions
    private void updateAttributesDropdown(){
        attributesDropDown.setModel(new DefaultComboBoxModel(sql.updateAttributes(tableDropdown.getSelectedIndex())));
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == tableDropdown){
            updateAttributesDropdown();
        }
    }
    //endregion actions
}
