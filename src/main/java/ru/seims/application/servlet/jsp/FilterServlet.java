package ru.seims.application.servlet.jsp;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FilterServlet {
    public static final String filter = "filter";

    @GetMapping(filter)
    public String doGet() { return "views/filterView"; }
}
