package ru.seims.application.security.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.ECDSAKeyProvider;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UserDetails;
import ru.seims.application.security.JwtKeyProvider;
import ru.seims.database.entitiy.Role;
import ru.seims.database.entitiy.User;
import ru.seims.utils.logging.Logger;
import ru.seims.localization.LocalizationManager;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
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
        algorithm = Algorithm.ECDSA256(new JwtKeyProvider());
    }

    public static AuthorizationService getInstance() {
        if(authorizationServiceContext == null)
            authorizationServiceContext = new AnnotationConfigApplicationContext(AuthorizationService.class);
        return authorizationServiceContext.getBean(AuthorizationService.class);
    }

    public String registerToken(User user) {
        String token;
        Date expDate =  new Date(new Date().getTime() + (1000 * 60 * 60 * 24));
        Role userRole = user.getPrimaryAuthority();
        if(userRole == null)
            userRole = new Role(0, "undefined");
        try {
            token = JWT.create()
                    .withIssuer("SEIMS")
                    .withClaim("userId", user.getId())
                    .withClaim("roleId", userRole.getId())
                    .withClaim("role",   userRole.getName())
                    .withClaim("login",  user.getUsername())
                    .withClaim("fname",  user.getFirstName())
                    .withClaim("lname",  user.getLastName())
                    .withClaim("pname",  user.getPatronymic())
                    .withClaim("params", user.getParamString())
                    .withExpiresAt(expDate)
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
        DecodedJWT jwt = verifier.verify(token);
        if(jwt == null)
            throw new JWTVerificationException("Cannot validate token");
        return jwt;
    }

    public UserDetails checkAuthorizationToken(HttpServletRequest request) {
        //Logger.log(AuthorizationService.class, "Check auth for request from " + request.getRemoteAddr(), 4);
        try {
            String token = null;
            //Cookie jwtCookie = Arrays.stream(request.getCookies()).filter(cookie ->
              //      cookie.getName().equals(JWT_HOLDER_COOKIE_NAME)).findFirst().orElse(null);
            //if (jwtCookie != null) {
              //  token = jwtCookie.getValue();
            //} else {
                String auth = request.getHeader(HttpHeaders.AUTHORIZATION);
                if (!auth.isEmpty() && auth.startsWith("Bearer "))
                    token = auth.substring(7);
           // }
            if(token == null || token.isEmpty())
                return null;
            User user = new User(verifyToken(token));
            if (request.getSession().getAttribute("user") == null) {
                request.getSession().setAttribute("user", user);
                LocalizationManager.setUserLocale(user);
            }
            return user;
        } catch (Exception e) {
            Logger.log(AuthorizationService.class, "Auth failed. " + e.getMessage(), 4);
            return null;
        }
    }

    public void addAuthorizationToken(HttpServletResponse response, User user) {
        String token = registerToken(user);
        response.addHeader("Authorization", "Bearer " + token);
        Cookie jwtCookie = new Cookie(JWT_HOLDER_COOKIE_NAME, token);
        jwtCookie.setSecure(true);
        jwtCookie.setHttpOnly(true);
        //response.addCookie(jwtCookie);
        Logger.log(this, "Token registered for user " + user.getUsername(), 4);
    }

    public void removeAuthorizationToken(HttpServletRequest request, HttpServletResponse response) throws SQLException {
        Cookie jwtCookie = new Cookie(JWT_HOLDER_COOKIE_NAME, null);
        jwtCookie.setSecure(true);
        jwtCookie.setMaxAge(0);
        response.addCookie(jwtCookie);
        request.getSession().removeAttribute("user");
        Logger.log(this, "Auth terminated for " + request.getRemoteAddr(), 4);
    }
}
