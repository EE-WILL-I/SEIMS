package ru.seims.application.servlet.rest;

import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.seims.application.security.authorization.AuthenticationService;
import ru.seims.application.security.authorization.AuthorizationService;
import ru.seims.database.entitiy.User;
import ru.seims.utils.json.JSONBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;

@RestController
public class AuthorizationServlet {
    @GetMapping("/api/login")
    public User logIn(HttpServletRequest request, HttpServletResponse response) {
        String login = request.getParameter("login");
        String passwd = request.getParameter("passwd");
        try {
            User user = AuthenticationService.authenticateUser(login, passwd);
            AuthorizationService.addAuthorizationToken(response, user);
            return user;
        } catch (AuthenticationException e) {
            return null;
        }
    }

    @GetMapping("/api/logout")
    public String logout(HttpServletResponse response) {
        try {
            AuthorizationService.removeAuthorizationToken(response);
            return new JSONBuilder().addAVP("result", "ok").getString();
        } catch (SQLException e) {
            return new JSONBuilder().addAVP("result", "error").getString();
        }
    }

    @PostMapping("/api/signIn")
    public String signIn(HttpServletRequest request) {
        String roleId = request.getParameter("role");
        String login = request.getParameter("login");
        String passwd = request.getParameter("passwd");
        String fname = request.getParameter("fname");
        String lname = request.getParameter("lname");
        String pname = request.getParameter("pname");
        User user = new User(null, roleId, login, fname, lname, pname,"ru.RU");
        try {
            return new JSONBuilder().addAVP("id", AuthenticationService.signInUser(user, passwd)).getString();
        } catch (SQLException e) {
            return new JSONBuilder().addAVP("error", e.getMessage()).getString();
        }

    }

    @GetMapping("/api/verify")
    public String verifyJWT(HttpServletRequest request) {
        return String.valueOf(AuthorizationService.checkAuthorizationToken(request));
    }
}
