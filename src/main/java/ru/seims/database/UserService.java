package ru.seims.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import ru.seims.application.security.handler.SecurityHandlerInterceptor;
import ru.seims.database.entitiy.User;
import ru.seims.database.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SecurityHandlerInterceptor handlerInterceptor;

    private static String  getRequestAddr() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        String xfHeader = attrs.getRequest().getHeader("X-Forwarded-For");
        if (xfHeader == null) {
           return attrs.getRequest().getRemoteAddr();
        } else {
            return xfHeader.split(",")[0];
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if(!handlerInterceptor.checkAddr(getRequestAddr())) throw new UsernameNotFoundException("IP blocked");
        User user = userRepository.findByUsername(username);
        if(user == null) {
            throw new UsernameNotFoundException(username);
        }
        return user;
    }
}
