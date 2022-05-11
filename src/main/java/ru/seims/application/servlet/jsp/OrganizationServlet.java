package ru.seims.application.servlet.jsp;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.seims.database.entitiy.DataTable;
import ru.seims.database.proccessing.SQLExecutor;
import ru.seims.utils.FileResourcesUtils;
import ru.seims.utils.logging.Logger;

import java.io.File;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;

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
            ArrayList<DataTable> tablesData = new ArrayList<>();
            JSONArray dataArray = new JSONArray();
            SQLExecutor executor = SQLExecutor.getInstance();
            dataArray.add(DatabaseServlet.convertResultSetToJSON(
                    executor.executeSelect(executor.loadSQLResource("get_org_info.sql"), id)
            ));
            int orgId = (int) ((JSONObject)dataArray.get(0)).get("id");
            dataArray.add(DatabaseServlet.convertResultSetToJSONArray(
                    executor.executeSelectSimple("images", "id", "id_org_web_info = " + orgId)
            ));
            if(!dataArray.isEmpty()) {
                File queryDir = new File(FileResourcesUtils.RESOURCE_PATH + executor.SQL_RESOURCE_PATH + "/doo_VR");
                for(final File query : queryDir.listFiles()) {
                    if(query.isFile()) {
                        ResultSet tableData = executor.executeSelect(
                                executor.loadSQLResource("doo_VR/" + query.getName()), id
                        );
                        String tableSysName = tableData.getMetaData().getTableName(1);
                        String tableDisplayName = tableSysName;
                        ResultSet rs = executor.executeSelect(
                                executor.loadSQLResource("get_doo_vr_display_name.sql"),
                                tableData.getMetaData().getTableName(1));
                        if(rs.next())
                            tableDisplayName = rs.getString(1);
                        tablesData.add(new DataTable(tableDisplayName, tableSysName).populateTable(tableData));
                    }
                }
                model.addAttribute("tables", tablesData);
                model.addAttribute("org_id", id);
            }
            model.addAttribute("org_data", dataArray.toString());
        } catch (Exception e) {
            Logger.log(this, e.getMessage(), 2);
        }
        return "views/organizationView";
    }
}
