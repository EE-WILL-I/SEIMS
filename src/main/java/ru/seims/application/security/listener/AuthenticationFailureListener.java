package ru.seims.application.security.listener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import ru.seims.application.security.handler.SecurityHandlerInterceptor;

import javax.servlet.http.HttpServletRequest;

@Component
public class AuthenticationFailureListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    private static HttpServletRequest getRequest() {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        if (attrs == null) {
            return null;
        }

        return attrs.getRequest();
    }

    @Autowired
    private SecurityHandlerInterceptor handlerInterceptor;

    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent e) {
        HttpServletRequest request = getRequest();
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            handlerInterceptor.handleAuthenticationFailure(request.getRemoteAddr());
        } else {
            handlerInterceptor.handleAuthenticationFailure(xfHeader.split(",")[0]);
        }
    }
}