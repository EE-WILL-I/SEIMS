package ru.seims.application.servlet.rest;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.seims.database.entitiy.DataTable;
import ru.seims.database.proccessing.InsertQueryBuilder;
import ru.seims.database.entitiy.StoredImage;
import ru.seims.utils.excel.ExcelParser;
import ru.seims.utils.FileResourcesUtils;
import ru.seims.utils.json.JSONBuilder;
import ru.seims.utils.logging.Logger;
import ru.seims.utils.properties.PropertyReader;
import ru.seims.utils.properties.PropertyType;
import ru.seims.database.proccessing.SQLExecutor;
import ru.seims.application.security.authorization.AuthenticationService;
import ru.seims.application.servlet.ServletUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@RestController
public class DatabaseRestServlet {
    @GetMapping("/api/data/tables")
    @ResponseBody
    public String getSchemaTablesJSON() {
        return getSchemaTables();
    }

    @GetMapping("/api/data/image/{id}")
    @ResponseBody
    public StoredImage getImage(@PathVariable String id) {
        try {
            return new StoredImage(id);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getSchemaTables() {
        SQLExecutor executor = SQLExecutor.getInstance();
        ResultSet resultSet = executor.executeSelect(executor.loadSQLResource("get_tables.sql"),
                PropertyReader.getPropertyValue(PropertyType.DATABASE, "datasource.schema"));
        JSONBuilder data = new JSONBuilder();
        try {
            while (resultSet.next()) {
                data.addSubJSONElement(new JSONBuilder().addAVP("table", resultSet.getString("table_name")).getString());
            }
            resultSet.close();
            return "[" + data.getString().substring(1, data.getString().length() - 1) + "]";
        } catch (Exception e) {
            Logger.log(DatabaseRestServlet.class, e.getMessage(), 2);
            return new JSONBuilder().addAVP("status", "error").addAVP("message", e.getMessage()).getString();
        }
    }
}
