package ru.seims.application.security.handler;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import ru.seims.application.context.GlobalApplicationContext;
import ru.seims.database.entitiy.User;
import ru.seims.utils.logging.Logger;
import ru.seims.utils.properties.PropertyReader;
import ru.seims.utils.properties.PropertyType;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SecurityHandlerInterceptor extends HandlerInterceptorAdapter {
    public final int MAX_LOGIN_ATTEMPT = 6;
    public final long BLOCK_TIME_MILLS = (1000 * 60 * 60);
    private final Map<String, Integer> failedLoginAttemptCache = new HashMap<>();
    private final Map<String, Long> blockedAddrCache = new HashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = "undefined";
        if(user instanceof UserDetails) {
            username = ((UserDetails) user).getUsername();
            request.setAttribute("username", username);
            request.setAttribute("authorized", "true");
        }
        Logger.log(this, "Request from " + request.getRemoteAddr() + "(" + username + ")" + " to URL: " + request.getRequestURL(), 4);
        return true;
    }
    
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) { }

    public void handleAuthenticationFailure(String addr) {
        Integer attempts = failedLoginAttemptCache.get(addr);
        if(attempts != null) {
            attempts++;
            if(attempts >= MAX_LOGIN_ATTEMPT) {
                blockedAddrCache.put(addr, new Date().getTime());
                failedLoginAttemptCache.remove(addr);
            } else {
                failedLoginAttemptCache.put(addr, attempts);
            }
        } else {
            failedLoginAttemptCache.put(addr, 1);
        }
    }

    public boolean checkAddr(String addr) {
        if(blockedAddrCache.containsKey(addr)) {
            long time = new Date().getTime();
            if(time - blockedAddrCache.get(addr) > BLOCK_TIME_MILLS) {
                blockedAddrCache.remove(addr);
                return true;
            }
            Logger.log(this, "Address " + addr + " is blocked by system: too many login attempts", 4);
            return false;
        }
        return true;
    }

    public void handleAuthenticationSuccess(String addr) {
        failedLoginAttemptCache.remove(addr);
    }
}
