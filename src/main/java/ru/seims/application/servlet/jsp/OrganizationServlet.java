package ru.seims.application.servlet.jsp;

import jdk.nashorn.internal.scripts.JO;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.seims.application.configuration.WebSecurityConfiguration;
import ru.seims.application.context.GlobalApplicationContext;
import ru.seims.application.servlet.ServletContext;
import ru.seims.database.proccessing.SQLExecutor;
import ru.seims.utils.logging.Logger;
import ru.seims.utils.properties.PropertyReader;
import ru.seims.utils.properties.PropertyType;

import javax.servlet.http.HttpServletRequest;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@Controller()
public class OrganizationServlet {
    public enum SelectScope {
        Org,
        Reg
    }
    public static final String org = WebSecurityConfiguration.viewerPattern+"org";
    public static final String getOrg = WebSecurityConfiguration.viewerPattern+"org/{id}";
    public static final String getRegionTotal = WebSecurityConfiguration.viewerPattern+"region/{id}";
    public static final String editOrg = WebSecurityConfiguration.orgEditorPattern+"org/{id}";
    public static final String resetOrg = WebSecurityConfiguration.orgEditorPattern+"org/{id}/reset";
    public static final String randomizeOrg = WebSecurityConfiguration.orgEditorPattern+"org/{id}/randomize";
    public static final String apps = WebSecurityConfiguration.orgEditorPattern+"org/{id}/apps";
    public static final String updateOrg = WebSecurityConfiguration.orgEditorPattern+"org/{id}/update/";
    public static final String updateOrgInfo = WebSecurityConfiguration.orgEditorPattern+"org/{id}/updateInfo";
    private static SQLExecutor sqlExecutor() {
        return SQLExecutor.getInstance();
    }
    private int tablesOnPage = 0;
    private final int TABLES_ON_PAGE_DEFAULT_NUM = 8;
    public int getTablesOnPage() {
        if(tablesOnPage == 0) try {
            tablesOnPage = Integer.parseInt(PropertyReader.getPropertyValue(PropertyType.SERVER, "data.tablesOnPage"));
        } catch (Exception e) {
            tablesOnPage =  TABLES_ON_PAGE_DEFAULT_NUM;
        }
        return tablesOnPage;
    }
    private final String[] ORG_WEB_INFO_FIELDS = {"description","contact_data","web_site"};
    public JSONArray regionArray = null;
    @GetMapping(org)
    public String doGet() {
        return "views/organizationView";
    }

    @GetMapping(getOrg)
    public String doGetById(@PathVariable String id, Model model,
                            @RequestParam(required = false, defaultValue = "2") String doc,
                            @RequestParam(required = false, defaultValue = "1") String page) {
        if (prepareRequest(id, model, doc, page)) return "redirect:/";
        return "views/organizationView";
    }

    @GetMapping(editOrg)
    public String doEditById(@PathVariable String id, Model model,
                            @RequestParam(required = false, defaultValue = "2") String doc,
                            @RequestParam(required = false, defaultValue = "1") String page) {
        if(fetchRegions())
            model.addAttribute("regions_array", regionArray);
        model.addAttribute("edit", "true");
        if (prepareRequest(id, model, doc, page)) return "redirect:/";
        return "views/organizationEdit";
    }

    @GetMapping(getRegionTotal)
    public String doGetByRegion(@PathVariable String id, Model model,
                            @RequestParam(required = false, defaultValue = "2") String doc,
                            @RequestParam(required = false, defaultValue = "1") String page) {
        if (prepareRequest(id, model, doc, page)) return "redirect:/";
        return "views/organizationView";
    }

    @PostMapping(resetOrg)
    public String resetOrg(@PathVariable String id) throws SQLException{
        if (!validateId(id)) {
            Logger.log(this, "Invalid ID", 3);
            return "redirect:/";
        }
        Logger.log(this, "Resetting data of organization: " + id, 1);
        try {
            sqlExecutor().executeUpdate("Start transaction");
            if (sqlExecutor().executeCall(sqlExecutor().loadSqlResource("reset_oo1.sql"), id, "0") &&
                    sqlExecutor().executeCall(sqlExecutor().loadSqlResource("reset_oo2.sql"), id, "0")) {
                sqlExecutor().executeUpdate("commit");
                Logger.log(this, "Data reset is successful", 1);
            } else {
                sqlExecutor().executeUpdate("rollback");
                Logger.log(this, "Data reset is failed", 2);
            }
        } catch (SQLException e) {
            Logger.log(this, "Data reset is failed due to exception: " + e.getMessage(), 2);
            sqlExecutor().executeUpdate("rollback");
        }
        return "redirect:" + getOrg.replace("{id}", id);
    }

    @GetMapping(apps)
    public String getApplications(@PathVariable String id, Model model) {
        if (!validateId(id)) {
            Logger.log(this, "Invalid ID", 3);
            return "redirect:/";
        }
        try {
            JSONObject data = DatabaseServlet.convertResultSetToJSON(
                    sqlExecutor().executeSelect(sqlExecutor().loadSqlResource("get_org_info.sql"), id)
            );
            JSONArray apps = DatabaseServlet.convertResultSetToJSONArray(
                    sqlExecutor().executeSelectSimple("application", "*", "id_build = " + id)
            );
            model.addAttribute("org_id", id);
            model.addAttribute("org_data", data);
            model.addAttribute("app_data", apps);
            model.addAttribute("edit", "true");
            return "views/applications";
        } catch (Exception e) {
            Logger.log(this, e.getMessage(), 2);
            return "redirect:"+getOrg.replace("{id}", id);
        }
    }

    @PostMapping(randomizeOrg)
    public String randomizeOrg(@PathVariable String id) {
        if (!validateId(id)) {
            Logger.log(this, "Invalid ID", 3);
            return "redirect:/";
        }
        Logger.log(this, "Resetting data of organization: " + id, 1);
        if (sqlExecutor().executeCall(sqlExecutor().loadSqlResource("randomize_oo1.sql"), id) &&
                sqlExecutor().executeCall(sqlExecutor().loadSqlResource("randomize_oo2.sql"), id))
            Logger.log(this, "Data generated successfully", 1);
        else
            Logger.log(this, "Data generation failed", 2);
        return "redirect:" + getOrg.replace("{id}", id);
    }

    @PostMapping(updateOrgInfo)
    public String UpdateOrgInfo(@PathVariable("id") String id, HttpServletRequest request, RedirectAttributes attributes) {
        if (!validateId(id)) {
            Logger.log(this, "Invalid ID", 3);
            return "redirect:/data";
        }
        JSONParser jsonParser = new JSONParser();
        String json = request.getParameter("info_data");
        if((json != null && json.isEmpty()))
            return "redirect:/data";
        try {
            JSONObject data = (JSONObject) jsonParser.parse(json);
            if (data.size() == 0)
                return "redirect:/data";
            String[] params = new String[3];
            params[0] = (String) data.get("upd_desc");
            params[1] = (String) data.get("upd_cont");
            params[2] = (String) data.get("upd_web");
            String regionId = (String) data.get("upd_reg");
            StringBuilder updateParams = new StringBuilder();
            boolean hasValues = false;
            for(int i = 0; i < params.length; i++) {
                if(params[i] != null && !params[i].isEmpty()) {
                    updateParams.append(ORG_WEB_INFO_FIELDS[i]).append(" = \"").append(params[i]).append("\"");
                    if(i > 0 && i < params.length - 1) updateParams.append(",");
                    hasValues = true;
                }
            }
            if(hasValues) {
                sqlExecutor().executeUpdate(
                        sqlExecutor().loadSqlResource("update_org_web_info.sql"),
                        updateParams.toString(),
                        id
                );
            }
            if(fetchRegions()) {
                for (Object region : regionArray) {
                    if(((JSONObject)region).get("id").toString().equals(regionId)) {
                        sqlExecutor().executeUpdate("update build set id_region = @a0 where id = \"@a1\"", regionId, id);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            Logger.log(this, e.getMessage(), 2);
            ServletContext.showPopup(attributes, e.getLocalizedMessage(), "error");
        }
        return "redirect:"+getOrg.replace("{id}", id);
    }

    @PostMapping(updateOrg)
    public String updateOrgData(@PathVariable("id") String id, HttpServletRequest request, RedirectAttributes attributes) {
        if (!validateId(id)) {
            Logger.log(this, "Invalid ID", 3);
            return "redirect:/data";
        }
        JSONParser jsonParser = new JSONParser();
        String json = request.getParameter("updated_values");
        if((json != null && json.isEmpty()))
            return "redirect:/data";
        try {
            JSONObject data = (JSONObject) jsonParser.parse(json);
            if(data.size() == 0)
                return "redirect:/data";
            ArrayList<PreparedStatement> queries = new ArrayList<>(data.size());
            for(Object obj : data.values()) {
                String rowId = (String) ((JSONObject) obj).get("vr1_name");
                String columnName = (String) ((JSONObject) obj).get("vr2_name");
                String newValue = (String) ((JSONObject) obj).get("val");
                String table = (String) ((JSONObject) obj).get("table");
                String r1_name = (String) ((JSONObject) obj).get("r1");
                int updateType = Integer.parseInt((String) ((JSONObject) obj).get("updateType"));
                String tableVRNum;
                if(r1_name.isEmpty()) {
                    tableVRNum = table.replace("vrr", "r");
                    if (updateType == 1)
                        tableVRNum += "_1";
                } else {
                    tableVRNum = r1_name;
                }
                queries.add(sqlExecutor().prepareStatement(sqlExecutor().loadSqlResource(
                                String.format("doo_VR_update/doo_VR_update_%s.sql", updateType)
                        ), id, rowId, columnName, newValue, table, tableVRNum)
                );
                //Logger.log(this, String.format("Updated table \"%s\" for row \"%s\" new value: %s", table, rowId, newValue), 1);
            }
            for(PreparedStatement statement : queries) sqlExecutor().executeUpdate(statement);
        } catch (Exception e) {
            Logger.log(this, e.getMessage(), 2);
            ServletContext.showPopup(attributes, e.getLocalizedMessage(), "error");
        }
        return "redirect:"+getOrg.replace("{id}", id);
    }

    public static boolean validateId(String id) {
        return !(id == null || id.isEmpty() || id.equals("0"));
    }

    public boolean fetchRegions() {
        if(this.regionArray != null)
            return true;
        try {
            ResultSet resultSet = sqlExecutor().executeSelectSimple("region", "id, name", "id > 0");
            JSONArray regionArray = DatabaseServlet.convertResultSetToJSONArray(resultSet);
            if (regionArray.size() > 0) {
                this.regionArray = regionArray;
                return true;
            }
        } catch (Exception e) {
            Logger.log(OrganizationServlet.class, e.getMessage(), 3);
        }
        return false;
    }

    private void prepareOrganizationData(String id, Model model, String doc, String page, int tablesOnPage) {
        int pageNum;
        try {
            pageNum = Integer.parseInt(page);
        } catch (NumberFormatException e) {
            pageNum = 1;
        }
        if(pageNum < 1) pageNum = 1;
        int vrBegin = (pageNum - 1) * tablesOnPage;
        int vrEnd = pageNum * tablesOnPage - 1;
        DatabaseServlet.getOrganizationInfo(id, model, doc, tablesOnPage, pageNum, vrBegin, vrEnd);
    }

    private boolean prepareRequest(String id, Model model, String doc, String page) {
        if (!validateId(id)) {
            Logger.log(this, "Invalid ID", 3);
            return true;
        }
        if(doc == null || doc.isEmpty() || !StringUtils.isNumeric(doc)) doc = "0";
        if(doc.equals("2") || doc.equals("0"))
            sqlExecutor().executeCall(sqlExecutor().loadSqlResource("copy_for_oo1.sql"), id);
        if(doc.equals("3") || doc.equals("0"))
            sqlExecutor().executeCall(sqlExecutor().loadSqlResource("copy_for_oo2.sql"), id);
        prepareOrganizationData(id, model, doc, page, getTablesOnPage());
        return false;
    }
}
