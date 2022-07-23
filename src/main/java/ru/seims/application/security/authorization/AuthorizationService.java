package ru.seims.application.security.authorization;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.security.core.userdetails.UserDetails;
import ru.seims.application.security.JwtRequestFilter;
import ru.seims.database.entitiy.User;
import ru.seims.utils.logging.Logger;
import ru.seims.localization.LocalizationManager;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class AuthorizationService {
    public final String JWT_HOLDER_COOKIE_NAME = "seims-token";
    public final long acceptLeeway = 3000;
    private static AnnotationConfigApplicationContext authorizationServiceContext;
    private final Algorithm algorithm;

    AuthorizationService() {
        boolean DEBUG_MODE = java.lang.management.ManagementFactory.getRuntimeMXBean().
                getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;
        String SECRET_HMAC_KEY = DEBUG_MODE ? "DEVELOPMENT" : String.valueOf(new Random().nextLong());
        algorithm = Algorithm.HMAC256(SECRET_HMAC_KEY);
    }

    public static AuthorizationService getInstance() {
        if(authorizationServiceContext == null)
            authorizationServiceContext = new AnnotationConfigApplicationContext(AuthorizationService.class);
        return authorizationServiceContext.getBean(AuthorizationService.class);
    }

    public void logInByCredentials(HttpServletResponse response, String login, String passwd) throws AuthenticationException {
        User user = AuthenticationService.authenticateUser(login, passwd);
        LocalizationManager.setUserLocale(user);
        addAuthorizationToken(response, user);
    }

    public String registerToken(User user) {
        String token;
        try {
            token = JWT.create()
                    .withIssuer("SEIMS")
                    .withClaim("userId", user.getId())
                    .withClaim("role", user.getRoleId())
                    .withClaim("login", user.getLogin())
                    .withClaim("fname", user.getFirstName())
                    .withClaim("lname", user.getLastName())
                    .withClaim("pname", user.getPatronymic())
                    .withClaim("params", user.getParamString())
                    .sign(algorithm);
        } catch (JWTCreationException exception){
            throw new IllegalArgumentException("Cannot create token for current user");
        }
        return token;
    }

    public DecodedJWT verifyToken(String token) throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer("SEIMS")
                .acceptLeeway(acceptLeeway)
                .build();
        return verifier.verify(token);
    }

    public UserDetails checkAuthorizationToken(HttpServletRequest request) {
        Logger.log(AuthorizationService.class, "Check auth for request from " + request.getRemoteAddr(), 4);
        try {
            Cookie jwtCookie = Arrays.stream(request.getCookies()).filter(cookie ->
                    cookie.getName().equals(JWT_HOLDER_COOKIE_NAME)).findFirst().orElse(null);
            String token = jwtCookie.getValue();
            User user = new User(verifyToken(token));
            request.getSession().setAttribute("user", user);
            LocalizationManager.setUserLocale(user);
            return user;
        } catch (Exception e) {
            Logger.log(AuthorizationService.class, "Auth failed. " + e.getMessage(), 4);
            return null;
        }
    }

    public void addAuthorizationToken(HttpServletResponse response, User user) {
        String token = registerToken(user);
        Cookie jwtCookie = new Cookie(JWT_HOLDER_COOKIE_NAME, token);
        jwtCookie.setSecure(true);
        jwtCookie.setHttpOnly(true);
        response.addCookie(jwtCookie);
    }

    public void removeAuthorizationToken(HttpServletRequest request, HttpServletResponse response) throws SQLException {
        Cookie jwtCookie = new Cookie(JWT_HOLDER_COOKIE_NAME, null);
        jwtCookie.setSecure(true);
        jwtCookie.setMaxAge(0);
        response.addCookie(jwtCookie);
        request.getSession().removeAttribute("user");
        Logger.log(AuthorizationService.class, "Auth terminated for " + request.getRemoteAddr(), 4);
    }
}
