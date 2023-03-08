package ru.seims.application.servlet.jsp;

import org.apache.commons.lang.StringUtils;
import org.apache.tomcat.jni.OS;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.multipart.MultipartFile;
import ru.seims.application.configuration.WebSecurityConfiguration;
import ru.seims.application.servlet.rest.DatabaseRestServlet;
import ru.seims.application.servlet.ServletContext;
import ru.seims.utils.FileResourcesUtils;
import ru.seims.utils.excel.ExcelReader;
import ru.seims.utils.excel.ExcelWriter;
import ru.seims.utils.logging.Logger;
import ru.seims.database.entitiy.DataTable;
import  ru.seims.database.proccessing.SQLExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.seims.utils.properties.PropertyReader;
import ru.seims.utils.properties.PropertyType;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

@Controller
public class DatabaseServlet {
    public static final String data = WebSecurityConfiguration.dbEditorPattern+"data";
    public static final String getTable = WebSecurityConfiguration.dbEditorPattern+"get/table/{tableName}";
    public static final String getTableAPI = WebSecurityConfiguration.dbEditorPattern+"get/table";
    public static final String updateTable = WebSecurityConfiguration.dbEditorPattern+"update/{tableName}";
    public static final String updateTableAPI = WebSecurityConfiguration.dbEditorPattern+"update";
    public static final String insertJson = WebSecurityConfiguration.dbEditorPattern+"insert/json/{tableName}";
    public static final String insertJsonAPI = WebSecurityConfiguration.dbEditorPattern+"insert/json/";
    public static final String deleteFromTable = WebSecurityConfiguration.dbEditorPattern+"delete/{tableName}";
    public static final String deleteFromTableAPI = WebSecurityConfiguration.dbEditorPattern+"delete";
    public static final String insertExcel = WebSecurityConfiguration.orgEditorPattern+"org/insert/excel/{id}";
    public static final String generateExcel = WebSecurityConfiguration.orgEditorPattern+"org/{id}/generate/excel";
    public static final String uploadImage = WebSecurityConfiguration.orgEditorPattern+"org/{id}/upload/image";
    public static final String uploadApplication = WebSecurityConfiguration.orgEditorPattern+"org/{id}/upload/app";
    public static final String deleteApplication = WebSecurityConfiguration.orgEditorPattern+"org/{id}/delete/app/{appId}";
    public static final String uploadExcel = WebSecurityConfiguration.orgEditorPattern+"org/{id}/upload/excel";
    public static final String postUploadExcel = WebSecurityConfiguration.orgEditorPattern+"org/{id}/upload/excel";
    public static final String postUploadImage = WebSecurityConfiguration.orgEditorPattern+"org/{id}/upload/image";
    public static final String downloadApplication = WebSecurityConfiguration.orgEditorPattern+"org/{id}/app/{appId}";
    public static String excelExt = ".xls";
    public static String ORG_IMG_FILE_NAME = "preview";
    public static String ORG_IMG_FILE_EXT = ".jpg";
    private String defaultTable;
    private String getDefaultTable() {
        if(defaultTable == null) try {
            defaultTable = PropertyReader.getPropertyValue(PropertyType.SERVER, "data.defTable");
        } catch (Exception e) {
            defaultTable = "build";
        }
        return defaultTable;
    }
    private static SQLExecutor sqlExecutor() {
        return SQLExecutor.getInstance();
    }
    @GetMapping(data)
    public String doGetDef(@ModelAttribute(name = "show_popup") String showPopup,
                           @ModelAttribute(name = "popup_message") String popupMessage,
                           RedirectAttributes attributes) {
        if(showPopup != null && !showPopup.isEmpty() && popupMessage != null && !popupMessage.isEmpty())
            ServletContext.showPopup(attributes, popupMessage, showPopup);
        return "redirect:"+getTableAPI+"/"+getDefaultTable();
    }

    @GetMapping(getTable)
    public String doGet(Model model, @PathVariable(value = "tableName") String tableName,
                        RedirectAttributes attributes) {
        String tables = DatabaseRestServlet.getSchemaTables();
        model.addAttribute("tables", tables);
        if(tableName.isEmpty() || tableName.equals("none")) {
            return "redirect:"+getTableAPI+"/"+getDefaultTable();
        }
        if(loadTable(model, tableName)) {
            return "views/dataView";
        } else if(tableName.equals(getDefaultTable())) {
            ServletContext.showPopup(attributes, "Can't load page because unable to fetch data from the database. Check connection status.", "error");
            return "redirect:/";
        } else {
            ServletContext.showPopup(attributes, "Can't load table " + tableName, "error");
            return "redirect:"+getTableAPI+"/"+getDefaultTable();
        }
    }

    @GetMapping(uploadImage)
    public String uploadImage(Model model, @PathVariable String id) {
        if (!OrganizationServlet.validateId(id))
            return "redirect:/";
        model.addAttribute("org_id", id);
        return "/views/imageLoader";
    }

    @GetMapping(downloadApplication)
    public ResponseEntity<ByteArrayResource> download(@PathVariable String id, @PathVariable String appId, RedirectAttributes attributes) {
        if (!OrganizationServlet.validateId(id) || !OrganizationServlet.validateId(appId)) {
            Logger.log(this, "Invalid ID", 3);
            return ResponseEntity.badRequest().build();
        }
        try {
            String filePath = FileResourcesUtils.UPLOAD_PATH + "/" + id + "/";
            ResultSet resultSet = sqlExecutor().executeSelectSimple("application", "path, format", "id = " + appId);
            resultSet.next();
            String fileName = resultSet.getString("path");
            String fileExtension = resultSet.getString("format");
            fileName += fileExtension;
            filePath += fileName;
            File outFile = new File(filePath);
            byte[] bytes = Files.readAllBytes(outFile.toPath());
            HttpHeaders header = new HttpHeaders();
            header.setContentType(new MediaType("application", "force-download"));
            header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
            return new ResponseEntity<>(new ByteArrayResource(bytes), header, HttpStatus.CREATED);
        } catch (Exception e) {
            Logger.log(DatabaseServlet.class, "Error during writing file: " + e.getMessage(), 2);
            ServletContext.showPopup(attributes, e.getLocalizedMessage(), "error");
        }
        return ResponseEntity.internalServerError().build();
    }

    @GetMapping(generateExcel)
    public ResponseEntity<ByteArrayResource> generate(@PathVariable String id,
                                                      @RequestParam(name = "type", defaultValue = "2") String type,
                                                      RedirectAttributes attributes)
    {
        if (!OrganizationServlet.validateId(id) || !StringUtils.isNumeric(type)) {
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
                    sqlExecutor().loadSqlResource("get_vr_label_mapping.sql"), type, "0", "200");
            int sheet = 1;
            while (resultSet.next()) {
                String vr = resultSet.getString(1);
                String r1 = resultSet.getString(2);
                String r2 = resultSet.getString(3);
                byte updateType = resultSet.getByte(4);
                DataTable table = new DataTable(vr);
                tableData = getVRData(id, table, vr, r1, r2, updateType, OrganizationServlet.SelectScope.Org);
                table.populate(tableData);
                writer.writeSheet = sheet++;
                writer.write(table);
            }
            String fileName = "output_" + id + "_" + new Timestamp(System.currentTimeMillis()) + excelExt;
            ByteArrayOutputStream outFile = writer.saveBytes(id + "/" + fileName);
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

    @GetMapping("data/get/query/{script}")
    public String getQuery(@PathVariable String script,
                           @RequestParam(required = false) String[] args,
                           Model model, RedirectAttributes attributes) {
        if(script.isEmpty()) {
            return "redirect:"+getTableAPI+"/"+getDefaultTable();
        }
        if(executeQuery(model, script, args)) {
            return "views/dataView";
            //return "views/queryView";
        } else {
            ServletContext.showPopup(attributes, "Can't execute query " + script, "error");
            return "redirect:/data/get/query/"+getDefaultTable();
        }
    }

    @GetMapping("/data/upload")
    public String upload(Model model, @ModelAttribute("error") String error) {
        if (!error.isEmpty())
            model.addAttribute("errorMessage", error);
        return "redirect:/data/excel";
    }

    @GetMapping(deleteFromTable)
    public String doDelete(@PathVariable(value = "tableName") String table,
                           @RequestParam(value = "column", required = false, defaultValue = "") String column,
                           @RequestParam(value = "value", required = false, defaultValue = "") String value,
                           RedirectAttributes attributes) {
        if(table.isEmpty() || column.isEmpty() || value.isEmpty())
            return "redirect:/data";
        try {
            SQLExecutor.getInstance().executeUpdate(SQLExecutor.getInstance().loadSqlResource("delete_any.sql"),
                    table, column, value);
        } catch (SQLException e) {
            Logger.log(this, e.getLocalizedMessage(), 2);
            ServletContext.showPopup(attributes, e.getLocalizedMessage(), "error");
        }
        return "redirect:"+getTableAPI+"/"+getDefaultTable();
    }

    @PostMapping(insertJson)
    public String doPostJSON(HttpServletRequest request, @PathVariable("tableName") String tableName,
                             RedirectAttributes attributes) {
        JSONParser jsonParser = new JSONParser();
        String json = request.getParameter("new_data");
        if((json != null && json.isEmpty()) || tableName.isEmpty())
            return "redirect:/data";
        try {
            JSONArray data = (JSONArray) jsonParser.parse(json);
            if(data.size() == 0)
                return "redirect:/data";
            Iterator<JSONObject> iterator = data.iterator();
            SQLExecutor executor = SQLExecutor.getInstance();
            //InsertQueryBuilder queryBuilder = new InsertQueryBuilder(tableName,
              //      executor.loadSQLResource("insert_" + tableName + ".sql"));
            ArrayList<String> rowData = new ArrayList<>(data.size());
            while(iterator.hasNext()) {
                JSONObject obj = iterator.next();
                rowData.add((String) obj.get("newValue"));
            }
           // queryBuilder.addRow(rowData.toArray(new String[0]));
            //executor.executeUpdate(queryBuilder.getStatement());
            Logger.log(this, "Updated table " + tableName, 1);
        } catch (Exception e) {
            Logger.log(this, e.getMessage(), 2);
            ServletContext.showPopup(attributes, e.getLocalizedMessage(), "error");
        }
        return "redirect:"+getTableAPI+"/"+getDefaultTable();
    }

    @PostMapping(updateTable)
    public String doPut(HttpServletRequest request, @PathVariable(value = "tableName") String table,
                        RedirectAttributes attributes) {
        JSONParser jsonParser = new JSONParser();
        String json = request.getParameter("updated_values");
        if((json != null && json.isEmpty()) || table.isEmpty())
            return "redirect:/data";
        try {
            JSONObject data = (JSONObject) jsonParser.parse(json);
            if(data.size() == 0)
                return "redirect:/data";
            for(Object obj : data.values()) {
                String rowId = (String) ((JSONObject)obj).get("id");
                String columnName = (String) ((JSONObject)obj).get("col");
                String newValue = (String) ((JSONObject)obj).get("val");
                SQLExecutor executor = SQLExecutor.getInstance();
                executor.executeUpdate(executor.loadSqlResource("update_any.sql"),
                        table, columnName, newValue, "id = '" + rowId + "'");
            }
            Logger.log(this, "Updated table " + table, 1);
        } catch (Exception e) {
            Logger.log(this, e.getMessage(), 2);
            ServletContext.showPopup(attributes, e.getLocalizedMessage(), "error");
        }
        return "redirect:"+getTableAPI+"/"+getDefaultTable();
    }

    @GetMapping(uploadExcel)
    public String loadExcel(@PathVariable String id, Model model) {
        if (!OrganizationServlet.validateId(id)) {
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
        if (file.isEmpty() || !OrganizationServlet.validateId(id) || !StringUtils.isNumeric(type)) {
            Logger.log(this, "File is empty", 3);
            ServletContext.showPopup(attributes, "File is empty", "error");
            return "redirect:"+uploadExcel;
        }
        String filePath = FileResourcesUtils.UPLOAD_PATH + "/" + id;
        try {
            String[] fileAttrs = file.getOriginalFilename().split("\\.");
            boolean replacement = new File(filePath+"/"+file.getOriginalFilename()).exists();
            File tmpFile = FileResourcesUtils.transferMultipartFile(
                    file, filePath,  file.getOriginalFilename()
            );
            ExcelReader reader = new ExcelReader();
            reader.load(tmpFile, false);
            ResultSet resultSet;
            resultSet = sqlExecutor().executeSelect(
                    sqlExecutor().loadSqlResource("get_vr_label_mapping.sql"), type, "0", "200"
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
            if(replacement) {
                sqlExecutor().executeUpdate(sqlExecutor().loadSqlResource("delete_application.sql"), id);
            }
            sqlExecutor().executeUpdate(sqlExecutor().loadSqlResource("insert_application.sql"),
                    fileAttrs[0], excelExt, String.valueOf(file.getSize()), "2", id);
        } catch (Exception e) {
            Logger.log(DatabaseServlet.class, "Error during file read/write: " + e.getLocalizedMessage(), 2);
            e.printStackTrace();
            ServletContext.showPopup(attributes, e.getLocalizedMessage(), "error");
            File temp = new File(filePath + "/" + file.getOriginalFilename());
            if(temp.exists())
                temp.delete();
            return "redirect:"+postUploadExcel;
        }

        String tables = DatabaseRestServlet.getSchemaTables();
        model.addAttribute("tables", tables);
        return "/views/previewExcel";
    }

    @PostMapping(uploadApplication)
    public String uploadApplication(@RequestParam MultipartFile file, @PathVariable String id,
                                    @RequestAttribute(required = false) String name,
                                    @RequestAttribute(required = false) String ext) {
        if (!OrganizationServlet.validateId(id)) {
            Logger.log(this, "Invalid ID", 3);
            return "redirect:/";
        }
        try {
            String filePath = FileResourcesUtils.UPLOAD_PATH + "/" + id;
            String fileName;
            if (name == null || name.isEmpty() || ext == null || ext.isEmpty()) {
                fileName = file.getOriginalFilename();
                name = fileName.split("\\.")[0];
                ext = "." + fileName.split("\\.")[1];
            }
            fileName = name + ext;
            boolean replacement = new File(filePath + "/" + fileName).exists();
            File imgFile = FileResourcesUtils.transferMultipartFile(
                    file, filePath, fileName
            );
            if (replacement) {
                sqlExecutor().executeUpdate(sqlExecutor().loadSqlResource("delete_application.sql"), id);
            }
            sqlExecutor().executeUpdate(sqlExecutor().loadSqlResource("insert_application.sql"),
                    name, ext, String.valueOf(file.getSize()), "1", id);
            sqlExecutor().executeUpdate(sqlExecutor().loadSqlResource("set_org_image.sql"),
                    name, id);
        } catch (Exception e) {
            Logger.log(this, e.getMessage(), 2);
        }
        return "redirect:"+OrganizationServlet.apps.replace("{id}", id);
    }

    @PostMapping(deleteApplication)
    public String deleteApplication(@PathVariable String id, @PathVariable String appId) {
        if (!OrganizationServlet.validateId(id)) {
            Logger.log(this, "Invalid ID", 3);
            return "redirect:/";
        }
        Logger.log(this, "Deleting application " + appId + " from org " + id, 1);
        try {
            String filePath = FileResourcesUtils.UPLOAD_PATH + "/" + id + "/";
            ResultSet resultSet = sqlExecutor().executeSelectSimple("application", "path, format, type_id", "id = " + appId);
            resultSet.next();
            String fileName = resultSet.getString("path");
            String fileExtension = resultSet.getString("format");
            int appType = resultSet.getInt("type_id");
            String fullName = fileName + fileExtension;
            filePath += fullName;
            File appFile = new File(filePath);
            Logger.log(this, "Deleting file " + filePath, 1);
            if((appFile.exists() && appFile.isFile()) && appFile.delete()) {
                Logger.log(this, " File deleted", 1);
                sqlExecutor().executeUpdate(sqlExecutor().loadSqlResource("delete_application.sql"), appId);
                if(appType == 1) {
                    sqlExecutor().executeUpdate(sqlExecutor().loadSqlResource("delete_org_image.sql"), id, appId);
                }
            }
            Logger.log(this, " Application " + appId + " deleted", 1);
        } catch (Exception e) {
            Logger.log(this, e.getMessage(), 2);
            Logger.log(this, " Application was not " + appId + " deleted", 1);
        }
        return "redirect:"+OrganizationServlet.apps.replace("{id}", id);
    }

    @PostMapping(postUploadImage)
    public String loadImage(@RequestParam MultipartFile file, @PathVariable String id) {
        if (!OrganizationServlet.validateId(id)) {
            Logger.log(this, "Invalid ID", 3);
            return "redirect:/";
        }
        try {
            uploadApplication(file, id, ORG_IMG_FILE_NAME, ORG_IMG_FILE_EXT);
            sqlExecutor().executeUpdate(sqlExecutor().loadSqlResource("set_org_image.sql"), ORG_IMG_FILE_NAME, id);
        } catch (SQLException e) {
            Logger.log(this, e.getMessage(), 2);
        }
        return "redirect:"+OrganizationServlet.apps.replace("{id}", id);
    }

    @PostMapping(insertExcel)
    public String uploadExcel(@PathVariable("id") String id,
                              @RequestParam(name = "type") String type,
                              HttpServletRequest request,
                              RedirectAttributes attributes) throws SQLException {
        if (id.isEmpty() || !StringUtils.isNumeric(type)) {
            Logger.log(this, "Invalid ID", 3);
            ServletContext.showPopup(attributes, "Invalid parameters", "error");
            return "redirect:/data";
        }
        SQLExecutor executor = SQLExecutor.getInstance();
        try {
            String json = request.getParameter("tables_data");
            JSONArray tablesData = ((JSONArray) new JSONParser().parse(json));
            ArrayList<DataTable> tables = new ArrayList<>();
            for (Object tableData : tablesData) {
                tables.add(new DataTable().populate(((JSONObject) tableData)));
            }
            ExcelReader reader = new ExcelReader();

            String typeResource;
            switch (type) {
                case "2": {
                    typeResource = "clean_oo1.sql";
                    break;
                }
                case "3": {
                    typeResource = "clean_oo2.sql";
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Document type not defined");
                }
            }
            executor.executeUpdate("Start transaction");
            executor.executeCall(executor.loadSqlResource(typeResource), id);
            for (DataTable table : tables) {
                PreparedStatement statement = reader.prepareStatement(table.getSysName(), table, id);
                Logger.log(this, "Executing for " + table.getName(), 1);
                executor.executeUpdate(statement);
            }
            executor.executeUpdate("commit");
            executor.executeUpdate("update build_db_info set `generated` = 1 where id = @a0", id);
            executor.executeUpdate("update build_db_info set upd_date  = CURRENT_TIMESTAMP where id = @a0", id);
            return "redirect:"+OrganizationServlet.getOrg.replace("{id}", id);
        } catch (Exception e) {
            executor.executeUpdate("rollback");
            Logger.log(this, e.getMessage(), 3);
            ServletContext.showPopup(attributes, e.getMessage(), "error");
            return "redirect:/data";
        }
    }

    public static ResultSet getVRData(String id, DataTable table, String vr, String r1, String r2, byte updateType, OrganizationServlet.SelectScope selectScope) throws SQLException {
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

    public static JSONObject convertResultSetToJSON(ResultSet rs) {
        JSONObject json = new JSONObject();
        try {
            ResultSetMetaData metaData = rs.getMetaData();
            while (rs.next()) {
                int numColumns = metaData.getColumnCount();
                for (int i = 1; i <= numColumns; i++) {
                    String column_name = metaData.getColumnLabel(i);
                    json.put(column_name, rs.getObject(column_name));
                }
            }
        } catch (SQLException e) {
            Logger.log(DatabaseServlet.class, e.getMessage(), 2);
        }
        return json;
    }

    public static JSONArray convertResultSetToJSONArray(ResultSet rs) {
        JSONArray jsonArray = new JSONArray();
        try {
            ResultSetMetaData metaData = rs.getMetaData();
            while (rs.next()) {
                int numColumns = metaData.getColumnCount();
                JSONObject jsonObj = new JSONObject();
                for (int i = 1; i <= numColumns; i++) {
                    String column_name = metaData.getColumnLabel(i);
                    jsonObj.put(column_name, rs.getObject(column_name));
                }
                jsonArray.add(jsonObj);
            }
            rs.close();
        } catch (SQLException e) {
            Logger.log(DatabaseServlet.class, e.getMessage(), 2);
        }
        return jsonArray;
    }

    private int readMetricsFromDatabase(String vr) throws SQLException {
        ResultSet metricsSet;
        metricsSet = sqlExecutor().executeSelect(sqlExecutor().loadSqlResource("get_vr_display_name.sql"), vr);
        if (!metricsSet.next()) throw new RuntimeException("Mapping for " + vr + " not found");
        String r1 = metricsSet.getString("r1_name");
        String r2 = metricsSet.getString("r2_name");
        r1 = r1 == null ? vr.replace("vrr", "r") + "_1" : r1;
        r2 = r2 == null ? vr.replace("vrr", "r") + "_2" : r2;
        metricsSet = sqlExecutor().executeSelect(sqlExecutor().loadSqlResource("get_vrr_metrics.sql"), vr, r1, r2);
        if (!metricsSet.next()) throw new RuntimeException("Metrics for " + vr + " not found");
        int cols = metricsSet.getInt("cols");
        metricsSet.close();
        return cols;
    }

    public static void generateTableFromResultSet(ArrayList<DataTable> tablesData, ResultSet tableData, byte updateType) throws SQLException {
        String tableSysName = tableData.getMetaData().getTableName(1);
        String tableDisplayName = tableSysName;
        String r1Name = null;
        String r2Name = null;
        ResultSet rs = sqlExecutor().executeSelect(
                sqlExecutor().loadSqlResource("get_vr_display_name.sql"),
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

    public static void getOrganizationInfo(String id, Model model, String doc, int tablesOnPage, OrganizationServlet.SelectScope selectScope, int pageNum, int vrBegin, int vrEnd, int maxPage) {
        ArrayList<DataTable> tablesData = new ArrayList<>();
        JSONObject orgData = new JSONObject();
        JSONArray appData = new JSONArray();
        String imageName = "";
        try {
            if(selectScope.equals(OrganizationServlet.SelectScope.Org)) {
                orgData = DatabaseServlet.convertResultSetToJSON(
                        sqlExecutor().executeSelect(sqlExecutor().loadSqlResource("get_org_info.sql"), id)
                );
                appData = DatabaseServlet.convertResultSetToJSONArray(
                        sqlExecutor().executeSelectSimple("application", "*", "id_build = " + id)
                );
            }
            if (!orgData.isEmpty() || selectScope.equals(OrganizationServlet.SelectScope.Reg)) {
                ResultSet resultSet;
                if(orgData.get("id_img") != null)
                    imageName = ORG_IMG_FILE_NAME+ORG_IMG_FILE_EXT;
                if(doc.equals("0"))
                    resultSet = sqlExecutor().executeSelect(sqlExecutor().loadSqlResource("get_vr_label_mapping_all.sql"),
                            String.valueOf(vrBegin), String.valueOf(vrEnd));
                else
                    resultSet = sqlExecutor().executeSelect(sqlExecutor().loadSqlResource("get_vr_label_mapping.sql"),
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
                        generateTableFromResultSet(tablesData, tableData, updateType);
                    } catch (NullPointerException e) {
                        Logger.log(OrganizationServlet.class, e.getMessage(), 2);
                    }
                }
            }
        } catch (Exception e) {
            Logger.log(OrganizationServlet.class, e.getMessage(), 2);
            e.printStackTrace();
        }
        model.addAttribute("tables", tablesData);
        model.addAttribute("org_id", id);
        model.addAttribute("vr_type", doc);
        model.addAttribute("page", pageNum);
        model.addAttribute("max_page", maxPage);
        model.addAttribute("org_data", orgData);
        model.addAttribute("app_data", appData);
        model.addAttribute("image_filename", imageName);
    }

    private boolean loadTable(Model model, String tableName) {
        SQLExecutor executor = SQLExecutor.getInstance();
        try {
            ResultSet resultSet = executor.executeSelectSimple(tableName, "*", "");
            DataTable table = new DataTable(resultSet.getMetaData().getTableName(1));
            table.populate(resultSet);
            resultSet.close();
            model.addAttribute("table", table);
            return true;
        } catch (Exception e) {
            Logger.log(this, "Error during parsing data from DB: " + e.getMessage(), 2);
            return false;
        }
    }

    private boolean executeQuery(Model model, String script, String... args) {
        SQLExecutor executor = SQLExecutor.getInstance();
        try {
            ResultSet resultSet = executor.executeSelect(executor.loadSqlResource(script), args);
            DataTable table = new DataTable(resultSet.getMetaData().getTableName(1));
            table.populate(resultSet);
            resultSet.close();
            model.addAttribute("table", table);
            return true;
        } catch (Exception e) {
            Logger.log(this, "Error during parsing data from DB: " + e.getMessage(), 2);
            return false;
        }
    }
}
