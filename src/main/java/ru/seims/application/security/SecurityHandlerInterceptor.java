package ru.seims.application.security;

import ru.seims.application.security.authorization.AuthenticationService;
import ru.seims.database.entitiy.User;
import ru.seims.utils.logging.Logger;
import ru.seims.utils.properties.PropertyReader;
import ru.seims.utils.properties.PropertyType;
import ru.seims.application.security.authorization.AuthorizationService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

public class SecurityHandlerInterceptor extends HandlerInterceptorAdapter {
    private final boolean secure = !PropertyReader.getPropertyValue(PropertyType.SERVER, "app.disableSecurity")
            .toLowerCase(Locale.ROOT).equals("true");
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        Logger.log(this, "Request for URL: " + request.getRequestURL(), 4);
        if(!secure) {
            if(!AuthorizationService.checkAuthorizationToken(request))
                request.getSession().setAttribute("user", new User("0", "0", "dev", "name", "family", "", "ru.RU"));
            return true;
        }
        if (request.getServletPath().contains("/login") || request.getServletPath().contains("/open-api"))
            return true;
        if (AuthorizationService.checkAuthorizationToken(request))
            return true;
        String auth = request.getHeader("Authorization");
        if(auth != null && !auth.isEmpty()) {
            String credentials = auth.substring(6);
            if (AuthenticationService.authenticateServiceUser(credentials))
                return true;
        }
        Logger.log(this, "Access denied for unauthorized user. IP:" + request.getRemoteAddr(), 2);
        response.sendRedirect("/login");
        return false;
    }
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {

    }
}
