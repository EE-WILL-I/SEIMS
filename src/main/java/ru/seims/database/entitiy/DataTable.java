package ru.seims.database.entitiy;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataTable {
    public int columnCount = 0, rowCount = 0;
    private final String name;
    private final String sysName;
    private final ArrayList<String> columnLabels = new ArrayList<>();
    private final ArrayList<Map<String, String>> dataRows = new ArrayList<>();

    public DataTable(String tableName, String tableSysName) {
        name = tableName; sysName = tableSysName;
    }

    public DataTable(String tableName) {
        name = tableName; sysName = tableName;
    }

    public String getName() {
        return name;
    }

    public String getSysName() {
        return sysName;
    }

    public void populateColumns(List<String> columns) {
        columnLabels.addAll(columns);
        columnCount += columns.size();
    }

    public void populateRows(List<List<String>> data) {
        if(data.get(0).size() != columnCount)
            throw new IllegalArgumentException("Column count does not match");
        for(List<String> row : data) {
            Map<String, String> map = new HashMap<>();
            for (int i = 0; i < columnCount; i++) {
                map.put(columnLabels.get(i), row.get(i));
            }
            dataRows.add(map);
            rowCount++;
        }
    }

    public DataTable populateTable(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        for (int column = 1; column <= metaData.getColumnCount(); column++) {
            columnLabels.add(metaData.getColumnName(column));
        }
        while (resultSet.next()) {
            Map<String, String> map = new HashMap<String, String>();
            for (int column = 1; column <= metaData.getColumnCount(); column++) {
                map.put(metaData.getColumnName(column), resultSet.getString(column));
            }
            dataRows.add(map);
        }
        return this;
    }

    public Map<String, String> getRow(int rowNumber) {
        return dataRows.get(rowNumber);
    }

    public ArrayList<Map<String, String>> getDataRows() {
        return dataRows;
    }

    public String getColumn(int colNumber) {
        return columnLabels.get(colNumber);
    }

    public ArrayList<String> getColumnLabels() {
        return columnLabels;
    }
}
