package ru.seims.application.servlet.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.seims.application.configuration.WebSecurityConfiguration;
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
        ResultSet resultSet = executor.executeSelect(executor.loadSQLResource("filter_scripts/get_tables.sql"), docType, filter);
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
            resultSet = executor.executeSelect(executor.loadSQLResource(resourceName), entity, filter);
        else
            resultSet = executor.executeSelect(executor.loadSQLResource(resourceName), filter);
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
