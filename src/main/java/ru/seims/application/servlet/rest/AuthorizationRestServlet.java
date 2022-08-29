package ru.seims.application.servlet.rest;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.seims.application.security.service.AuthenticationService;
import ru.seims.application.security.service.AuthorizationService;
import ru.seims.database.entitiy.User;
import ru.seims.localization.LocalizationManager;
import ru.seims.utils.json.JSONBuilder;
import ru.seims.utils.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;

@RestController
public class AuthorizationRestServlet {
    @PostMapping("/api/login")
    public static String logIn(HttpServletRequest request, HttpServletResponse response) {
        //Logger.log(AuthorizationRestServlet.class, "Logging as: " + login);
        try {
            //logInByCredentials(response, login, passwd);
            User userDetails = (User) AuthorizationService.getInstance().checkAuthorizationToken(request);
            AuthorizationService.getInstance().addAuthorizationToken(response, userDetails);
            Logger.log(AuthorizationRestServlet.class, "Login successful", 3);
            return new JSONBuilder().addAVP("result", "ok").getString();
        } catch (AuthenticationException e) {
            Logger.log(AuthorizationRestServlet.class, e.getMessage(), 3);
            return new JSONBuilder().addAVP("result", "failed")
                    .addAVP("details", e.getMessage()).getString();
        }
    }

    public static void logInByCredentials(HttpServletResponse response, String login, String passwd) throws AuthenticationException {
        User user = AuthenticationService.getInstance().authenticateUser(login, passwd);
        LocalizationManager.setUserLocale(user);
        AuthorizationService.getInstance().addAuthorizationToken(response, user);
    }

    @PostMapping("/api/logout")
    public static String logOut(HttpServletRequest request, HttpServletResponse response) {
        try {
            AuthorizationService.getInstance().removeAuthorizationToken(request, response);
            return new JSONBuilder().addAVP("result", "ok").getString();
        } catch (SQLException e) {
            return new JSONBuilder().addAVP("result", "error").getString();
        }
    }

    @PostMapping("/api/registration")
    public String signIn(HttpServletRequest request) {
        String roleId = request.getParameter("role");
        String login = request.getParameter("login");
        String passwd = request.getParameter("passwd");
        String fname = request.getParameter("fname");
        String lname = request.getParameter("lname");
        String pname = request.getParameter("pname");
        User user = new User(0L, roleId, login, fname, lname, pname,"ru.RU", true);
        try {
            return new JSONBuilder().addAVP("id", AuthenticationService.getInstance().signInUser(user, passwd)).getString();
        } catch (SQLException e) {
            return new JSONBuilder().addAVP("error", e.getMessage()).getString();
        }

    }

    @GetMapping("/api/verify")
    public String verifyJWT(HttpServletRequest request) {
        return String.valueOf(AuthorizationService.getInstance().checkAuthorizationToken(request));
    }
}
