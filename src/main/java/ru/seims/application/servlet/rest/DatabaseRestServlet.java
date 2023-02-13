package ru.seims.application.servlet.rest;

import org.springframework.web.bind.annotation.*;
import ru.seims.database.entitiy.StoredImage;
import ru.seims.utils.json.JSONBuilder;
import ru.seims.utils.logging.Logger;
import ru.seims.utils.properties.PropertyReader;
import ru.seims.utils.properties.PropertyType;
import ru.seims.database.proccessing.SQLExecutor;

import javax.servlet.http.HttpServletResponse;
import java.sql.ResultSet;

@RestController
public class DatabaseRestServlet {
    @GetMapping("/tables")
    @ResponseBody
    public String getSchemaTablesJSON() {
        return getSchemaTables();
    }

    @GetMapping("/image/{id}")
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
