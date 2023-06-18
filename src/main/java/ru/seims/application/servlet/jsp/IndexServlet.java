package ru.seims.application.servlet.jsp;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.seims.application.configuration.WebSecurityConfiguration;
import ru.seims.database.entitiy.DataTable;
import ru.seims.database.entitiy.User;
import ru.seims.database.proccessing.SQLExecutor;
import ru.seims.utils.FileResourcesUtils;
import ru.seims.utils.logging.Logger;
import ru.seims.utils.properties.PropertyReader;
import ru.seims.utils.properties.PropertyType;

import javax.servlet.http.HttpServletRequest;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@Controller
public class IndexServlet {
    public static final String index = WebSecurityConfiguration.viewerPattern + "monitoring";
    @GetMapping({"/", "/index","/monitoring","/main","/home"})
    public String index() {
        return "redirect:"+index;
    }

    @GetMapping(index)
    public String getMonitoring(Model model) {
        SQLExecutor executor = SQLExecutor.getInstance();
        ArrayList<ResultSet> dataSetList = new ArrayList<>(6);
        dataSetList.add(executor.executeSelect(executor.loadSqlResource("statistics_scripts/get_constants.sql")));
        dataSetList.add(executor.executeSelect(executor.loadSqlResource("statistics_scripts/get_org_district_distribution.sql")));
        dataSetList.add(executor.executeSelect(executor.loadSqlResource("statistics_scripts/get_student_distribution.sql")));
        dataSetList.add(executor.executeSelect(executor.loadSqlResource("statistics_scripts/get_org_type_ratio.sql")));
        dataSetList.add(executor.executeSelect(executor.loadSqlResource("statistics_scripts/get_class_ratio.sql")));
        dataSetList.add(executor.executeSelect(executor.loadSqlResource("statistics_scripts/get_class_ratio_for_disabled.sql")));
        dataSetList.add(executor.executeSelect(executor.loadSqlResource("statistics_scripts/get_graduated_students.sql")));

        ArrayList<DataTable> dataTables = new ArrayList<>(dataSetList.size());
        try {
            for (ResultSet dataSet : dataSetList) {
                DataTable table = new DataTable();
                table.populate(dataSet);
                dataTables.add(table);
                dataSet.close();
            }
        } catch (SQLException e) {
            Logger.log(this, e.getMessage(), 2);
        }

        model.addAttribute("tables", dataTables);
        return "views/monitoring";
    }
}
