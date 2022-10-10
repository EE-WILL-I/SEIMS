package ru.seims.application.servlet.jsp;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.seims.application.configuration.WebSecurityConfiguration;
import ru.seims.database.entitiy.User;
import ru.seims.utils.properties.PropertyReader;
import ru.seims.utils.properties.PropertyType;

import javax.servlet.http.HttpServletRequest;

@Controller
public class IndexServlet {
    public static final String index = WebSecurityConfiguration.viewerPattern + "monitoring";
    @GetMapping({"/", "/index","/monitoring","/main","/home"})
    public String index() {
        return "redirect:"+index;
    }

    @GetMapping(index)
    public String getMonitoring() {
        return "views/monitoring";
    }
}
