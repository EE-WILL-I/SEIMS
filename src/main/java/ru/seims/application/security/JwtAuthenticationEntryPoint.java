package ru.seims.application.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import ru.seims.utils.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        //response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        Logger.log(this, "Access denied for unauthorized user. IP:" + request.getRemoteAddr(), 2);
        response.sendRedirect("/login");
    }
}
