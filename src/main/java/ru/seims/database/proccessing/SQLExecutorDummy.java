package ru.seims.database.proccessing;

import  ru.seims.database.connection.DatabaseConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLExecutorDummy extends SQLExecutor {
    private final static SQLExecutorDummy instance = new SQLExecutorDummy(DatabaseConnector.connection, "");
    protected SQLExecutorDummy(Connection connection, String sqlPath) {
        super(connection, sqlPath);
    }

    public static SQLExecutor getInstance() {
        return instance;
    }

    @Override
    public ResultSet executeSelect(String query, String... args) {
        return null;
    }

    @Override
    public ResultSet executeSelect(PreparedStatement statement, String... args) {
        return null;
    }

    @Override
    public boolean executeUpdate(String query, String... args) throws SQLException {
        return true;
    }

    @Override
    public boolean executeUpdate(PreparedStatement statement, String... args) throws SQLException {
        return true;
    }

    @Override
    public boolean executeInsert(String query, String table, String... args) throws SQLException {
        return true;
    }

    @Override
    public boolean executeCall(String query, String... args) {
        return true;
    }

    @Override
    public String loadSQLResource(String resourceName) {
        return "";
    }
}