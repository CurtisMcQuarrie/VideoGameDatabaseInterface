import java.io.FileWriter;
import java.sql.*;
import java.util.ArrayList;

public class SQL {

    //region fields
    private final int MAX_ROW_COUNT = 2500;
    private final String DB_FILE_NAME = "jdbc:sqlite:" + System.getProperty("user.dir") + "\\Group73_VideoGame_DB.db";
    private String csvText;
    private String[] queries; //sql code
    private String[] sqlQueries;
    private String[] tableNames;
    private String[] currAttributeNames;
    private String[] currTableColumnNames;
    private Object[][] currTableData;
    private Connection connection;
    private DatabaseMetaData metaData;
    //endregion fields

    //region constructors
    public SQL() {
        //System.out.println(DB_FILE_NAME);
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

        sqlQueries = new String[] {
                "SELECT gameName, platform, DDOff.devName, LOC.country FROM (Developed D LEFT JOIN DeveloperOffices DOff ON D.devName=DOff.devName) DDOff\n" +
                    "LEFT JOIN Locations Loc ON DDOff.locID=Loc.locID WHERE Loc.country='Canada' AND gameName IN\n" +
                    "(SELECT gameName FROM Ratings WHERE rating='E' UNION SELECT gameName FROM PublishedVideoGames\n" +
                    "WHERE genre='strategy' UNION SELECT gameName FROM Scores WHERE scoreType='Critic' AND scoreValue<=60) LIMIT " + MAX_ROW_COUNT + ";",
                "select gameName,platform, pubName, naSales , gamesPublished from PublishedVideoGames natural join publishers\n" +
                        "    natural join sales where gamesPublished > 10 AND gameName in( select gameName from PublishedVideoGames natural join sales\n" +
                        "    natural join scores where (scoreValue/outOf > 0.5)) order by naSales Desc ;",
                "SELECT gameName, platform, pubName, devName, (globalCount + naCount + euCount + jpCount + otherCount)/5 AS avgPlayerCount\n" +
                    "FROM PublishedVideoGames NATURAL JOIN PlayedBy NATURAL JOIN Players NATURAL JOIN Developed\n" +
                    "WHERE avgPlayerCount in (SELECT MIN((globalCount + naCount + euCount + jpCount + otherCount)/5) AS minPlayerCount\n" +
                    "FROM PublishedVideoGames NATURAL JOIN PlayedBy NATURAL JOIN Players NATURAL JOIN Developed) LIMIT " + MAX_ROW_COUNT + ";",
                "select gameName, platform, devName, country from Locations natural join DeveloperOffices \n" +
                    "natural join Developed where country='Japan' and gameName in(\n" +
                "select gameName from sales where jpSales> naSales and jpSales>euSales and jpSales>otherSales order by jpSales desc) LIMIT " + MAX_ROW_COUNT + ";",
                "SELECT gameName, platform, euCount AS num_Of_EU_Players\n" +
                    "FROM PlayedBy PB LEFT JOIN Players P ON PB.playerGroupID=P.playerGroupID WHERE gameName IN (SELECT PVG.gameName\n" +
                    "FROM PublishedVideoGames PVG LEFT JOIN Scores S ON PVG.gameName=S.gameName\n" +
                    "WHERE S.scoreType='User' AND S.scoreValue>=8 GROUP BY PVG.gameName, PVG.platform) ORDER BY euCount DESC LIMIT " + MAX_ROW_COUNT + ";",
                "SELECT country, COUNT(gameName) as numOfGames, AVG(scoreValue/outOf)*100 as averageGameScores\n" +
                        "FROM Locations NATURAL JOIN DeveloperOffices NATURAL JOIN Developed NATURAL JOIN PublishedVideoGames NATURAL JOIN Scores\n" +
                        "GROUP BY country HAVING averageGameScores > 80 ORDER BY numOfGames DESC LIMIT " + MAX_ROW_COUNT + ";",
                "SELECT pubName, gameName, platform, scoreValue\n" +
                    "FROM (SELECT  * FROM PublishedVideoGames PVG LEFT JOIN Scores S ON PVG.gameName=S.gameName AND PVG.platform=S.platform\n" +
                    "WHERE scoreType='User' ORDER BY scoreValue DESC LIMIT 50) WHERE genre='Shooter';",
                "select gameName,platform,naCount from PublishedVideoGames natural join playedBy natural join players where naCount > 1 And gameName like 'SoulCalibur%' LIMIT " + MAX_ROW_COUNT + ";",
                "select Developed.devName, PublishedVideoGames.releaseYear, sales.globalSales,\n" +
                    "players.globalCount as numOfPlayers, Scores.scoreValue/Scores.outOf as score\n" +
                    "from Developed natural join PublishedVideoGames\n" +
                    "natural join sales natural join scores natural join PlayedBy \n" +
                    "natural join Players where releaseYear < 2000\n" +
                    "order by globalSales DESC, numOfPlayers DESC, score DESC Limit 50;",
                "select gameName,platform,globalCount from PublishedVideoGames natural join players natural join\n" +
                    "PlayedBy where globalCount > 1 GROUP BY gameName HAVING count(platform)=1 LIMIT " + MAX_ROW_COUNT + ";",
                "SELECT rating, MAX(drugRef) AS drugRef, MAX(blood) As blood, MAX(bloodAndGore) AS bloodAndGore, MAX(cartoonViolence) AS cartoonViolence, MAX(fantasyViolence) AS fantasyViolence, MAX(animatedBlood) AS animatedBlood, MAX(alcoholRef) AS alcoholRef, MAX(crudeHumor) AS crudeHumor\n" +
                    "FROM Ratings NATURAL JOIN GameRatings\n" +
                    "GROUP BY rating LIMIT " + MAX_ROW_COUNT + ";",
                "SELECT gameName, platform, AVG(scoreValue/outOf) as avg_percent FROM Scores WHERE gameName LIKE '%Pokemon%'\n" +
                    "GROUP BY gameName, platform ORDER BY avg_percent ASC LIMIT 5;"};
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

    public String[] getTableAttributes(){
        return currAttributeNames;
    }

    public String[] getCurrTableColumnNames() {
        return currTableColumnNames;
    }

    public Object[][] getCurrTableData() {
        return currTableData;
    }

    public String getCSVText() {
        return csvText;
    }
    //endregion getters and setters

    //region database connection methods
    private void connect(){
        connection = null;
        try {
            connection = DriverManager.getConnection(DB_FILE_NAME);
        } catch(SQLException e){
            e.printStackTrace();
        }
    }
    //endregion database connection methods

    //region database commands
    private String formatResultantSet(ResultSet resultSet){
        StringBuilder stringBuilder = new StringBuilder();
        String result = "";
        try{
            ResultSetMetaData metaData = resultSet.getMetaData();
            int colCount = metaData.getColumnCount();
            currTableColumnNames = new String[colCount];
            for (int i = 0; i < colCount; i++) { //retrieves column labels
                //result += metaData.getColumnName(i+1);
                stringBuilder.append(metaData.getColumnName(i+1));
                if (i != colCount-1){
                    //result += ", ";
                    stringBuilder.append(", ");
                }
                /*else{
                    result += metaData.getColumnName(i+1) + ", ";
                }*/
                currTableColumnNames[i] = metaData.getColumnName(i+1);
            }
            //result += "\n";
            stringBuilder.append("\n");
            currTableData = new Object[MAX_ROW_COUNT][colCount];
            int rowCount = 0;
            while(resultSet.next() && rowCount < MAX_ROW_COUNT){ //cycles through rows
                for (int i = 0; i < colCount; i++) { //cycles through columns
                    if (i > 0) {
                        stringBuilder.append(", ");
                        //result += ",  ";
                    }
                    stringBuilder.append(removeCommas(resultSet.getString(i+1)));
                    //result += removeCommas(resultSet.getString(i+1));
                    //result += resultSet.getString(i+1);
                    currTableData[rowCount][i] = resultSet.getString(i+1);
                }
                stringBuilder.append("\n");
                //result += "\n";
                rowCount++;
            }
            Object[][] tempTableData = new Object[rowCount][colCount];
            for (int col = 0; col < colCount; col++) {
                for (int row = 0; row < rowCount; row++) {
                    tempTableData[row][col] = currTableData[row][col];
                }
            }
            currTableData = tempTableData;
            result = stringBuilder.toString();
        } catch(SQLException e) {
            e.printStackTrace();
        }
        csvText = result;
        return result;
    }

    private void retrieveTableNames(){
       try{
           metaData = connection.getMetaData();
           ResultSet tables = metaData.getTables(null, null,"%" ,null);
           ArrayList<String> namesList = new ArrayList<>();
           while(tables.next()){
                namesList.add(tables.getString("TABLE_NAME"));
           }
           //System.out.println("Number of Table Names : " + namesList.size());
           tableNames = namesList.toArray(new String[namesList.size()]);
       } catch (SQLException e){
           e.printStackTrace();
       }
    }
    //endregion database commands

    //region GUI called methods
    public String executeMadeQueries(int index){
        String result = "";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQueries[index]);
            result = formatResultantSet(resultSet);
        } catch (SQLException e){
            e.printStackTrace();
        }
        return result;
    }

    public String executeTableQueries(int tableIndex, String[] attributesList){
        String result = "";
        //String query = "SELECT ";
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT ");
        try{
            Statement statement = connection.createStatement();
            if (attributesList == null || attributesList[0].compareTo("*") == 0) {
                queryBuilder.append("*");
                //query += "*";
            }
            else {
                for (int index = 0; index < attributesList.length; index++) {
                    if (index == attributesList.length-1) {
                        queryBuilder.append(attributesList[index]);
                        //query += attributesList[index];
                    } else {
                        queryBuilder.append(attributesList[index]);
                        queryBuilder.append(", ");
                        //query += attributesList[index] + ", ";
                    }
                }
            }
            queryBuilder.append(" FROM ");
            queryBuilder.append(tableNames[tableIndex]);
            queryBuilder.append(" LIMIT ");
            queryBuilder.append(MAX_ROW_COUNT);
            queryBuilder.append(";");
            //query += " FROM " + tableNames[tableIndex] + " LIMIT " + MAX_ROW_COUNT + ";";
            //ResultSet resultSet = statement.executeQuery(query);
            //System.out.println(queryBuilder.toString());
            ResultSet resultSet = statement.executeQuery(queryBuilder.toString());
            result = formatResultantSet(resultSet);
        } catch(SQLException e){
            e.printStackTrace();
        }
        return result;
    }
    int csvCounter =0;
    public void exportToCsv(String result) {
            try {
                FileWriter csvFile = new FileWriter("csvOutput"+csvCounter+".CSV");
                csvCounter++;
                if (result != null)
                    csvFile.write(result);
                else
                    csvFile.write("");
                csvFile.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        //}
    }

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
    //endregion GUI called methods

    //region helper methods
    private String removeCommas(String message){
        String result = "";
        if (message != null && message.contains(",")){
            result = message.replace(",", " ");
        }
        else {
            result = message;
        }
        return result;
    }
    //endregion helper methods
}
