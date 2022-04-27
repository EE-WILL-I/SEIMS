package ru.seims.application.servlet.jsp;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MapServlet {
    @GetMapping("/map")
    public String doGet() {
        return "views/mapView";
    }
}
