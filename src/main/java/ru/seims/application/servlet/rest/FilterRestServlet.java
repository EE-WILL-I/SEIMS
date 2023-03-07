package ru.seims.application.servlet.rest;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;
import ru.seims.application.configuration.WebSecurityConfiguration;
import ru.seims.database.entitiy.DataTable;
import ru.seims.database.proccessing.SQLExecutor;
import ru.seims.utils.json.JSONBuilder;
import ru.seims.utils.logging.Logger;

import java.sql.ResultSet;

@RestController
public class FilterRestServlet {
    public static final String getTablesAPI = WebSecurityConfiguration.regEditorPattern+"api/filter/tables";
    public static final String getRowsAPI = WebSecurityConfiguration.regEditorPattern+"api/filter/rows";
    public static final String getColsAPI = WebSecurityConfiguration.regEditorPattern+"api/filter/cols";
    public static final String getRegionsAPI = WebSecurityConfiguration.regEditorPattern+"api/filter/regions";
    public static final String getOrgsAPI = WebSecurityConfiguration.regEditorPattern+"api/filter/orgs";
    public static final String getFilterAPI = WebSecurityConfiguration.regEditorPattern+"api/filter/filter";

    @GetMapping(getTablesAPI)
    public String getTables(@RequestParam(name = "doc", required = false) String docType,
                            @RequestParam(name = "filter", required = false) String filter) {
        if(docType == null || docType.isEmpty())
            docType = "2";
        if(filter == null || filter.isEmpty())
            filter = "";
        SQLExecutor executor = SQLExecutor.getInstance();
        JSONBuilder builder = new JSONBuilder();
        builder.openArray();
        Logger.log(this, "Fetching data for tables", 4);
        ResultSet resultSet = executor.executeSelect(executor.loadSqlResource("filter_scripts/get_tables.sql"), docType, filter);
        try {
            while (resultSet.next()) {
                String vrName = resultSet.getString("vr_name");
                int updateType = resultSet.getInt("update_type");
                String temp = resultSet.getString("r1_name");
                String r1 = temp != null ? temp : vrName.replace("_vrr", "_r");
                temp = resultSet.getString("r2_name");
                String r2 = temp != null ? temp : vrName.replace("_vrr", "_r");
                if(temp == null && updateType == 1) {
                    r1 += "_1";
                    r2 += "_2";
                }
                builder.addSubJSONElement(new JSONBuilder()
                        .addAVP("vr_name", vrName)
                        .addAVP("display_name", resultSet.getString("display_name")
                                .replaceAll("\"", "'")
                                .replaceAll("\n", " "))
                        .addAVP("r1_name", r1)
                        .addAVP("r2_name", r2)
                        .addAVP("update_type", updateType)
                        .getString());
            }
        } catch (Exception e) {
            Logger.log(this, e.getMessage(), 2);
            return new JSONBuilder().addAVP("error", e.getMessage()).getString();
        }
        builder.closeArray();
        //Thread.sleep(500);
        //Logger.log(this, builder.getRawString(), 4);
        return builder.getRawString();
    }

    @GetMapping(getRowsAPI)
    public String getRows(@RequestParam(name = "tab") String table,
                          @RequestParam(name = "filter", required = false) String filter) {
        return fetchDataForTable(table, filter, "filter_scripts/get_rows.sql");
    }

    @GetMapping(getColsAPI)
    public String getCols(@RequestParam(name = "tab") String table,
                          @RequestParam(name = "filter", required = false) String filter) {
        return fetchDataForTable(table, filter, "filter_scripts/get_cols.sql");
    }

    @GetMapping(getRegionsAPI)
    public String getRegions(@RequestParam(name = "filter", required = false) String filter) {
        return fetchDataForTable("", filter, "filter_scripts/get_regions.sql", false);
    }

    @GetMapping(getOrgsAPI)
    public String getOrgs(@RequestParam(name = "reg", required = false) String region,
                             @RequestParam(name = "filter", required = false) String filter) {
        if(region != null && !region.isEmpty())
            return fetchDataForTable(region, filter, "filter_scripts/get_orgs_of_region.sql");
        else
            return fetchDataForTable("", filter, "filter_scripts/get_orgs.sql", false);
    }

    @PostMapping(getFilterAPI)
    @ResponseBody
    public String doFilter(HttpEntity<String> httpEntity) {
        String body = httpEntity.getBody();
        if (body == null || body.isEmpty()) {
            Logger.log(this, "Null body", 2);
            return null;
        }
        try {
            //get filter body
            JSONObject filterBodyJSON = ((JSONObject) new JSONParser().parse(body));
            //table info
            JSONObject tableBody = (JSONObject) filterBodyJSON.get("tab");
            //@a0
            String vrName = tableBody.get("vr_name").toString();
            String displayName = tableBody.get("display_name").toString();
            String updateType = tableBody.get("update_type").toString();
            //@a1
            String r1 = tableBody.get("r1_name").toString();
            String r2 = tableBody.get("r2_name").toString();
            //filter rows
            JSONArray rowsJson = (JSONArray) filterBodyJSON.get("rows");
            String[] rows = new String[rowsJson.size()];
            for (int i = 0; i < rowsJson.size(); i++) {
                rows[i] = rowsJson.get(i).toString();
            }
            //filter columns
            JSONArray colsJson = (JSONArray) filterBodyJSON.get("cols");
            String[] cols = new String[colsJson.size()];
            String[] colsLabels = new String[colsJson.size()];
            for (int i = 0; i < colsJson.size(); i++) {
                JSONObject col = ((JSONObject) colsJson.get(i));
                cols[i] = col.get("id").toString();
                colsLabels[i] = col.get("text").toString();
            }
            //filter objects
            boolean filterByRegion = filterBodyJSON.get("obj").toString().equals("reg");
            String[] regs = null;
            String[] orgs = null;
            //regions to filter
            if (filterByRegion) {
                JSONArray regsJson = (JSONArray) filterBodyJSON.get("regs");
                if (regsJson.size() > 0) {
                    regs = new String[regsJson.size()];
                    for (int i = 0; i < regsJson.size(); i++) {
                        regs[i] = regsJson.get(i).toString();
                    }
                }
            } else {
                //organizations to filter
                JSONArray orgsJson = (JSONArray) filterBodyJSON.get("orgs");
                if(orgsJson.size() > 0) {
                    orgs = new String[orgsJson.size()];
                    for (int i = 0; i < orgsJson.size(); i++) {
                        orgs[i] = orgsJson.get(i).toString();
                    }
                }
            }
            //prepare query
            SQLExecutor executor = SQLExecutor.getInstance();
            String template;
            StringBuilder query = new StringBuilder();
            //@a2
            String rowsQuery = prepareRowsQuery(rows);
            //@a3
            String colsQuery = prepareColumnsQuery(cols, colsLabels);
            if (filterByRegion) {
                if (regs != null) {
                    template = executor.loadSqlResource("filter_scripts/get_result_for_reg.sql");
                    for (int i = 0; i < regs.length; i++) {
                        query.append(SQLExecutor.getInstance().insertArgs(template, vrName, r1, rowsQuery, colsQuery, regs[i]));
                        if (i < regs.length - 1) query.append("\nunion all\n");
                    }
                } else {
                    template = executor.loadSqlResource("filter_scripts/get_result_for_reg_all.sql");
                    query.append(executor.insertArgs(template, vrName, r1, rowsQuery, colsQuery));
                }
            } else {
                if (orgs != null) {
                    template = executor.loadSqlResource("filter_scripts/get_result_for_org.sql");
                    for (int i = 0; i < orgs.length; i++) {
                        query.append(SQLExecutor.getInstance().insertArgs(template, vrName, r1, rowsQuery, colsQuery, orgs[i]));
                        if (i < orgs.length - 1) query.append("\nunion all\n");
                    }
                } else {
                    template = executor.loadSqlResource("filter_scripts/get_result_for_org_all.sql");
                    query.append(executor.insertArgs(template, vrName, r1, rowsQuery, colsQuery));
                }
            }
            ResultSet resultSet = executor.executeSelect(executor.prepareStatement(query.toString()));
            DataTable table = new DataTable(displayName, vrName);
            table.populate(resultSet);
            return table.toJSON().toJSONString();
        } catch (Exception e) {
            Logger.log(this, "Cannot filter data: " + e.getMessage(), 2);
            e.printStackTrace();
            return null;
        }
    }

    private static String prepareColumnsQuery(String[] cols, String[] labels) {
        if(cols.length != labels.length)
            throw new IllegalArgumentException("Columns count don't match labels size");
        String colSrt = "sum(val_r2_@a0) as \"@a1\"";
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < cols.length; i++) {
            builder.append(colSrt.replace("@a0", cols[i]).replace("@a1", labels[i]));
            if(i < cols.length- 1) builder.append(",");
        }
        return builder.toString();
    }

    private static String prepareRowsQuery(String[] rows) {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < rows.length; i++) {
            builder.append(rows[i]);
            if(i < rows.length - 1) builder.append(",");
        }
        return builder.toString();
    }

    private static String fetchDataForTable(String entity, String filter, String resourceName) {
        return fetchDataForTable(entity, filter, resourceName, true);
    }
    private static String fetchDataForTable(String entity, String filter, String resourceName, boolean setEntity) {
        if(setEntity && (entity == null || entity.isEmpty()))
            return null;
        if(filter == null || filter.isEmpty())
            filter = "";
        SQLExecutor executor = SQLExecutor.getInstance();
        JSONBuilder builder = new JSONBuilder();
        builder.openArray();
        Logger.log(FilterRestServlet.class, "Fetching data for " + entity, 4);
        ResultSet resultSet;
        if(setEntity)
            resultSet = executor.executeSelect(executor.loadSqlResource(resourceName), entity, filter);
        else
            resultSet = executor.executeSelect(executor.loadSqlResource(resourceName), filter);
        try {
            while (resultSet.next()) {
                builder.addSubJSONElement(new JSONBuilder()
                        .addAVP("id", resultSet.getInt("id"))
                        .addAVP("name", resultSet.getString("name")
                                .replaceAll("\"", "'")
                                .replaceAll("\n", " ")
                                .replaceAll("\t", " "))
                        .getString());
            }
        } catch (Exception e) {
            Logger.log(FilterRestServlet.class, e.getMessage(), 2);
            return new JSONBuilder().addAVP("error", e.getMessage()).getString();
        }
        builder.closeArray();
        //Thread.sleep(500);
        //Logger.log(FilterRestServlet.class, builder.getRawString(), 4);
        return builder.getRawString();
    }
}
