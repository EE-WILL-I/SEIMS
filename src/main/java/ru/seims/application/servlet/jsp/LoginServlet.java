package ru.seims.application.servlet.jsp;

import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.seims.application.security.service.AuthorizationService;
import ru.seims.application.servlet.rest.AuthorizationRestServlet;
import ru.seims.database.entitiy.User;
import ru.seims.utils.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class LoginServlet {
    @GetMapping("/login")
    public String showForm(HttpServletRequest request, @RequestParam(name = "error", required = false) String error) {
        if(error != null && !error.isEmpty())
            request.setAttribute("error", error);
        return "views/authorization";
    }

    @GetMapping("/error")
    public String error(HttpServletRequest request) {
        Logger.log(this, "Exception occurred by IP: " + request.getRemoteAddr(), 3);
        return "redirect:/login?failed=true";
    }
}
