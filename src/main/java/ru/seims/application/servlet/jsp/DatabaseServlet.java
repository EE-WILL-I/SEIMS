package ru.seims.application.servlet.jsp;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.web.multipart.MultipartFile;
import ru.seims.application.servlet.rest.DatabaseRestServlet;
import ru.seims.application.servlet.ServletUtils;
import ru.seims.database.proccessing.InsertQueryBuilder;
import ru.seims.utils.FileResourcesUtils;
import ru.seims.utils.excel.ExcelParser;
import ru.seims.utils.logging.Logger;
import ru.seims.database.entitiy.DataTable;
import  ru.seims.database.proccessing.SQLExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class DatabaseServlet {
    private static final String defaultTable = "organizations";
    @GetMapping("/data")
    public String doGetDef(@ModelAttribute(name = "show_popup") String showPopup,
                           @ModelAttribute(name = "popup_message") String popupMessage,
                           RedirectAttributes attributes) {
        if(!showPopup.isEmpty() && !popupMessage.isEmpty())
            ServletUtils.showPopup(attributes, popupMessage, showPopup);
        return "redirect:/data/get/table/"+defaultTable;
    }

    @GetMapping("/data/get/table/{tableName}")
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
            ServletUtils.showPopup(attributes, "Can't load page because unable to fetch data from the database. Check connection status.", "error");
            return "redirect:/";
        } else {
            ServletUtils.showPopup(attributes, "Can't load table " + tableName, "error");
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
            ServletUtils.showPopup(attributes, "Can't execute query " + script, "error");
            return "redirect:/data/get/query/"+defaultTable;
        }
    }

    @GetMapping("/data/upload")
    public String upload(Model model, @ModelAttribute("error") String error) {
        if(!error.isEmpty())
            model.addAttribute("errorMessage", error);
        return "redirect:/data/excel";
    }

    @GetMapping("/data/excel")
    public String uploadExcel(Model model, @ModelAttribute("error") String error) {
        if(!error.isEmpty())
            model.addAttribute("errorMessage", error);
        return "/views/excelLoader";
    }

    @GetMapping("/data/image")
    public String uploadImage(Model model, @ModelAttribute("error") String error) {
        if(!error.isEmpty())
            model.addAttribute("errorMessage", error);
        return "/views/imageLoader";
    }

    @GetMapping("/data/load/image/{id}")
    public String getImage(Model model, @PathVariable String id) {
        model.addAttribute("image_id", id);
        return "views/imageView";
    }

    @PostMapping("/data/upload/excel")
    public String doPost(Model model, @RequestParam MultipartFile file,
                         RedirectAttributes attributes, HttpServletRequest request) {
        if (file.isEmpty()) {
            Logger.log(this, "File is empty", 3);
            ServletUtils.showPopup(attributes, "File is empty", "error");
            return "redirect:/data/upload";
        }
        ExcelParser excelParser = new ExcelParser();
        try {
            File tmpFile = FileResourcesUtils.transferMultipartFile(file, FileResourcesUtils.RESOURCE_PATH
                    + "temp/excelData.tmp");
            model.addAttribute("table", excelParser.getTable(excelParser.read(tmpFile), file.getOriginalFilename()));
        } catch (Exception e) {
            Logger.log(this, e.getMessage(), 3);
            ServletUtils.showPopup(attributes, e.getLocalizedMessage(), "error");
            return "redirect:/data/upload";
        }
        String tables = DatabaseRestServlet.getSchemaTables();
        model.addAttribute("tables", tables);
        return "/views/previewExcel";
    }

    @PostMapping("/data/upload/image")
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

    @GetMapping("/data/delete/{tableName}")
    public String doDelete(@PathVariable(value = "tableName") String table,
                           @RequestParam(value = "column") String column,
                           @RequestParam(value = "value") String value,
                           RedirectAttributes attributes) {
        if(table.isEmpty())
            return "redirect:/data";
        try {
            SQLExecutor.getInstance().executeUpdate(SQLExecutor.getInstance().loadSQLResource("delete_any.sql"),
                    table, column, value);
        } catch (SQLException e) {
            Logger.log(this, e.getLocalizedMessage(), 2);
            ServletUtils.showPopup(attributes, e.getLocalizedMessage(), "error");
        }
        return "redirect:/data/get/table/" + table;
    }

    @PostMapping("/data/insert/json/{tableName}")
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
            InsertQueryBuilder queryBuilder = new InsertQueryBuilder(tableName,
                    executor.loadSQLResource("insert_" + tableName + ".sql"));
            ArrayList<String> rowData = new ArrayList<>(data.size());
            while(iterator.hasNext()) {
                JSONObject obj = iterator.next();
                rowData.add((String) obj.get("newValue"));
            }
            queryBuilder.addRow(rowData.toArray(new String[0]));
            executor.executeUpdate(queryBuilder.getStatement());
            Logger.log(this, "Updated table " + tableName, 1);
        } catch (Exception e) {
            Logger.log(this, e.getMessage(), 2);
            ServletUtils.showPopup(attributes, e.getLocalizedMessage(), "error");
        }
        return "redirect:/data/get/table/" + tableName;
    }

    @PostMapping("/data/update/{tableName}")
    public String doPut(HttpServletRequest request, @PathVariable(value = "tableName") String table,
                        RedirectAttributes attributes) {
        JSONParser jsonParser = new JSONParser();
        String json = request.getParameter("updated_values");
        if((json != null && json.isEmpty()) || table.isEmpty())
            return "redirect:/data";
        try {
            JSONArray data = (JSONArray) jsonParser.parse(json);
            if(data.size() == 0)
                return "redirect:/data";
            Iterator<JSONObject> iterator = data.iterator();
            while(iterator.hasNext()) {
                JSONObject obj = iterator.next();
                String rowId = (String) obj.get("id");
                String columnName = (String) obj.get("col");
                String newValue = (String) obj.get("val");
                SQLExecutor executor = SQLExecutor.getInstance();
                executor.executeUpdate(executor.loadSQLResource("update_any.sql"),
                        table, columnName, newValue, "id = '" + rowId + "'");
            }
            Logger.log(this, "Updated table " + table, 1);
        } catch (Exception e) {
            Logger.log(this, e.getMessage(), 2);
            ServletUtils.showPopup(attributes, e.getLocalizedMessage(), "error");
        }
        return "redirect:/data/get/table" + table;
    }

    @PostMapping("/data/update/org/{id}")
    public String updateOrgData(@PathVariable("id") String id, HttpServletRequest request, RedirectAttributes attributes) {
        JSONParser jsonParser = new JSONParser();
        String json = request.getParameter("updated_values");
        if((json != null && json.isEmpty()))
            return "redirect:/data";
        try {
            JSONArray data = (JSONArray) jsonParser.parse(json);
            if(data.size() == 0)
                return "redirect:/data";
            Iterator<JSONObject> iterator = data.iterator();
            while(iterator.hasNext()) {
                JSONObject obj = iterator.next();
                String rowName = (String) obj.get("vr1_name");
                String columnName = (String) obj.get("vr2_name");
                String newValue = (String) obj.get("val");
                String table = (String) obj.get("table");
                Pattern numPattern = Pattern.compile("\\d+");
                Matcher numMatcher = numPattern.matcher(table);
                String tableVRNum = numMatcher.find() ? numMatcher.group() : "0";
                SQLExecutor executor = SQLExecutor.getInstance();
                int updateType = 1;
                ResultSet rs = executor.executeSelect(executor.loadSQLResource("get_doo_vr_update_type.sql"), table);
                if(rs.next()) updateType = rs.getInt(1);
                if(executor.executeUpdate(executor.loadSQLResource(
                        String.format("doo_VR_update/doo_VR_update_%s.sql", updateType)),
                        id, rowName, updateType == 1 ? columnName : "null", newValue, tableVRNum)) {
                    Logger.log(this, String.format("Updated table \"%s\" for row \"%s\" new value: %s", table, rowName, newValue), 1);
                }
            }
        } catch (Exception e) {
            Logger.log(this, e.getMessage(), 2);
            ServletUtils.showPopup(attributes, e.getLocalizedMessage(), "error");
        }
        return "redirect:/org/get/"+id;
    }

    @PostMapping("/data/insert/{tableName}")
    public String doPost(HttpServletRequest request, @PathVariable("tableName") String tableName,
                         RedirectAttributes attributes) {
        if (tableName.isEmpty() || tableName.equals("none"))
            return "redirect:/data";
        DataTable table = (DataTable) request.getSession().getAttribute("table");
        SQLExecutor executor = SQLExecutor.getInstance();
        try {
            InsertQueryBuilder insertBuilder = new InsertQueryBuilder(tableName,
                    executor.loadSQLResource("insert_" + tableName + ".sql"));
            for (Map<String, String> rowData : table.getDataRows()) {
                List<String> row = new ArrayList<>(table.columnCount);
                for (String column : table.getColumnLabels()) {
                    row.add(rowData.get(column));
                }
                insertBuilder.addRow(row.toArray(new String[0]));
            }
            executor.executeUpdate(insertBuilder.getStatement());
            return "redirect:/data/" + tableName;
        } catch (Exception e) {
            Logger.log(this, e.getMessage(), 3);
            ServletUtils.showPopup(attributes, e.getMessage(), "error");
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
            table.populateTable(resultSet);
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
            table.populateTable(resultSet);
            resultSet.close();
            model.addAttribute("table", table);
            return true;
        } catch (Exception e) {
            Logger.log(this, "Error during parsing data from DB: " + e.getMessage(), 2);
            return false;
        }
    }
}
