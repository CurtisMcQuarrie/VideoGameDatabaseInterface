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
        queries = new String[] {"Hello World", "Good Bye World"};
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
