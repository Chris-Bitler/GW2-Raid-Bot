package me.cbitler.raidbot.database;

import java.sql.*;
import java.util.List;

public class Database {
    String databaseName;
    Connection connection;

    String raidTableInit = "CREATE TABLE IF NOT EXISTS raids (\n"
            + " raidId text PRIMARY KEY, \n"
            + " serverId text NOT NULL, \n"
            + " channelId text NOT NULL, \n"
            + " `name` text NOT NULL, \n"
            + " `date` text NOT NULL, \n"
            + " `time` text NOT NULL, \n"
            + " roles text NOT NULL);";

    String raidUsersTableInit = "CREATE TABLE IF NOT EXISTS raidUsers (\n"
            + " userId text, \n"
            + " username text, \n"
            + " spec text, \n"
            + " role text, \n"
            + " raidId text)";


    public Database(String databaseName) {
        this.databaseName = databaseName;
    }

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
            System.exit(1);
        }
    }

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

    public void tableInits() throws SQLException {
        connection.createStatement().execute(raidTableInit);
        connection.createStatement().execute(raidUsersTableInit);
    }
}
