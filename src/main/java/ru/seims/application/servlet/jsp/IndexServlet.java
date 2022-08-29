package ru.seims.application.servlet.jsp;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.seims.database.entitiy.User;

import javax.servlet.http.HttpServletRequest;

@Controller
public class IndexServlet {
    @GetMapping({"/", "/index"})
    public String index() {
        return "redirect:/monitoring";
    }

    @GetMapping("/monitoring")
    public String getMonitoring() {
        return "views/monitoring";
    }

    @GetMapping("/test")
    public String getTest() { return "views/test"; }
}
