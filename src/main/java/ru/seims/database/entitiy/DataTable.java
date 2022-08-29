package ru.seims.database.entitiy;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import ru.seims.database.proccessing.SQLExecutor;
import ru.seims.utils.json.JSONBuilder;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataTable {
    public int columnCount = 0, rowCount = 0;
    private String name;
    private String sysName;
    private byte updateType = 1;
    private final ArrayList<String> columnLabels = new ArrayList<>();
    private final ArrayList<String> columnNames = new ArrayList<>();
    private final ArrayList<Map<String, String>> dataRows = new ArrayList<>();

    public DataTable(String tableName, String tableSysName) {
        name = tableName; sysName = tableSysName;
    }

    public DataTable(String tableName) {
        name = tableName; sysName = tableName;
    }

    public DataTable() {
        this("Undefined");
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getSysName() {
        return sysName;
    }

    public void setSysName(String name) {
        this.sysName = name;
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

    public JSONObject toJSON() {
        JSONObject tableObject = new JSONObject();
        JSONArray dataArray = new JSONArray();
        JSONArray labels = new JSONArray();
        for(String column : columnLabels) {
            labels.add(column);
        }
        for(Map<String, String> row : dataRows) {
            JSONObject rowData = new JSONObject();
            int i = 0;
            for(String column : columnLabels) {
                rowData.put(i++, row.get(column));
            }
            dataArray.add(rowData);
        }
        tableObject.put("name", name);
        tableObject.put("sysName", sysName);
        tableObject.put("data", dataArray);
        tableObject.put("labels", labels);
        return tableObject;
    }

    public ArrayList<String> generateLabelForVR(String vr,String r2) throws SQLException {
        if(r2 == null) r2 = vr.replace("_vrr", "_r") + "_2";
        SQLExecutor executor = SQLExecutor.getInstance();
        ResultSet resultSet = executor.executeSelectSimple(r2,"name", "");
        ArrayList<String> columns = new ArrayList<>();
        int ind = 1;
        while(resultSet.next()) {
            String label = resultSet.getString(1);
            String join = executor.insertArgs(executor.loadSQLResource("vr_general_label.sql"),
                    new String[]{String.valueOf(ind), label});
            columns.add(join);
            ind++;
        }
        return columns;
    }

    public String generateQueryForVR(String orgId, ArrayList<String> labelArr, String vr, String r1) {
        SQLExecutor executor = SQLExecutor.getInstance();
        if (labelArr != null) {
            if (r1 == null) r1 = vr.replace("_vrr", "_r") + "_1";
            StringBuilder builder = new StringBuilder();
            for (String join : labelArr) builder.append(join);
            String labels = builder.substring(0, builder.toString().length() - 1);
            return executor.insertArgs(executor.loadSQLResource("vr_general_full_ut1.sql"),
                    new String[]{orgId, labels, vr, r1});
        } else {
            if (r1 == null || r1.isEmpty()) r1 = vr.replace("_vrr", "_r");
            return executor.insertArgs(executor.loadSQLResource("vr_general_full_ut2.sql"),
                    new String[]{orgId, vr, r1});
        }
    }

    public DataTable populate(JSONObject jsonData) {
        sysName = (String) jsonData.get("sysName");
        name = (String) jsonData.get("name");
        JSONArray labels = (JSONArray) jsonData.get("labels");
        JSONArray dataArray = (JSONArray) jsonData.get("data");
        for(Object column : labels) {
            columnLabels.add((String) column);
        }
        for(Object row : dataArray) {
            int i = 0;
            Map<String, String> map = new HashMap<>();
            for(String column : columnLabels) {
                String val = ((JSONObject)row).get(String.valueOf(i++)).toString();
                map.put(column, val);
            }
            dataRows.add(map);
        }
        return  this;
    }

    public DataTable populate(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        for (int column = 1; column <= metaData.getColumnCount(); column++) {
            columnLabels.add(metaData.getColumnLabel(column));
            columnNames.add(metaData.getColumnName(column));
        }
        while (resultSet.next()) {
            Map<String, String> map = new HashMap<String, String>();
            for (int column = 1; column <= metaData.getColumnCount(); column++) {
                map.put(metaData.getColumnLabel(column), resultSet.getString(column));
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

    public String getColumnName(int ind) {
        return columnNames.get(ind);
    }

    public void setUpdateType(byte type) {
        updateType = type;
    }

    public byte getUpdateType() { return updateType; }
}
