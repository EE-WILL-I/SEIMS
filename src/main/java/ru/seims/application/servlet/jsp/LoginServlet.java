package ru.seims.application.servlet.jsp;
import org.apache.tomcat.websocket.AuthenticationException;
import ru.seims.application.security.authorization.AuthenticationService;
import ru.seims.database.entitiy.User;
import ru.seims.utils.logging.Logger;
import ru.seims.application.security.authorization.AuthorizationService;

import ru.seims.localization.LocalizationManager;
import  ru.seims.database.proccessing.SQLExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;

@Controller
public class LoginServlet {
    @GetMapping("/login")
    public String showForm() {
        return "views/authorization";
    }

    @PostMapping("/login")
    public String login(HttpServletResponse response,
                        HttpServletRequest request,
                        RedirectAttributes attributes,
                        @RequestParam(value = "login", defaultValue = "") String login,
                        @RequestParam(value = "passwd", defaultValue = "") String passwd) {
        Logger.log(this, "Logging as: " + login);
        try {
            User user = AuthenticationService.authenticateUser(login, passwd);
            LocalizationManager.setUserLocale(user);
            AuthorizationService.addAuthorizationToken(response, user);
            Logger.log(this, "Login successful", 3);
            return "redirect:/";
        } catch (AuthenticationException e) {
            attributes.addFlashAttribute("failed", "true");
            Logger.log(this, e.getMessage(), 3);
        }
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response) throws SQLException {
        AuthorizationService.removeAuthorizationToken(response);
        return "redirect:/login";
    }
}
