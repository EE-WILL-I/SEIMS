package ru.seims.application.servlet.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.seims.application.servlet.jsp.OrganizationServlet;
import ru.seims.database.entitiy.DataTable;
import ru.seims.database.entitiy.Organization;
import ru.seims.database.proccessing.SQLExecutor;
import ru.seims.utils.FileResourcesUtils;
import ru.seims.utils.json.JSONBuilder;
import ru.seims.utils.logging.Logger;

import java.io.File;
import java.sql.ResultSet;
import java.util.ArrayList;

@RestController
public class OrganizationRestServlet {
    public static String organizationTableName = "organizations";
    @GetMapping("/api/org/get/{id}")
    public Organization getOrgById(@PathVariable String id) {
        return getOrganizationById(id);
    }

    @GetMapping("/api/org/get/region/{region}")
    @ResponseBody
    public ArrayList<DataTable> getRegionData(@PathVariable String region) {
        if (region == null || region.isEmpty())
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
                    OrganizationServlet.generateTableToFromResultSet(tablesData, executor, tableData);
                }
            }
            return tablesData;
        } catch (Exception e) {
            Logger.log(this, e.getMessage(), 2);
        }
        return null;
    }

    public static Organization getOrganizationById(String id) {
        if(id.isEmpty())
            return null;
        try {
            SQLExecutor executor = SQLExecutor.getInstance();
            ResultSet rs = executor.executeSelectSimple(organizationTableName,
                    "*", "id like '"+ id +"'");
            if(rs.next()) {
                String name = rs.getString("name");
                String type = rs.getString("type");
                String district = rs.getString("district");
                String pageId = "";
                return new Organization(id, type, name, district, pageId);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
