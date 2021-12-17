import java.sql.*;
import java.util.ArrayList;
import java.io.*;
import java.util.Scanner ;

public class SQL {

    //region fields
    private String dbFileName = "jdbc:sqlite:" + System.getProperty("user.dir") + "\\VideoGameDB_Interface\\src\\Group73_VideoGame_DB.db";
    private String[] queries; //sql code
    private String[] sqlQueries;
    private String[] tableNames;
    private String[] currAttributeNames;
    private Connection connection;
    private DatabaseMetaData metaData;


    //endregion fields

    //region constructors
    public SQL() {
        queries = new String[] {"Find all E-rated strategy games that have been developed by developers located in Canada, with a critic score worse than 60%.",
                "Find the highest selling game in North America that has a user and critic score of above 50%. The publisher needs to have published over 10 games.",
                "Find the games which have the lowest average player base across all regions.",
                "Find the games that have Japan sales as their highest sales numbers and were developed in Japan.",
                "Find how many players in Europe are playing a game with a user score above 80%.",
                "Find the countries that have developed the most games with an average critic and user score over 80%.",
                "Show all the publishers that published a Shooter game that is within the top 50 user scores.",
                "Show 'SoulCalibur' games that have over 1 million North American players.",
                "Find developers that released a game before 2000 and placed in the top 50 for global sales.",
                "Find video games that are on only available on one platform and whose total player count across all regions is greater than a million.",
                "Find all the warnings for each game rating.",
                "Find the 5 worst pokemon game made according to user and critic score."};

        sqlQueries = new String[] {"SELECT gameName, DDOff.devName, LOC.country FROM (Developed D LEFT JOIN DeveloperOffices DOff ON D.devName=DOff.devName) DDOff\n" +
                "LEFT JOIN Locations Loc ON DDOff.locID=Loc.locID WHERE Loc.country='Canada' AND gameName IN\n" +
                "(SELECT gameName FROM Ratings WHERE rating='E' UNION SELECT gameName FROM PublishedVideoGames\n" +
                "WHERE genre='strategy' UNION SELECT gameName FROM Scores WHERE scoreType='Critic' AND scoreValue<=60);",
                "SELECT gameName,platform, pubName, naSales from PublishedVideoGames natural join publishers natural join sales where gamesPublished > 10 AND gameName in( select gameName from PublishedVideoGames natural join sales natural join scores where (scoreValue/outOf > 0.5)) order by naSales Desc ;",
                "SELECT gameName, platform, pubName, devName, (globalCount + naCount + euCount + jpCount + otherCount)/5 AS avgPlayerCount\n" +
                "FROM PublishedVideoGames NATURAL JOIN PlayedBy NATURAL JOIN Players NATURAL JOIN Developed\n" +
                "WHERE avgPlayerCount in (SELECT MIN((globalCount + naCount + euCount + jpCount + otherCount)/5) AS minPlayerCount\n" +
                "FROM PublishedVideoGames NATURAL JOIN PlayedBy NATURAL JOIN Players NATURAL JOIN Developed);",
                "select country,gameName,platform from Locations natural join DeveloperOffices \n" +
                "natural join Developed where country='Japan' and gameName in(\n" +
                "select gameName from sales where jpSales> naSales and jpSales>euSales and jpSales>otherSales ) \n",
                "SELECT gameName, platform, euCount AS num_Of_EU_Players\n" +
                "FROM PlayedBy PB LEFT JOIN Players P ON PB.playerGroupID=P.playerGroupID WHERE gameName IN (SELECT PVG.gameName\n" +
                "FROM PublishedVideoGames PVG LEFT JOIN Scores S ON PVG.gameName=S.gameName\n" +
                "WHERE S.scoreType='User' AND S.scoreValue>=8 GROUP BY PVG.gameName, PVG.platform) ORDER BY euCount DESC;",
                "SELECT distinct country FROM Locations WHERE country in ( SELECT country FROM PublishedVideoGames NATURAL JOIN Developed NATURAL JOIN DeveloperOffices NATURAL JOIN Locations NATURAL JOIN Scores GROUP BY gameName, platform HAVING AVG(scoreValue/outof) > 0.8);\n","" +
                "SELECT pubName, gameName, platform, scoreValue\n" +
                "FROM (SELECT  * FROM PublishedVideoGames PVG LEFT JOIN Scores S ON PVG.gameName=S.gameName AND PVG.platform=S.platform\n" +
                "WHERE scoreType='User' ORDER BY scoreValue DESC LIMIT 50) WHERE genre='Shooter';",
                "select gameName,platform,naCount from PublishedVideoGames natural join playedBy natural join players where naCount > 1 And gameName like 'SoulCalibur%' ;\n",
                "select Developed.devName, PublishedVideoGames.releaseYear, sales.globalSales,\n" +
                "players.globalCount as numOfPlayers, Scores.scoreValue/Scores.outOf as score\n" +
                "from Developed natural join PublishedVideoGames\n" +
                "natural join sales natural join scores natural join PlayedBy \n" +
                "natural join Players where releaseYear < 2000\n" +
                "order by globalSales DESC, numOfPlayers DESC, score DESC Limit 50\n",
                "select gameName,platform,globalCount from PublishedVideoGames natural join players natural join\n" +
                "PlayedBy where globalCount > 1 GROUP BY gameName HAVING count(platform)=1;",
                "SELECT rating, MAX(drugRef) AS drugRef, MAX(blood) As blood, MAX(bloodAndGore) AS bloodAndGore, MAX(cartoonViolence) AS cartoonViolence, MAX(fantasyViolence) AS fantasyViolence, MAX(animatedBlood) AS animatedBlood, MAX(alcoholRef) AS alcoholRef, MAX(crudeHumor) AS crudeHumor\n" +
                "FROM Ratings NATURAL JOIN GameRatings\n" +
                "GROUP BY rating;",
                "SELECT gameName, platform, AVG(scoreValue/outOf) as avg_percent FROM Scores WHERE gameName LIKE '%Pokemon%'\n" +
                "GROUP BY gameName, platform ORDER BY avg_percent ASC LIMIT 5;"};
        initialize();

    }

    public void exportToCsv(String result) {

        String new_result = "" ;
        Scanner scan = new Scanner(result) ;
        String headers =  scan.nextLine() ;

        System.out.println(headers) ;

        String[] columnsLength = headers.split(",");

        for(int i = 0 ; i <result.length() ; i++){



        }


        try {



         //   String[] columnsLength = headers.split(",") ;

            //System.out.println(columnsLength.length) ;


            FileWriter csvFile = new FileWriter("csvOutput.CSV") ;
            csvFile.write(result);
            csvFile.close() ;

        }

        catch(Exception e){
            e.printStackTrace();
        }
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

    public String[] getTableAttributes(){
        return currAttributeNames;
    }

    public String[] getSqlQueries() {
        return sqlQueries;
    }
//endregion getters and setters

    //region database connection methods
    private void connect(){
        connection = null;
        try {
            connection = DriverManager.getConnection(dbFileName);
        } catch(SQLException e){
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
    public String[] updateAttributes(int index){
        ArrayList<String> attributes = new ArrayList<>();
        attributes.add("*");
        try{
            ResultSet resultSet = metaData.getColumns(null, null, tableNames[index], "%");
            while (resultSet.next()){
                attributes.add(resultSet.getString("COLUMN_NAME"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        currAttributeNames = attributes.toArray(new String[attributes.size()]);
        return currAttributeNames;
    }

    public String executeMadeQueries(int index){
        String result = "";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQueries[index]);
            result = formatResultantSet(resultSet);
        } catch (SQLException e){
            e.printStackTrace();
        }

        exportToCsv(result) ;
        return result;
    }

    public String executeTableQueries(int tableIndex, String[] attributesList){
        String result = "";
        String query = "SELECT ";
        try{
            Statement statement = connection.createStatement();
            if (attributesList == null || attributesList[0].compareTo("*") == 0) {
                query += "*";
            }
            else {
                for (int index = 0; index < attributesList.length; index++) {
                    if (index == attributesList.length-1) {
                        query += attributesList[index];
                    } else {
                        query += attributesList[index] + ", ";
                    }
                    System.out.println(attributesList[index]);
                }
            }
            query += " FROM " + tableNames[tableIndex] + ";";
            System.out.println(query);
            ResultSet resultSet = statement.executeQuery(query);
            result = formatResultantSet(resultSet);
        } catch(SQLException e){
            e.printStackTrace();
        }
        //System.out.println(result);
        return result;
    }
    //endregion GUI called methods

    private String formatResultantSet(ResultSet resultSet){
        String result = "";
        try{
            ResultSetMetaData metaData = resultSet.getMetaData();
            for (int i = 0; i < metaData.getColumnCount(); i++) { //retrieves column labels
                if (i == metaData.getColumnCount()-1)
                    result += metaData.getColumnName(i+1);
                else
                    result += metaData.getColumnName(i+1) + ", ";
            }
            result += "\n";
            while(resultSet.next()){ //cycles through rows
                for (int i = 0; i < metaData.getColumnCount(); i++) { //cycles through columns
                    if (i > 0) {
                        result += ",  ";
                    }
                    result += resultSet.getString(i+1);
                    //System.out.println(resultSet.getString(i+1));
                }
                result += "\n";
            }
        } catch(SQLException e){
            e.printStackTrace();
        }
        return result;
    }


}
