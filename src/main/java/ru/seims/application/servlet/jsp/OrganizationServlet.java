package ru.seims.application.servlet.jsp;

import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.seims.application.servlet.ServletUtils;
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
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@Controller()
public class OrganizationServlet {
    public int tablesOnPage = 5;
    public static String excelExt = ".xls";
    @GetMapping("org/")
    public String doGet() {
        return "views/organizationView";
    }

    @GetMapping("org/{id}")
    public String doGetById(@PathVariable String id, Model model,
                            @RequestParam(required = false, defaultValue = "0") String doc,
                            @RequestParam(required = false, defaultValue = "1") String page) {
        if (id == null || id.isEmpty())
            id = "0";
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
        JSONArray dataArray = new JSONArray();
        try {
            SQLExecutor executor = SQLExecutor.getInstance();
            dataArray.add(DatabaseServlet.convertResultSetToJSON(
                    executor.executeSelect(executor.loadSQLResource("get_org_info.sql"), id)
            ));
            dataArray.add(DatabaseServlet.convertResultSetToJSONArray(
                    executor.executeSelectSimple("images", "id", "id_build_web_info = " + id)
            ));
            if (!dataArray.isEmpty()) {
                ResultSet resultSet;
                if(doc.equals("0"))
                    resultSet = executor.executeSelect(executor.loadSQLResource("get_vr_label_mapping_all.sql"),
                            String.valueOf(vrBegin), String.valueOf(vrEnd));
                else
                    resultSet = executor.executeSelect(executor.loadSQLResource("get_vr_label_mapping.sql"),
                            doc, String.valueOf(vrBegin), String.valueOf(vrEnd));
                while (resultSet.next()) {
                    String vr = resultSet.getString(1);
                    String r1 = resultSet.getString(2);
                    String r2 = resultSet.getString(3);
                    byte updateType = resultSet.getByte(4);
                    byte vrCount = resultSet.getByte(5);
                    maxPage = (vrCount / tablesOnPage) + (vrCount % tablesOnPage == 0 ? 0 : 1);

                    DataTable table = new DataTable(vr);
                    ResultSet tableData = getVRData(id, table, executor, vr, r1, r2, updateType);
                    try {
                        generateTableToFromResultSet(tablesData, executor, tableData, updateType);
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
        model.addAttribute("org_data", dataArray.toString());
        return "views/organizationView";
    }

    @GetMapping("/org/region/{region}")
    public String doGetByRegion(@PathVariable String region, Model model) {
        if(region == null || region.isEmpty())
            region = "Аннинский";
        try {
            ArrayList<DataTable> tablesData = new ArrayList<>();
            SQLExecutor executor = SQLExecutor.getInstance();
            File queryDir = new File(FileResourcesUtils.RESOURCE_PATH + executor.SQL_RESOURCE_PATH + "/doo_VR_region");
            for (final File query : queryDir.listFiles()) {
                if (query != null && query.isFile()) {
                    ResultSet tableData = executor.executeSelect(
                            executor.loadSQLResource("doo_VR_region/" + query.getName()), region
                    );
                    //generateTableToFromResultSet(tablesData, executor, tableData);
                }
            }
            model.addAttribute("tables", tablesData);
            model.addAttribute("region", region);
        } catch (Exception e) {
            Logger.log(this, e.getMessage(), 2);
        }
        return "views/regionView";
    }

    @GetMapping("/org/{id}/apps")
    public String getApplications(@PathVariable String id, Model model) {
        if (id.isEmpty()) {
            Logger.log(this, "Invalid ID", 3);
            return "redirect:/";
        }
        JSONArray dataArray = new JSONArray();
        try {
            SQLExecutor executor = SQLExecutor.getInstance();
            dataArray.add(DatabaseServlet.convertResultSetToJSON(
                    executor.executeSelect(executor.loadSQLResource("get_org_info.sql"), id)
            ));
            dataArray.add(DatabaseServlet.convertResultSetToJSONArray(
                    executor.executeSelectSimple("images", "id", "id_build_web_info = " + id)
            ));
            model.addAttribute("org_id", id);
            model.addAttribute("org_data", dataArray.toString());
            return "views/applications";
        } catch (Exception e) {
            Logger.log(this, e.getMessage(), 2);
            return "redirect:/org/"+id;
        }
    }

    @GetMapping("/org/{id}/generate/excel")
    public ResponseEntity<ByteArrayResource> generate(@PathVariable String id,
                                                      @RequestParam(name = "type", defaultValue = "2") String type,
                                                      RedirectAttributes attributes) {
        if (id.isEmpty() || !StringUtils.isNumeric(type)) {
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
            SQLExecutor executor = SQLExecutor.getInstance();
            resultSet = executor.executeSelect(
                    executor.loadSQLResource("get_vr_label_mapping.sql"), type, "0", "200");
            int sheet = 1;
            while (resultSet.next()) {
                String vr = resultSet.getString(1);
                String r1 = resultSet.getString(2);
                String r2 = resultSet.getString(3);
                byte updateType = resultSet.getByte(4);
                DataTable table = new DataTable(vr);
                tableData = getVRData(id, table, executor, vr, r1, r2, updateType);
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
            ServletUtils.showPopup(attributes, e.getLocalizedMessage(), "error");
        }
        return ResponseEntity.internalServerError().build();
    }

    @GetMapping("/org/{id}/upload/excel")
    public String loadExcel(@PathVariable String id, Model model) {
        if (id.isEmpty()) {
            Logger.log(this, "Invalid ID", 3);
            return "redirect:/";
        }
        model.addAttribute("org_id", id);
        return "views/excelLoader";
    }

    @PostMapping("/org/{id}/upload/excel")
    public String uploadExcel(Model model,
                              @RequestParam MultipartFile file,
                              @RequestParam(name = "type", defaultValue = "2") String type,
                              @PathVariable String id,
                              RedirectAttributes attributes) {
        if (file.isEmpty() || id.isEmpty() || !StringUtils.isNumeric(type)) {
            Logger.log(this, "File is empty", 3);
            ServletUtils.showPopup(attributes, "File is empty", "error");
            return "redirect:/data/upload";
        }
        try {
            File tmpFile = FileResourcesUtils.transferMultipartFile(file,
                    FileResourcesUtils.UPLOAD_PATH + "/" + file.getOriginalFilename());
            ExcelReader reader = new ExcelReader();
            reader.load(tmpFile, false);
            ResultSet resultSet;
            SQLExecutor executor = SQLExecutor.getInstance();
            resultSet = executor.executeSelect(
                    executor.loadSQLResource("get_vr_label_mapping.sql"), type, "0", "200"
            );
            ArrayList<DataTable> tables = new ArrayList<>();
            JSONArray tableArrayJson = new JSONArray();
            while (resultSet.next() && reader.currentSheet <= reader.sheetCount) {
                String vr = resultSet.getString(1);
                byte updateType = resultSet.getByte(4);
                DataTable table = reader.readNext();
                table.setSysName(vr);
                table.setUpdateType(updateType);
                tables.add(table);
                tableArrayJson.add(table.toJSON());
            }
            model.addAttribute("org_id", id);
            model.addAttribute("doc_type", type);
            model.addAttribute("table", tables.get(2));
            model.addAttribute("excel_tables", tableArrayJson);
        } catch (Exception e) {
            Logger.log(DatabaseServlet.class, "Error during writing file: " + e.getMessage(), 2);
            ServletUtils.showPopup(attributes, e.getLocalizedMessage(), "error");
            return "redirect:/data/upload";
        }
        String tables = DatabaseRestServlet.getSchemaTables();
        model.addAttribute("tables", tables);
        return "/views/previewExcel";
    }

    public static ResultSet getVRData(String id, DataTable table, SQLExecutor executor, String vr, String r1, String r2, byte updateType) throws SQLException {
        ResultSet tableData;
        String queryForVR;
        if(updateType == 1) {
            ArrayList<String> data = table.generateLabelForVR(vr, r2);
            queryForVR = table.generateQueryForVR(id, data, vr, r1);
        } else if(updateType == 2) {
            queryForVR = table.generateQueryForVR(id, null, vr, r1);
        } else throw new IllegalArgumentException("Invalid update type fot table: " + vr);
        tableData = executor.executeSelect(queryForVR);
        return tableData;
    }

    public static void generateTableToFromResultSet(ArrayList<DataTable> tablesData, SQLExecutor executor, ResultSet tableData, byte updateType) throws SQLException {
        String tableSysName = tableData.getMetaData().getTableName(1);
        String tableDisplayName = tableSysName;
        ResultSet rs = executor.executeSelect(
                executor.loadSQLResource("get_vr_display_name.sql"),
                tableData.getMetaData().getTableName(1));
        if(rs.next())
            tableDisplayName = rs.getString(1);
        DataTable table = new DataTable(tableDisplayName, tableSysName);
        table.populate(tableData);
        table.setUpdateType(updateType);
        tablesData.add(table);
    }
}
