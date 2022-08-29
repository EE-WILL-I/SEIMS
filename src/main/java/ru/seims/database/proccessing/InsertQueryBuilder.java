package ru.seims.database.proccessing;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class InsertQueryBuilder {
    public int startDataColumnInd = 2;
    private final StringBuilder query = new StringBuilder();
    //private final String insertSample;
    private final String tableName;
    private final SQLExecutor sqlExecutor;
    private String  orgId;
    private int rowCount = 0, argsBias = 0;
    private int columnCount = 0;
    private StringBuilder row;

    public InsertQueryBuilder(String tableName, String orgId) {
        this.tableName = tableName;
        this.orgId = orgId;
        //this.insertSample = insertSample;
        sqlExecutor = SQLExecutor.getInstance();
        row = new StringBuilder("(").append(orgId).append(",1");
        query.append("INSERT INTO ").append(this.tableName).append(" VALUES ");
    }

    public InsertQueryBuilder addColumnOld(String value) {
        if(columnCount >= startDataColumnInd) {
            try {
                //return addColumn(Integer.parseInt(value));
            } catch (NumberFormatException ignored) {}
        }
        row.append(",");
        row.append("\"").append(value).append("\"");
        columnCount++;
        return this;
    }

    public InsertQueryBuilder addColumn(String  value) {
        row.append(",");
        row.append(value);
        columnCount++;
        return this;
    }

    public InsertQueryBuilder addRows(List<String[]> data) {
        //for (String[] row : data)
           // addRow(row);
        return this;
    }

    public InsertQueryBuilder addRow() {
        if(rowCount > 0)
            query.append("),");
        //query.append(sqlExecutor.insertArgs(insertSample, rowData, argsBias));
        query.append(row);
        rowCount++;
        row = new StringBuilder("(").append(orgId).append(",").append(rowCount + 1);
        columnCount = 0;
        return this;
    }

    public InsertQueryBuilder closeRow() {
        row.append(");");
        query.append("),");
        query.append(row);
        columnCount = 0;
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

    public String getTableName() { return tableName; }

    public void setArgsBias(int bias) {
        argsBias = bias;
    }
}
