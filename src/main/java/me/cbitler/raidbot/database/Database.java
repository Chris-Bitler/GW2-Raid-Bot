package me.cbitler.raidbot.database;

import java.sql.*;
import java.util.List;

/**
 * Class for managing the SQLite database for this bot
 * @author Christopher Bitler
 */
public class Database {
    String databaseName;
    Connection connection;

    //Thee are the queries for creating the tables

    String raidTableInit = "CREATE TABLE IF NOT EXISTS raids (\n"
            + " raidId text PRIMARY KEY, \n"
            + " serverId text NOT NULL, \n"
            + " channelId text NOT NULL, \n"
            + " leader text NOT NULL, \n"
            + " `name` text NOT NULL, \n"
            + " `description` text, \n"
            + " `date` text NOT NULL, \n"
            + " `time` text NOT NULL, \n"
            + " roles text NOT NULL);";

    String raidUsersTableInit = "CREATE TABLE IF NOT EXISTS raidUsers (\n"
            + " userId text, \n"
            + " username text, \n"
            + " spec text, \n"
            + " role text, \n"
            + " raidId text)";

    String raidUsersFlexRolesTableInit = "CREATE TABLE IF NOT EXISTS raidUsersFlexRoles (\n"
            + " userId text, \n"
            + " username text, \n"
            + " spec text, \n"
            + " role text, \n"
            + " raidId text)";

    String botServerSettingsInit = "CREATE TABLE IF NOT EXISTS serverSettings (\n"
            + " serverId text PRIMARY KEY, \n"
            + " raid_leader_role text)";

    /**
     * Create a new database with the specific filename
     * @param databaseName The filename/location of the SQLite database
     */
    public Database(String databaseName) {
        this.databaseName = databaseName;
    }

    /**
     * Connect to the SQLite database and create the tables if they don't exist
     */
    public void connect() {
        String url = "jdbc:sqlite:" + databaseName;
        try {
            connection = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println("Database connection error");
            System.exit(1);
        }

        try {
            tableInits();
        } catch (SQLException e) {
            System.out.println("Couldn't create tables");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Run a query and return the results using the specified query and parameters
     * @param query The query with ?s where the parameters need to be placed
     * @param data The parameters to put in the query
     * @return QueryResult representing the statement used and the ResultSet
     * @throws SQLException
     */
    public QueryResult query(String query, String[] data) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(query);
        int i = 1;
        for(String input : data) {
            stmt.setObject(i, input);
            i++;
        }

        ResultSet rs = stmt.executeQuery();

        return new QueryResult(stmt, rs);
    }

    /**
     * Run an update query with the specified parameters
     * @param query The query with ?s where the parameters need to be placed
     * @param data The parameters to put in the query
     * @throws SQLException
     */
    public void update(String query, String[] data) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(query);
        int i = 1;
        for(String input : data) {
            stmt.setObject(i, input);
            i++;
        }

        stmt.execute();
        stmt.close();
    }

    /**
     * Create the database tables. Also alters the raid table to add the leader column if it doesn't exist.
     * @throws SQLException
     */
    public void tableInits() throws SQLException {
        connection.createStatement().execute(raidTableInit);
        connection.createStatement().execute(raidUsersTableInit);
        connection.createStatement().execute(raidUsersFlexRolesTableInit);
        connection.createStatement().execute(botServerSettingsInit);

        try {
            connection.createStatement().execute("ALTER TABLE raids ADD COLUMN leader text");
            connection.createStatement().execute("ALTER TABLE raids ADD COLUMN `description` text");
        } catch (Exception e) { }
    }
}
