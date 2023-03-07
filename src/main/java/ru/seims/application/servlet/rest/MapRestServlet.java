package ru.seims.application.servlet.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.seims.database.proccessing.SQLExecutor;
import ru.seims.utils.json.JSONBuilder;
import ru.seims.utils.logging.Logger;

import java.sql.ResultSet;

@RestController
public class MapRestServlet {
    public static final String getDistrictAPI = "/api/map/districtData/";
    private final String getDistrict = "/api/map/districtData/{id}";
    @GetMapping(getDistrict)
    public String fetchOrganizationsDataOfDistrict(@PathVariable String id) {
        //response.addHeader("Access-Control-Allow-Origin", "*");
        JSONBuilder builder = new JSONBuilder();
        builder.openArray();
        SQLExecutor executor = SQLExecutor.getInstance();
        Logger.log(this, "Fetching data for district: " + id, 4);
        ResultSet resultSet = executor.executeSelect(executor.loadSqlResource("get_orgs_from_region.sql"), id);
        try {
            while (resultSet.next()) {
                builder.addSubJSONElement(new JSONBuilder()
                        .addAVP("id", resultSet.getString("id"))
                        .addAVP("type", resultSet.getString("id_type"))
                        .addAVP("name", resultSet.getString("name").replaceAll("\"", "'"))
                        .getString());
            }
        } catch (Exception e) {
            Logger.log(this, e.getMessage(), 2);
            return new JSONBuilder().addAVP("error", e.getMessage()).getString();
        }
        builder.closeArray();
        //Thread.sleep(500);
        return builder.getRawString();
    }
}
