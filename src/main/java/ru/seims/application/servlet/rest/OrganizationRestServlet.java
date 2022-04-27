package ru.seims.application.servlet.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.seims.database.entitiy.Organization;
import ru.seims.database.proccessing.SQLExecutor;
import ru.seims.utils.json.JSONBuilder;

import java.sql.ResultSet;

@RestController
public class OrganizationRestServlet {
    public static String organizationTableName = "organizations";
    @GetMapping("/open-api/org/get/{id}")
    public Organization getOrgById(@PathVariable String id) {
        return getOrganizationById(id);
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
