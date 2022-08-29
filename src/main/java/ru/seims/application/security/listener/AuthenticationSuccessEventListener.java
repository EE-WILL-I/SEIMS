package ru.seims.application.security.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import ru.seims.application.security.handler.SecurityHandlerInterceptor;

import javax.servlet.http.HttpServletRequest;

@Component
public class AuthenticationSuccessEventListener implements ApplicationListener<AuthenticationSuccessEvent> {

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
    public void onApplicationEvent(final AuthenticationSuccessEvent e) {
        HttpServletRequest request = getRequest();
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            handlerInterceptor.handleAuthenticationSuccess(request.getRemoteAddr());
        } else {
            handlerInterceptor.handleAuthenticationSuccess(xfHeader.split(",")[0]);
        }
    }
}