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
            JSONArray dataArray = new JSONArray();
            SQLExecutor executor = SQLExecutor.getInstance();
            dataArray.add(DatabaseServlet.convertResultSetToJSON(
                    executor.executeSelect(executor.loadSQLResource("get_org_info.sql"), id)
            ));
            dataArray.add(DatabaseServlet.convertResultSetToJSONArray(
                    executor.executeSelect(executor.loadSQLResource("doo_VR7.sql"), id)
            ));
            model.addAttribute("org_data", dataArray.toString());
        } catch (Exception e) {
            Logger.log(this, e.getMessage(), 2);
        }
        return "views/organizationView";
    }
}
