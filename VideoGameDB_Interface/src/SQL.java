import java.sql.*;
import java.util.ArrayList;

public class SQL {

    //region fields
    private String dbFileName = "jdbc:sqlite:G:/University_Backup/Fall2021/comp3380_databases_concepts_and_usage/project/VideoGameDB_Interface/src/Group73_VideoGame_DB.db";
    private String[] queries;
    private String[] tableNames;
    private Connection connection;
    private DatabaseMetaData metaData;
    //endregion fields

    //region constructors
    public SQL() {
        queries = new String[] {"Hello World", "Good Bye World"};
        initialize();
    }
    //endregion constructors

    //region main methods
    public void initialize(){
        connect();
        retrieveTableNames();
    }
    //endregion main methods

    //region getters and setters
    public String[] getQueries() {
        return queries;
    }

    public String[] getTableNames() {
        return tableNames;
    }

    public String[] getTableAttributes(String table){
        return new String[] {"Title", "Rating", "Genre"};
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
    public String[] updateAttributes(String tableName){
        ArrayList<String> attributes = new ArrayList<>();

        return attributes.toArray(new String[attributes.size()]);
    }
    //endregion GUI called methods
}
