import java.sql.*;
import java.util.ArrayList;

public class SQL {

    //region fields
    private String dbFileName = "jdbc:sqlite:" + System.getProperty("user.dir") + "\\VideoGameDB_Interface\\src\\Group73_VideoGame_DB.db";
    private String[] queries; //sql code
    private String[] queryDesc;
    private String[] tableNames;
    private String[] currAttributeNames;
    private Connection connection;
    private DatabaseMetaData metaData;
    //endregion fields

    //region constructors
    public SQL() {
        System.out.println(dbFileName);
        queries = new String[] {"1. Find all the E-rated strategy games that have been developed by developers located in Canada with a critic score worse than 60%.",
                "2.Find the highest selling game in North America that has a score of above 50% for both user and critic and was made by a publisher that has published over 10 games.",
                "3.Find the game names, publisher names, and developer names of the game which has the lowest average player base across all regions.",
        "4.Find all the games that have Japan sales as their highest sales numbers and were developed in Japan.",
        "5.Find how many players in Europe are playing a game with a user score above 80%? Display alongside with the gameName and genre .",
        "6.Find all the countries that have developed the most games with an average critic and user score of over 80%.",
        "7.Show all the publishers that published a game that is within the top 50 by user scores and is in the Shooter genre.",
        "8.Show each “SoulCalibur” game with over 1 mil NA players.",
        "9.Find the developers that released a game before 2000 and placed it in the top 50 for global sales. Display the dev name, release year, number of global players, critic score and user score.",
        "10.Find all video games that are on only one platform whose total player count across all regions is greater than a million.",
        "11.Find all the warnings for each game rating.",
        "12.Find the 5 worst pokemon game made according to user and critic score."};
        initialize();
    }
    //endregion constructors

    //region main methods
    public void initialize(){
        connect();
        retrieveTableNames();
        updateAttributes(0);
    }
    //endregion main methods

    //region getters and setters
    public String[] getQueries() {
        return queries;
    }

    public String[] getTableNames() {
        return tableNames;
    }

    public String[] getTableAttributes(String selectedTable){
        return currAttributeNames;
    }
    //endregion getters and setters

    //region database connection methods
    private void connect(){
        connection = null;
        try {
            connection = DriverManager.getConnection(dbFileName);
        } catch(SQLException  e){
            e.printStackTrace();
        }
    }
    //endregion database connection methods

    //region database commands
    private void retrieveTableNames(){
       try{
           metaData = connection.getMetaData();
           ResultSet tables = metaData.getTables(null, null,"%" ,null);
           ArrayList<String> namesList = new ArrayList<>();
           while(tables.next()){
                namesList.add(tables.getString("TABLE_NAME"));
           }
           tableNames = namesList.toArray(new String[namesList.size()]);
       } catch (SQLException e){
           e.printStackTrace();
       }
    }
    //endregion database commands

    //region GUI called methods
    public String[] updateAttributes(int index){ //not working
        ArrayList<String> attributes = new ArrayList<>();
        try{
            ResultSet resultSet = metaData.getAttributes(null, null, tableNames[index], "%");
            while (resultSet.next()){
                attributes.add(resultSet.getString("COLUMN_NAME"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        currAttributeNames = attributes.toArray(new String[attributes.size()]);
        return currAttributeNames;
    }
    //endregion GUI called methods
}
