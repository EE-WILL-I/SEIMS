package ru.seims.application.servlet.jsp;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.seims.application.servlet.rest.OrganizationRestServlet;
import ru.seims.database.entitiy.Organization;
import ru.seims.database.proccessing.SQLExecutor;
import ru.seims.utils.logging.Logger;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

@Controller
public class OrganizationServlet {
    @GetMapping("/org")
    public String doGet() {
        return "views/organizationView";
    }

    @GetMapping("/org/get/{id}")
    public String doGetById(@PathVariable String id, Model model) {
        if(id == null || id.isEmpty())
            id = "0";
        try {
            ResultSet rs = SQLExecutor.getInstance().executeSelect(SQLExecutor.getInstance().loadSQLResource("doo_VR7.sql"), id);
            JSONArray json = new JSONArray();
            ResultSetMetaData rsmd = rs.getMetaData();
            while (rs.next()) {
                int numColumns = rsmd.getColumnCount();
                JSONObject obj = new JSONObject();
                for (int i = 1; i <= numColumns; i++) {
                    String column_name = rsmd.getColumnLabel(i);
                    obj.put(column_name, rs.getObject(column_name));
                }
                json.add(obj);
            }
            model.addAttribute("org_data", json.toString());
        } catch (Exception e) {
            Logger.log(this, e.getMessage(), 2);
        }
        return "views/organizationView";
    }
}
