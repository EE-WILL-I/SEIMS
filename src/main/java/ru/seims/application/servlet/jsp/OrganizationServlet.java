package ru.seims.application.servlet.jsp;

import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.seims.application.configuration.WebSecurityConfiguration;
import ru.seims.application.servlet.ServletContext;
import ru.seims.application.servlet.rest.DatabaseRestServlet;
import ru.seims.database.entitiy.DataTable;
import ru.seims.database.proccessing.SQLExecutor;
import ru.seims.utils.FileResourcesUtils;
import ru.seims.utils.excel.ExcelReader;
import ru.seims.utils.excel.ExcelWriter;
import ru.seims.utils.logging.Logger;
import ru.seims.utils.properties.PropertyReader;
import ru.seims.utils.properties.PropertyType;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Locale;

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
    @GetMapping(org)
    public String doGet() {
        return "views/organizationView";
    }

    @GetMapping(getOrg)
    public String doGetById(@PathVariable String id, Model model,
                            @RequestParam(required = false, defaultValue = "0") String doc,
                            @RequestParam(required = false, defaultValue = "1") String page) {
        if (prepareRequest(id, model, doc, page, SelectScope.Org)) return "redirect:/";
        return "views/organizationView";
    }

    @GetMapping(editOrg)
    public String doEditById(@PathVariable String id, Model model,
                            @RequestParam(required = false, defaultValue = "0") String doc,
                            @RequestParam(required = false, defaultValue = "1") String page) {
        model.addAttribute("edit", "true");
        if (prepareRequest(id, model, doc, page, SelectScope.Org)) return "redirect:/";
        return "views/organizationEdit";
    }

    @GetMapping(getRegionTotal)
    public String doGetByRegion(@PathVariable String id, Model model,
                            @RequestParam(required = false, defaultValue = "0") String doc,
                            @RequestParam(required = false, defaultValue = "1") String page) {
        if (prepareRequest(id, model, doc, page, SelectScope.Reg)) return "redirect:/";
        return "views/organizationView";
    }

    @PostMapping(resetOrg)
    public String resetOrg(@PathVariable String id) {
        if (!validateId(id)) {
            Logger.log(this, "Invalid ID", 3);
            return "redirect:/";
        }
        Logger.log(this, "Resetting data of organization: " + id, 1);
        if (sqlExecutor().executeCall(sqlExecutor().loadSqlResource("reset_oo1.sql"), id))
            Logger.log(this, "Data reset is successful", 1);
        else
            Logger.log(this, "Data reset is failed", 2);
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
        if (sqlExecutor().executeCall(sqlExecutor().loadSqlResource("randomize_oo1.sql"), id))
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
            String description = data.get("upd_desc").toString();
            String region = data.get("upd_reg").toString();
            String webInfo = data.get("upd_web").toString();
            String contactData = data.get("upd_cont").toString();

            //TODO: add implementation

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

    private void prepareOrganizationData(String id, Model model, String doc, String page, int tablesOnPage, SelectScope selectScope) {
        int pageNum;
        try {
            pageNum = Integer.parseInt(page);
        } catch (NumberFormatException e) {
            pageNum = 1;
        }
        if(pageNum < 1) pageNum = 1;
        int vrBegin = (pageNum - 1) * getTablesOnPage();
        int vrEnd = pageNum * getTablesOnPage() - 1;
        int maxPage = 1;
        DatabaseServlet.getOrganizationInfo(id, model, doc, getTablesOnPage(), selectScope, pageNum, vrBegin, vrEnd, maxPage);
    }

    private boolean prepareRequest(String id, Model model, String doc, String page, SelectScope selectScope) {
        if (!validateId(id)) {
            Logger.log(this, "Invalid ID", 3);
            return true;
        }
        if(doc.equals("1")) doc = "2";
        if(selectScope.equals(SelectScope.Org))
            sqlExecutor().executeCall(sqlExecutor().loadSqlResource("copy_for_oo1.sql"), id);
        prepareOrganizationData(id, model, doc, page, getTablesOnPage(), selectScope);
        return false;
    }
}
