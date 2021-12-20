import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

public class GUI implements ActionListener, ItemListener {
    //region fields
    private SQL sql;
    // border fields
    private int prefWidth = 900;
    private int prefHeight = 200;
    private int minWidth = 900;
    private int minHeight = 200;
    // font fields
    private String fontName = Font.SANS_SERIF;
    private int fontStyle = Font.PLAIN;
    private int fontSize = 20;
    private Font font = new Font(fontName, fontStyle, fontSize);
    // colors
    private Color darkestColor = new Color(13, 27, 42);
    private Color darkColor = new Color(27, 38, 59);
    private Color middleColor = new Color(65, 90, 119);
    private Color lightColor = new Color(119,141,169);
    private Color lightestColor = new Color(224,255,221);
    // jObjects
    private JFrame frame;
    private ArrayList<JPanel> panels;
    private JButton queryExecuteButton;
    private JButton tableExecuteButton;
    private JComboBox<String> queryDropdown;
    private JComboBox<String> tableDropdown;
    private JList<String> attributeList;
    private JScrollPane attributeListScroller;
    private JTable resultTable;
    private JScrollPane resultTableScroller;
    private JButton toCSVButton;
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
        setupFrame();
        setupPanels();
    }

    public void run(){
        frame.pack();
        frame.setVisible(true);
    }
    //endregion main methods

    //region setup methods
    private void setupFrame() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.PAGE_AXIS));
    }

    private void setupPanels(){
        panels.add(setupQueryPanel());
        panels.add(setupTablePanel());
        panels.add(setupResultantPanel());
        for (JPanel panel: panels) {
            panel.setBackground(darkestColor);
            panel.setLayout(new FlowLayout());
            panel.setMinimumSize(new Dimension(minWidth, minHeight));
            frame.add(panel);
        }
    }

    private JPanel setupQueryPanel(){
        JPanel panel = createPanel("SQL Queries", new BorderLayout());
        panel.setPreferredSize(new Dimension(prefWidth, prefHeight));

        // create components
        JLabel label = createLabel("Select a Query:");

        queryDropdown = new JComboBox<>(sql.getQueries());

        queryExecuteButton = new JButton("Run");
        queryExecuteButton.setFont(font);
        queryExecuteButton.addActionListener(this);

        // add components
        panel.add(label);
        panel.add(queryDropdown);
        panel.add(queryExecuteButton);

        return panel;
    }

    private JPanel setupResultantPanel(){
        JPanel panel = createPanel("Resultant Set", new BorderLayout());
        panel.setPreferredSize(new Dimension(prefWidth, prefHeight+50));

        // create components
        resultTable = new JTable();
        resultTable.setForeground(lightestColor);
        resultTable.setBackground(darkestColor);

        resultTableScroller  = new JScrollPane(resultTable);
        resultTableScroller.setPreferredSize(new Dimension(800, 150));

        toCSVButton = new JButton("Write CSV");
        toCSVButton.addActionListener(this);

        panel.add(resultTableScroller, BorderLayout.CENTER);
        panel.add(toCSVButton);

        return panel;
    }

    private JPanel setupTablePanel(){
        JPanel panel = createPanel("Table Queries", new BorderLayout());
        panel.setPreferredSize(new Dimension(prefWidth, prefHeight));

        // create components
        JLabel tableLabel = createLabel("Table:");

        tableDropdown = new JComboBox<>(sql.getTableNames());
        tableDropdown.setSelectedIndex(0); // set initial index for dropdown
        tableDropdown.addItemListener(this);

        JLabel attributeLabel = createLabel("Attributes:");

        attributeList = new JList<>(sql.getTableAttributes());
        attributeList.setSelectedIndex(0);
        attributeList.setVisibleRowCount(5);
        attributeListScroller = new JScrollPane(attributeList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        attributeListScroller.setPreferredSize(new Dimension(115,90));
        tableExecuteButton = new JButton("Run");
        tableExecuteButton.setFont(font);
        tableExecuteButton.addActionListener(this);

        // add components
        panel.add(tableLabel);
        panel.add(tableDropdown);
        panel.add(attributeLabel);
        panel.add(attributeListScroller);
        panel.add(tableExecuteButton);

        return panel;
    }
    //endregion setup methods

    //region jcomponent creation methods
    private JLabel createLabel(String label){
        JLabel jLabel = new JLabel(label);
        jLabel.setForeground(lightestColor);
        jLabel.setFont(font);
        return jLabel;
    }

    private JPanel createPanel(String title, LayoutManager layoutManager){
        JPanel panel = new JPanel(layoutManager);
        TitledBorder titleBorder = BorderFactory.createTitledBorder(title);
        titleBorder.setTitleColor(lightestColor);
        titleBorder.setTitleFont(font);
        panel.setBorder(titleBorder);
        return panel;
    }
    //endregion jcomponents creation methods

    //region listener methods
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == queryExecuteButton){
            sql.executeMadeQueries(queryDropdown.getSelectedIndex());
            resultTable.setModel(new DefaultTableModel(sql.getCurrTableData(), sql.getCurrTableColumnNames()));
        }
        else if (e.getSource() == tableExecuteButton){
            List<String> attributesList = this.attributeList.getSelectedValuesList();
            String[] attributesArray = null;
            if (attributesList.size() > 0){
                attributesArray = attributesList.toArray(new String[attributesList.size()]);
            }
            sql.executeTableQueries(tableDropdown.getSelectedIndex(), attributesArray);
            resultTable.setModel(new DefaultTableModel(sql.getCurrTableData(), sql.getCurrTableColumnNames()));
        }
        else if (e.getSource() == toCSVButton){
            sql.exportToCsv(sql.getCSVText());
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == tableDropdown){
            attributeList.setListData(sql.updateAttributes(tableDropdown.getSelectedIndex()));
        }
    }
    //endregion listener methods
}