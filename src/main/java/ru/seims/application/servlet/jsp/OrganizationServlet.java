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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
    public static final String generateExcel = WebSecurityConfiguration.orgEditorPattern+"org/{id}/generate/excel";
    public static final String uploadExcel = WebSecurityConfiguration.orgEditorPattern+"org/{id}/upload/excel";
    public static final String postUploadExcel = WebSecurityConfiguration.orgEditorPattern+"org/{id}/upload/excel";
    public static final String apps = WebSecurityConfiguration.orgEditorPattern+"org/{id}/apps";
    public static final String updateOrg = WebSecurityConfiguration.orgEditorPattern+"org/{id}/update/";
    public static final String postUploadImage = WebSecurityConfiguration.orgEditorPattern+"upload/image";
    public int tablesOnPage = 8;
    public static String excelExt = ".xls";
    public static String regionViewPageTitle = "Общие данные по региону ";
    private static SQLExecutor sqlExecutor() {
        return SQLExecutor.getInstance();
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

    @GetMapping(apps)
    public String getApplications(@PathVariable String id, Model model) {
        if (!validateId(id)) {
            Logger.log(this, "Invalid ID", 3);
            return "redirect:/";
        }
        JSONArray dataArray = new JSONArray();
        try {
            dataArray.add(DatabaseServlet.convertResultSetToJSON(
                    sqlExecutor().executeSelect(sqlExecutor().loadSQLResource("get_org_info.sql"), id)
            ));
            dataArray.add(DatabaseServlet.convertResultSetToJSONArray(
                    sqlExecutor().executeSelectSimple("images", "id", "id_build_web_info = " + id)
            ));
            model.addAttribute("org_id", id);
            model.addAttribute("org_data", dataArray.toString());
            return "views/applications";
        } catch (Exception e) {
            Logger.log(this, e.getMessage(), 2);
            return "redirect:"+getOrg.replace("{id}", id);
        }
    }

    @GetMapping(generateExcel)
    public ResponseEntity<ByteArrayResource> generate(@PathVariable String id,
                                                      @RequestParam(name = "type", defaultValue = "2") String type,
                                                      RedirectAttributes attributes)
    {
        if (!validateId(id) || !StringUtils.isNumeric(type)) {
            Logger.log(this, "Invalid ID", 3);
            return ResponseEntity.badRequest().build();
        }
        try {
            String templateFileName = type + excelExt;
            File file = new File(PropertyReader.getPropertyValue(PropertyType.SERVER,
                    "app.excelTemplatePath") + "/" + templateFileName);
            if(!file.exists() || !file.isFile())
                throw new IOException("Template file not found for type id " + type);
            ExcelReader reader = new ExcelReader();
            ExcelWriter writer = new ExcelWriter(reader.load(file, false));
            ResultSet resultSet;
            ResultSet tableData;
            resultSet = sqlExecutor().executeSelect(
                    sqlExecutor().loadSQLResource("get_vr_label_mapping.sql"), type, "0", "200");
            int sheet = 1;
            while (resultSet.next()) {
                String vr = resultSet.getString(1);
                String r1 = resultSet.getString(2);
                String r2 = resultSet.getString(3);
                byte updateType = resultSet.getByte(4);
                DataTable table = new DataTable(vr);
                tableData = getVRData(id, table, vr, r1, r2, updateType, SelectScope.Org);
                table.populate(tableData);
                writer.writeSheet = sheet++;
                writer.write(table);
            }
            String fileName = "output_buildid" + id + excelExt;
            ByteArrayOutputStream outFile = writer.saveBytes(fileName);
            HttpHeaders header = new HttpHeaders();
            header.setContentType(new MediaType("application", "force-download"));
            header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
            return new ResponseEntity<>(new ByteArrayResource(outFile.toByteArray()), header, HttpStatus.CREATED);
        } catch (Exception e) {
            Logger.log(DatabaseServlet.class, "Error during writing file: " + e.getMessage(), 2);
            ServletContext.showPopup(attributes, e.getLocalizedMessage(), "error");
        }
        return ResponseEntity.internalServerError().build();
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
                queries.add(sqlExecutor().prepareStatement(sqlExecutor().loadSQLResource(
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

    @GetMapping(uploadExcel)
    public String loadExcel(@PathVariable String id, Model model) {
        if (!validateId(id)) {
            Logger.log(this, "Invalid ID", 3);
            return "redirect:/";
        }
        model.addAttribute("org_id", id);
        return "views/excelLoader";
    }

    @PostMapping(postUploadExcel)
    public String uploadExcel(Model model,
                              @RequestParam MultipartFile file,
                              @RequestParam(name = "type", defaultValue = "2") String type,
                              @PathVariable String id,
                              RedirectAttributes attributes) {
        if (file.isEmpty() || !validateId(id) || !StringUtils.isNumeric(type)) {
            Logger.log(this, "File is empty", 3);
            ServletContext.showPopup(attributes, "File is empty", "error");
            return "redirect:"+uploadExcel;
        }
        try {
            File tmpFile = FileResourcesUtils.transferMultipartFile(file,
                    FileResourcesUtils.UPLOAD_PATH + "/" + file.getOriginalFilename());
            ExcelReader reader = new ExcelReader();
            reader.load(tmpFile, false);
            ResultSet resultSet;
            resultSet = sqlExecutor().executeSelect(
                    sqlExecutor().loadSQLResource("get_vr_label_mapping.sql"), type, "0", "200"
            );
            ArrayList<DataTable> tables = new ArrayList<>();
            JSONArray tableArrayJson = new JSONArray();
            boolean readMetricsFromDatabase = PropertyReader.getPropertyValue(PropertyType.SERVER, "data.excel.readMetricsFromDB")
                    .toLowerCase(Locale.ROOT).equals("true");
            while (resultSet.next() && reader.currentSheet <= reader.sheetCount) {
                String vr = resultSet.getString(1);
                byte updateType = resultSet.getByte(4);
                int cols = 0;
                 if(readMetricsFromDatabase)
                    cols = readMetricsFromDatabase(vr);
                DataTable table = reader.readNext(cols);
                table.setSysName(vr);
                table.setUpdateType(updateType);
                tables.add(table);
                tableArrayJson.add(table.toJSON());
            }
            resultSet.close();
            model.addAttribute("org_id", id);
            model.addAttribute("doc_type", type);
            model.addAttribute("table", tables.get(2));
            model.addAttribute("excel_tables", tableArrayJson);
        } catch (Exception e) {
            Logger.log(DatabaseServlet.class, "Error during file read/write: " + e.getLocalizedMessage(), 2);
            e.printStackTrace();
            ServletContext.showPopup(attributes, e.getLocalizedMessage(), "error");
            return "redirect:"+postUploadExcel;
        }
        String tables = DatabaseRestServlet.getSchemaTables();
        model.addAttribute("tables", tables);
        return "/views/previewExcel";
    }

    @PostMapping(postUploadImage)
    public String loadImage(@RequestParam MultipartFile file, @RequestParam(value = "orgId") int orgId) {
        if (!validateId(String.valueOf(orgId))) {
            Logger.log(this, "Invalid ID", 3);
            return "redirect:/";
        }
        try {
            File imgFile = FileResourcesUtils.transferMultipartFile(file,
                    FileResourcesUtils.RESOURCE_PATH + "temp/" + file.getOriginalFilename());
            if(sqlExecutor().uploadFile(imgFile, "images", orgId))
                Logger.log(this, "Uploaded image: " + file.getOriginalFilename(), 2);
            else
                Logger.log(this, "Cannot upload image: " + file.getOriginalFilename(), 2);
            if(imgFile.exists())
                imgFile.delete();
        } catch (Exception e) {
            Logger.log(this, e.getMessage(), 2);
        }
        return "redirect:"+getOrg.replace("{id}", String.valueOf(orgId));
    }

    private boolean validateId(String id) {
        return !(id == null || id.isEmpty() || id.equals("0"));
    }

    private int readMetricsFromDatabase(String vr) throws SQLException {
        ResultSet metricsSet;
        metricsSet = sqlExecutor().executeSelect(sqlExecutor().loadSQLResource("get_vr_display_name.sql"), vr);
        if (!metricsSet.next()) throw new RuntimeException("Mapping for " + vr + " not found");
        String r1 = metricsSet.getString("r1_name");
        String r2 = metricsSet.getString("r2_name");
        r1 = r1 == null ? vr.replace("vrr", "r") + "_1" : r1;
        r2 = r2 == null ? vr.replace("vrr", "r") + "_2" : r2;
        metricsSet = sqlExecutor().executeSelect(sqlExecutor().loadSQLResource("get_vrr_metrics.sql"), vr, r1, r2);
        if (!metricsSet.next()) throw new RuntimeException("Metrics for " + vr + " not found");
        int cols = metricsSet.getInt("cols");
        metricsSet.close();
        return cols;
    }
    private void prepareOrganizationData(String id, Model model, String doc, String page, int tablesOnPage, SelectScope selectScope) {
        int pageNum;
        try {
            pageNum = Integer.parseInt(page);
        } catch (NumberFormatException e) {
            pageNum = 1;
        }
        if(pageNum < 1) pageNum = 1;
        int vrBegin = (pageNum - 1) * tablesOnPage;
        int vrEnd = pageNum * tablesOnPage - 1;
        int maxPage = 1;
        ArrayList<DataTable> tablesData = new ArrayList<>();
        JSONObject orgData = new JSONObject();
        JSONArray appData = new JSONArray();
        try {
            if(selectScope.equals(SelectScope.Org)) {
                orgData = DatabaseServlet.convertResultSetToJSON(
                        sqlExecutor().executeSelect(sqlExecutor().loadSQLResource("get_org_info.sql"), id)
                );
                appData = DatabaseServlet.convertResultSetToJSONArray(
                        sqlExecutor().executeSelectSimple("images", "id", "id_build_web_info = " + id)
                );
            }
            if (!orgData.isEmpty() || selectScope.equals(SelectScope.Reg)) {
                ResultSet resultSet;
                if(doc.equals("0"))
                    resultSet = sqlExecutor().executeSelect(sqlExecutor().loadSQLResource("get_vr_label_mapping_all.sql"),
                            String.valueOf(vrBegin), String.valueOf(vrEnd));
                else
                    resultSet = sqlExecutor().executeSelect(sqlExecutor().loadSQLResource("get_vr_label_mapping.sql"),
                            doc, String.valueOf(vrBegin), String.valueOf(vrEnd));
                while (resultSet.next()) {
                    String vr = resultSet.getString("vr_name");
                    String r1 = resultSet.getString("r1_name");
                    String r2 = resultSet.getString("r2_name");
                    byte updateType = resultSet.getByte("update_type");
                    byte vrCount = resultSet.getByte("vr_count");
                    maxPage = (vrCount / tablesOnPage) + (vrCount % tablesOnPage == 0 ? 0 : 1);

                    DataTable table = new DataTable(vr);
                    ResultSet tableData = getVRData(id, table, vr, r1, r2, updateType, selectScope);
                    try {
                        generateTableToFromResultSet(tablesData, tableData, updateType);
                    } catch (NullPointerException e) {
                        Logger.log(OrganizationServlet.class, e.getMessage(), 2);
                    }
                }
                if(selectScope.equals(SelectScope.Reg)) {
                    //TODO: add display info
                    resultSet = sqlExecutor().executeSelect("");
                }
            }
        } catch (Exception e) {
            Logger.log(OrganizationServlet.class, e.getMessage(), 2);
            e.printStackTrace();
        }
        if(selectScope.equals(SelectScope.Reg)) {
            orgData.put("name", regionViewPageTitle);
            //TODO: add display name
            //orgData.put("org_data", null);
        }
        model.addAttribute("tables", tablesData);
        model.addAttribute("org_id", id);
        model.addAttribute("vr_type", doc);
        model.addAttribute("page", pageNum);
        model.addAttribute("max_page", maxPage);
        model.addAttribute("org_data", orgData.toString());
        model.addAttribute("app_data", appData.toString());
        model.addAttribute("name", orgData.get("name"));
        model.addAttribute("district", orgData.get("name"));
    }

    public static ResultSet getVRData(String id, DataTable table, String vr, String r1, String r2, byte updateType, SelectScope selectScope) throws SQLException {
        ResultSet tableData;
        String queryForVR;
        if(updateType == 1) {
            ArrayList<String> data = table.generateLabelForVR(vr, r2, sqlExecutor(), selectScope);
            queryForVR = table.generateQueryForVR(sqlExecutor(), id, data, vr, r1, selectScope);
        } else if(updateType == 2) {
            queryForVR = table.generateQueryForVR(sqlExecutor(), id, null, vr, r1, selectScope);
        } else throw new IllegalArgumentException("Invalid update type fot table: " + vr);
        tableData = sqlExecutor().executeSelect(queryForVR);
        return tableData;
    }

    private boolean prepareRequest(String id, Model model, String doc, String page, SelectScope selectScope) {
        if (!validateId(id)) {
            Logger.log(this, "Invalid ID", 3);
            return true;
        }
        if(doc.equals("1")) doc = "2";
        if(selectScope.equals(SelectScope.Org))
            sqlExecutor().executeCall(sqlExecutor().loadSQLResource("copy_for_oo1.sql"), id);
        prepareOrganizationData(id, model, doc, page, tablesOnPage, selectScope);
        return false;
    }

    public static void generateTableToFromResultSet(ArrayList<DataTable> tablesData, ResultSet tableData, byte updateType) throws SQLException {
        String tableSysName = tableData.getMetaData().getTableName(1);
        String tableDisplayName = tableSysName;
        String r1Name = null;
        String r2Name = null;
        ResultSet rs = sqlExecutor().executeSelect(
                sqlExecutor().loadSQLResource("get_vr_display_name.sql"),
                tableData.getMetaData().getTableName(1));
        if(rs.next()) {
            tableDisplayName = rs.getString("display_name");
            String r2Str = rs.getString("r2_name");
            if(r2Str != null) {
                r2Name = r2Str;
            }
            String r1Str = rs.getString("r1_name");
            if(r1Str != null) {
                r1Name = r1Str;
            }
        }
        DataTable table = new DataTable(tableDisplayName, tableSysName);
        table.populate(tableData);
        table.setUpdateType(updateType);
        table.setIsChild(r2Name != null);
        table.setR1Name(r1Name);
        table.setR2Name(r2Name);
        tablesData.add(table);
    }
}
