import java.sql.*;
import java.util.ArrayList;

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

        sqlQueries = new String[] {"\n" +
                "\n" +
                "SELECT gameName, DDOff.devName, LOC.country\n" +
                "FROM (Developed D LEFT JOIN DeveloperOffices DOff ON D.devName=DOff.devName) DDOff\n" +
                "LEFT JOIN Locations Loc ON DDOff.locID=Loc.locID\n" +
                "WHERE Loc.country='Canada' AND gameName IN\n" +
                "(SELECT gameName FROM Ratings\n" +
                "WHERE rating='E'\n" +
                "UNION\n" +
                "SELECT gameName FROM PublishedVideoGames\n" +
                "WHERE genre='strategy'\n" +
                "UNION\n" +
                "SELECT gameName FROM Scores\n" +
                "WHERE scoreType='Critic' AND scoreValue<=60);\n"," select gameName,platform, pubName, naSales from PublishedVideoGames natural join publishers natural join sales where gamesPublished > 10 AND gameName in( select gameName from PublishedVideoGames natural join sales natural join scores where (scoreValue/outOf > 0.5)) order by naSales Desc ;",
        "SELECT gameName, platform, pubName, devName, (globalCount + naCount + euCount + jpCount + otherCount)/5 AS avgPlayerCount\n" +
                "FROM\n" +
                "PublishedVideoGames NATURAL JOIN PlayedBy NATURAL JOIN Players NATURAL JOIN Developed\n" +
                "WHERE avgPlayerCount in\n" +
                "(SELECT MIN((globalCount + naCount + euCount + jpCount + otherCount)/5) AS minPlayerCount\n" +
                "FROM\n" +
                "PublishedVideoGames NATURAL JOIN PlayedBy NATURAL JOIN Players NATURAL JOIN Developed);\n","select country,gameName,platform from Locations natural join DeveloperOffices \n" +
                "natural join Developed where country='Japan' and gameName in(\n" +
                "select gameName from sales where jpSales> naSales and jpSales>euSales and jpSales>otherSales ) \n","SELECT gameName, platform, euCount AS num_Of_EU_Players\n" +
                "FROM PlayedBy PB LEFT JOIN Players P ON PB.playerGroupID=P.playerGroupID\n" +
                "WHERE gameName IN\n" +
                "(SELECT PVG.gameName\n" +
                "FROM PublishedVideoGames PVG LEFT JOIN Scores S ON PVG.gameName=S.gameName\n" +
                "WHERE S.scoreType='User' AND S.scoreValue>=8\n" +
                "GROUP BY PVG.gameName, PVG.platform)\n" +
                "ORDER BY euCount DESC;\n","  SELECT distinct country FROM Locations WHERE country in ( SELECT country FROM PublishedVideoGames NATURAL JOIN Developed NATURAL JOIN DeveloperOffices NATURAL JOIN Locations NATURAL JOIN Scores GROUP BY gameName, platform HAVING AVG(scoreValue/outof) > 0.8);\n","" +
                "SELECT pubName, gameName, platform, scoreValue\n" +
                "FROM (SELECT  * FROM PublishedVideoGames PVG LEFT JOIN Scores S ON PVG.gameName=S.gameName AND PVG.platform=S.platform\n" +
                "WHERE scoreType='User'\n" +
                "ORDER BY scoreValue DESC\n" +
                "LIMIT 50)\n" +
                "WHERE genre='Shooter';\n","select gameName,platform,naCount from PublishedVideoGames natural join playedBy natural join players where naCount > 1 And gameName like 'SoulCalibur%' ;\n",
        "select Developed.devName, PublishedVideoGames.releaseYear, sales.globalSales,\n" +
                "players.globalCount as numOfPlayers, Scores.scoreValue/Scores.outOf as score\n" +
                "from Developed natural join PublishedVideoGames\n" +
                "natural join sales natural join scores natural join PlayedBy \n" +
                "natural join Players\n" +
                "where releaseYear < 2000\n" +
                "order by globalSales DESC, numOfPlayers DESC, score DESC \n" +
                "Limit 50\n","select gameName,platform,globalCount from PublishedVideoGames natural join players natural join\n" +
                "     PlayedBy where globalCount > 1 GROUP BY gameName HAVING count(platform)=1 ;\n" +
                "\n","SELECT rating, MAX(drugRef) AS drugRef, MAX(blood) As blood, MAX(bloodAndGore) AS bloodAndGore, MAX(cartoonViolence) AS cartoonViolence, MAX(fantasyViolence) AS fantasyViolence, MAX(animatedBlood) AS animatedBlood, MAX(alcoholRef) AS alcoholRef, MAX(crudeHumor) AS crudeHumor\n" +
                "FROM Ratings NATURAL JOIN GameRatings\n" +
                "GROUP BY rating;\n","SELECT gameName, platform, AVG(scoreValue/outOf) as avg_percent\n" +
                "FROM Scores\n" +
                "WHERE gameName LIKE '%Pokemon%'\n" +
                "GROUP BY gameName, platform\n" +
                "ORDER BY avg_percent ASC\n" +
                "LIMIT 5;\n" +
                "\n"};
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

    public String[] getSqlQueries() {
        return sqlQueries;
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
}
