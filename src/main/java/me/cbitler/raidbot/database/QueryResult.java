package me.cbitler.raidbot.database;

import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Represents the result of a query
 * It also returns the statement so that it can be cleanly closed
 * @author Christopher Bitler
 */
public class QueryResult {
    Statement stmt;
    ResultSet results;

    /**
     * Create a new QueryResult with the specified values
     * @param stmt The statement used to get the ResultSet
     * @param resultSet The ResultSet
     */
    public QueryResult(Statement stmt, ResultSet resultSet) {
        this.stmt = stmt;
        this.results = resultSet;
    }

    /**
     * Get the statement used to get the ResultSet
     * @return The statement
     */
    public Statement getStmt() {
        return stmt;
    }

    /**
     * Get the ResultSet that came as a result of this query
     * @return The ResultSet
     */
    public ResultSet getResults() {
        return results;
    }
}
