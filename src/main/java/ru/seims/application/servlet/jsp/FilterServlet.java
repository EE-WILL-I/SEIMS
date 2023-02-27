package ru.seims.application.servlet.jsp;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class FilterServlet {
    public static final String filter = "filter";

    @GetMapping(filter)
    public String doGet(Model model, @RequestParam(required = false, name = "staticOrgId") String staticOrgId) {
        if(staticOrgId != null && !staticOrgId.isEmpty()) {
            model.addAttribute("staticId", staticOrgId);
        }
        return "views/filterView";
    }
}
