package ru.seims.application.servlet.jsp;

import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.web.multipart.MultipartFile;
import ru.seims.application.configuration.WebSecurityConfiguration;
import ru.seims.application.servlet.rest.DatabaseRestServlet;
import ru.seims.application.servlet.ServletContext;
import ru.seims.utils.FileResourcesUtils;
import ru.seims.utils.excel.ExcelReader;
import ru.seims.utils.logging.Logger;
import ru.seims.database.entitiy.DataTable;
import  ru.seims.database.proccessing.SQLExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

@Controller
public class DatabaseServlet {
    public static final String data = "/data";
    public static final String getTable = WebSecurityConfiguration.dbEditorPattern+"get/table/{tableName}";
    public static final String getTableAPI = WebSecurityConfiguration.dbEditorPattern+"get/table";
    public static final String updateTable = WebSecurityConfiguration.dbEditorPattern+"update/{tableName}";
    public static final String insertJson = WebSecurityConfiguration.dbEditorPattern+"insert/json/{tableName}";
    public static final String insertJsonAPI = WebSecurityConfiguration.dbEditorPattern+"insert/json/";
    public static final String uploadExcel = WebSecurityConfiguration.orgEditorPattern+"org/upload/excel";
    public static final String uploadImage = WebSecurityConfiguration.orgEditorPattern+"org/upload/image/{id}";
    public static final String postUploadImage = WebSecurityConfiguration.orgEditorPattern+"upload/image";
    public static final String deleteFromTable = WebSecurityConfiguration.dbEditorPattern+"delete/{tableName}";
    public static final String updateOrg = WebSecurityConfiguration.orgEditorPattern+"org/update/{id}";
    public static final String insertExcel = WebSecurityConfiguration.orgEditorPattern+"org/insert/excel/{id}";
    private static final String defaultTable = "build";
    @GetMapping(data)
    public String doGetDef(@ModelAttribute(name = "show_popup") String showPopup,
                           @ModelAttribute(name = "popup_message") String popupMessage,
                           RedirectAttributes attributes) {
        if(showPopup != null && !showPopup.isEmpty() && popupMessage != null && !popupMessage.isEmpty())
            ServletContext.showPopup(attributes, popupMessage, showPopup);
        return "redirect:/data/get/table/"+defaultTable;
    }

    @GetMapping(getTable)
    public String doGet(HttpServletRequest request, Model model, @PathVariable(value = "tableName") String tableName,
                        RedirectAttributes attributes) {
        String tables = DatabaseRestServlet.getSchemaTables();
        model.addAttribute("tables", tables);
        if(tableName.isEmpty() || tableName.equals("none")) {
            return "redirect:/data/get/table/"+defaultTable;
        }
        if(loadTable(model, tableName)) {
            return "views/dataView";
        } else if(tableName.equals(defaultTable)) {
            ServletContext.showPopup(attributes, "Can't load page because unable to fetch data from the database. Check connection status.", "error");
            return "redirect:/";
        } else {
            ServletContext.showPopup(attributes, "Can't load table " + tableName, "error");
            return "redirect:/data/get/table/"+defaultTable;
        }
    }

    @GetMapping("data/get/query/{script}")
    public String getQuery(@PathVariable String script,
                           @RequestParam(required = false) String[] args,
                           Model model, RedirectAttributes attributes) {
        if(script.isEmpty()) {
            return "redirect:/data/get/table/"+defaultTable;
        }
        if(executeQuery(model, script, args)) {
            return "views/dataView";
            //return "views/queryView";
        } else {
            ServletContext.showPopup(attributes, "Can't execute query " + script, "error");
            return "redirect:/data/get/query/"+defaultTable;
        }
    }

    @GetMapping("/data/upload")
    public String upload(Model model, @ModelAttribute("error") String error) {
        if(!error.isEmpty())
            model.addAttribute("errorMessage", error);
        return "redirect:/data/excel";
    }

    @GetMapping(uploadExcel)
    public String uploadExcel(Model model, @ModelAttribute("error") String error) {
        if(!error.isEmpty())
            model.addAttribute("errorMessage", error);
        return "/views/excelLoader";
    }

    @GetMapping("data/upload/image")
    public String uploadImage(Model model, @ModelAttribute("error") String error) {
        if(!error.isEmpty())
            model.addAttribute("errorMessage", error);
        return "/views/imageLoader";
    }

    @GetMapping(uploadImage)
    public String getImage(Model model, @PathVariable String id) {
        model.addAttribute("image_id", id);
        return "views/imageView";
    }

    @PostMapping(postUploadImage)
    public String loadImage(Model model, @RequestParam MultipartFile file, @RequestParam(value = "orgId") int orgId,
                            RedirectAttributes attributes, HttpServletRequest request) {
        try {
            File imgFile = FileResourcesUtils.transferMultipartFile(file,
                    FileResourcesUtils.RESOURCE_PATH + "temp/" + file.getOriginalFilename());
            if(SQLExecutor.getInstance().uploadFile(imgFile, "images", orgId))
                Logger.log(this, "Uploaded image: " + file.getOriginalFilename(), 2);
            else
                Logger.log(this, "Cannot upload image: " + file.getOriginalFilename(), 2);
            if(imgFile.exists())
                imgFile.delete();
        } catch (Exception e) {
            Logger.log(this, e.getMessage(), 2);
        }
        return "redirect:/data/upload";
    }

    @PostMapping("/data/upload/application")
    public String loadApp(Model model, @RequestParam MultipartFile file, @RequestParam(value = "orgId") int orgId,
                            RedirectAttributes attributes, HttpServletRequest request) {
        try {
            File imgFile = FileResourcesUtils.transferMultipartFile(file,
                    FileResourcesUtils.RESOURCE_PATH + "temp/" + file.getOriginalFilename());
            if(SQLExecutor.getInstance().uploadFile(imgFile, "applications", orgId))
                Logger.log(this, "Uploaded file: " + file.getOriginalFilename(), 2);
            else
                Logger.log(this, "Cannot upload file: " + file.getOriginalFilename(), 2);
            if(imgFile.exists())
                imgFile.delete();
        } catch (Exception e) {
            Logger.log(this, e.getMessage(), 2);
        }
        return "redirect:/data/upload";
    }

    @GetMapping(deleteFromTable)
    public String doDelete(@PathVariable(value = "tableName") String table,
                           @RequestParam(value = "column", required = false, defaultValue = "") String column,
                           @RequestParam(value = "value", required = false, defaultValue = "") String value,
                           RedirectAttributes attributes) {
        if(table.isEmpty() || column.isEmpty() || value.isEmpty())
            return "redirect:/data";
        try {
            SQLExecutor.getInstance().executeUpdate(SQLExecutor.getInstance().loadSQLResource("delete_any.sql"),
                    table, column, value);
        } catch (SQLException e) {
            Logger.log(this, e.getLocalizedMessage(), 2);
            ServletContext.showPopup(attributes, e.getLocalizedMessage(), "error");
        }
        return "redirect:/data/get/table/" + table;
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
        return "redirect:/data/get/table/" + tableName;
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
                executor.executeUpdate(executor.loadSQLResource("update_any.sql"),
                        table, columnName, newValue, "id = '" + rowId + "'");
            }
            Logger.log(this, "Updated table " + table, 1);
        } catch (Exception e) {
            Logger.log(this, e.getMessage(), 2);
            ServletContext.showPopup(attributes, e.getLocalizedMessage(), "error");
        }
        return "redirect:/data/get/table/" + table;
    }

    @PostMapping(updateOrg)
    public String updateOrgData(@PathVariable("id") String id, HttpServletRequest request, RedirectAttributes attributes) {
        JSONParser jsonParser = new JSONParser();
        String json = request.getParameter("updated_values");
        if((json != null && json.isEmpty()))
            return "redirect:/data";
        try {
            JSONObject data = (JSONObject) jsonParser.parse(json);
            if(data.size() == 0)
                return "redirect:/data";
            for(Object obj : data.values()) {
                String rowId = (String) ((JSONObject)obj).get("vr1_name");
                String columnName = (String) ((JSONObject)obj).get("vr2_name");
                String newValue = (String) ((JSONObject)obj).get("val");
                String table = (String) ((JSONObject)obj).get("table");
                int updateType = Integer.parseInt((String) ((JSONObject)obj).get("updateType"));
                String tableVRNum = table.replace("vrr", "r");
                if(updateType == 1)
                    tableVRNum += "_1";
                SQLExecutor executor = SQLExecutor.getInstance();
                ResultSet rs = executor.executeSelect(executor.loadSQLResource("get_vr_update_type.sql"), table);
                if(rs.next()) updateType = rs.getInt(1);
                if(executor.executeUpdate(executor.loadSQLResource(
                        String.format("doo_VR_update/doo_VR_update_%s.sql", updateType)),
                        id, rowId, columnName, newValue, table, tableVRNum)) {
                    Logger.log(this, String.format("Updated table \"%s\" for row \"%s\" new value: %s", table, rowId, newValue), 1);
                }
            }
        } catch (Exception e) {
            Logger.log(this, e.getMessage(), 2);
            ServletContext.showPopup(attributes, e.getLocalizedMessage(), "error");
        }
        return "redirect:/org/"+id;
    }

    @PostMapping(insertExcel)
    public String uploadExcel(@PathVariable("id") String id,
                              @RequestParam(name = "type") String type,
                              HttpServletRequest request,
                              RedirectAttributes attributes) {
        if (id.isEmpty() || !StringUtils.isNumeric(type)) {
            Logger.log(this, "Invalid ID", 3);
            ServletContext.showPopup(attributes, "Invalid parameters", "error");
            return "redirect:/data";
        }
        try {
            String json = request.getParameter("tables_data");
            JSONArray tablesData = ((JSONArray) new JSONParser().parse(json));
            ArrayList<DataTable> tables = new ArrayList<>();
            for (Object tableData : tablesData) {
                tables.add(new DataTable().populate(((JSONObject) tableData)));
            }
            SQLExecutor executor = SQLExecutor.getInstance();
            ExcelReader reader = new ExcelReader();
            for (DataTable table : tables) {
                PreparedStatement statement = reader.prepareStatement(table.getSysName(), table, id);
                System.out.println(statement.toString());
                executor.executeUpdate(statement);
            }
            return "redirect:/data";
        } catch (Exception e) {
            Logger.log(this, e.getMessage(), 3);
            ServletContext.showPopup(attributes, e.getMessage(), "error");
            return "redirect:/data";
        }
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
            ResultSet resultSet = executor.executeSelect(executor.loadSQLResource(script), args);
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
