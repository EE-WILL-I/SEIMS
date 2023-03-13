package ru.seims.database.entitiy;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import ru.seims.application.servlet.jsp.OrganizationServlet;
import ru.seims.database.proccessing.SQLExecutor;

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
    private String r1Name, r2Name;
    private byte updateType = 1;
    private boolean isChild = false;
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

    public String getR1Name() {
        if(r1Name == null)
            return "";
        return r1Name;
    }

    public String getR2Name() {
        if(r2Name == null)
            return "";
        return r2Name;
    }

    public void setR1Name(String r1Name) {
        this.r1Name = r1Name;
    }

    public void setR2Name(String r2Name) {
        this.r2Name = r2Name;
    }

    public String getSysName() {
        return sysName;
    }

    public void setSysName(String name) {
        this.sysName = name;
    }

    public boolean isChild() {
        return isChild;
    }

    public void setIsChild(boolean isChild) {
        this.isChild = isChild;
    }

    public void populateColumns(List<String> columns) {
        for(int i = 0; i < columns.size(); ++i) {
            if(columns.get(i).isEmpty())
                columns.set(i, "ND_"+i);
        }
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

    public ArrayList<String> generateLabelForVR(String vr, String r2, SQLExecutor executor) throws SQLException {
        if(r2 == null) r2 = vr.replace("_vrr", "_r") + "_2";
        ResultSet resultSet = executor.executeSelectSimple(r2,"name", "");
        ArrayList<String> columns = new ArrayList<>();
        int ind = 1;
        String labelSampleResource = "vr_general_label.sql";
        while(resultSet.next()) {
            String label = resultSet.getString(1);
            String join = executor.insertArgs(
                    executor.loadSqlResource(labelSampleResource), String.valueOf(ind), label
            );
            columns.add(join);
            ind++;
        }
        return columns;
    }

    public String generateQueryForVR(SQLExecutor executor, String id, ArrayList<String> labelArr, String vr, String r1) {
        if (labelArr != null) {
            if (r1 == null) r1 = vr.replace("_vrr", "_r") + "_1";
            StringBuilder builder = new StringBuilder();
            for (String join : labelArr) builder.append(join);
            String labels = builder.substring(0, builder.toString().length() - 1);
            String resource;
                resource = "vr_general_full_ut1.sql";
            return executor.insertArgs(
                    executor.loadSqlResource(resource), id, labels, vr, r1
            );
        } else {
            if (r1 == null || r1.isEmpty())
                r1 = vr.replace("_vrr", "_r");
            String resource = "vr_general_full_ut2.sql";
            return executor.insertArgs(executor.loadSqlResource(resource), id, vr, r1);
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

    public void populate(ResultSet resultSet) throws SQLException {
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
