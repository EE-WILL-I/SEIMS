package ru.seims.database.proccessing;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class InsertQueryBuilder {
    private final StringBuilder query = new StringBuilder();
    private final String insertSample, tableName;
    private final SQLExecutor sqlExecutor;
    private int rowCount = 0, argsBias = 0;

    public InsertQueryBuilder(String tableName, String insertSample) {
        this.tableName = tableName;
        this.insertSample = insertSample;
        sqlExecutor = SQLExecutor.getInstance();

        query.append("INSERT INTO ").append(this.tableName).append(" VALUES ");
    }

    public InsertQueryBuilder addRows(List<String[]> data) {
        for (String[] row : data)
            addRow(row);
        return this;
    }

    public InsertQueryBuilder addRow(String[] rowData) {
        if (rowCount > 0)
            query.append(",");
        query.append(sqlExecutor.insertArgs(insertSample, rowData, argsBias));
        rowCount++;
        return this;
    }

    public PreparedStatement getStatement() {
        try {
            return sqlExecutor.prepareStatement(query.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setArgsBias(int bias) {
        argsBias = bias;
    }
}
