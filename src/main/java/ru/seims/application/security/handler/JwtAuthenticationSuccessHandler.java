package ru.seims.application.security.handler;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import ru.seims.application.security.service.AuthorizationService;
import ru.seims.database.UserService;
import ru.seims.database.entitiy.User;
import ru.seims.utils.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;

public class JwtAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final UserService userService;

    public JwtAuthenticationSuccessHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Logger.log(this, "Registering user token for " + request.getRemoteAddr(), 4);
        String username = ((UserDetails)authentication.getPrincipal()).getUsername();
        User user = (User) userService.loadUserByUsername(username);
        AuthorizationService.getInstance().addAuthorizationToken(response, user);
        response.sendRedirect("/");
    }
}
