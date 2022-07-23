package ru.seims.application.servlet.jsp;
import org.apache.tomcat.websocket.AuthenticationException;
import ru.seims.application.security.authorization.AuthorizationService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.seims.application.servlet.rest.AuthorizationRestServlet;

import javax.servlet.http.HttpServletResponse;

@Controller
public class LoginServlet {
    @GetMapping("/login")
    public String showForm() {
        return "views/authorization";
    }

    @PostMapping("/login")
    public String login(HttpServletResponse response,
                        RedirectAttributes attributes,
                        @RequestParam(value = "login", defaultValue = "") String login,
                        @RequestParam(value = "passwd", defaultValue = "") String passwd) {
        try {
            AuthorizationRestServlet.logInByCredentials(response, login, passwd);
            return "redirect:/";
        } catch (AuthenticationException e) {
            attributes.addFlashAttribute("failed", "true");
            return "redirect:/login";
        }
    }
}
