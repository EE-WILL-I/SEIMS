package ru.seims.application.security.authorization;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import ru.seims.database.entitiy.User;
import ru.seims.utils.logging.Logger;
import ru.seims.localization.LocalizationManager;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Random;

public class AuthorizationService {
    private static final long acceptLeeway = 3000;
    private static final String JWT_HOLDER_COOKIE_NAME = "seims-token";
    private static final String SECRET_HMAC_KEY;
    public static final boolean DEBUG_MODE = java.lang.management.ManagementFactory.getRuntimeMXBean().
            getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;
    static {
       SECRET_HMAC_KEY = DEBUG_MODE ? "DEVELOPMENT" : String.valueOf(new Random().nextLong());
    }

    public static String registerToken(User user) {
        String token;
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET_HMAC_KEY);
            token = JWT.create()
                    .withIssuer("SEIMS")
                    .withClaim("userId", user.getId())
                    .withClaim("role", user.getRoleId())
                    .withClaim("login", user.getLogin())
                    .withClaim("fname", user.getFirstName())
                    .withClaim("lname", user.getLastName())
                    .withClaim("pname", user.getPatronymic())
                    .withClaim("locale", user.getParamString())
                    .sign(algorithm);
        } catch (JWTCreationException exception){
            throw new IllegalArgumentException("Cannot create token for current user");
        }
        return token;
    }

    public static DecodedJWT verifyToken(String token) throws JWTVerificationException {
        Algorithm algorithm = Algorithm.HMAC256(SECRET_HMAC_KEY);
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer("SEIMS")
                .acceptLeeway(acceptLeeway)
                .build();
        return verifier.verify(token);
    }

    public static boolean checkAuthorizationToken(HttpServletRequest request) {
        try {
        Cookie jwtCookie = Arrays.stream(request.getCookies()).filter(cookie ->
                cookie.getName().equals(JWT_HOLDER_COOKIE_NAME)).findFirst().orElse(null);
            String token = jwtCookie.getValue();
            User user = new User(verifyToken(token));
            request.getSession().setAttribute("user", user);
            LocalizationManager.setUserLocale(user);
            return true;
        } catch (Exception e) {
            Logger.log(AuthorizationService.class, e.getMessage(), 3);
            return false;
        }
    }

    public static void addAuthorizationToken(HttpServletResponse response, User user) {
        String token = registerToken(user);
        Cookie jwtCookie = new Cookie(JWT_HOLDER_COOKIE_NAME, token);
        jwtCookie.setSecure(true);
        jwtCookie.setHttpOnly(true);
        response.addCookie(jwtCookie);
    }

    public static void removeAuthorizationToken(HttpServletResponse response) throws SQLException {
        Cookie jwtCookie = new Cookie(JWT_HOLDER_COOKIE_NAME, null);
        jwtCookie.setSecure(true);
        jwtCookie.setMaxAge(0);
        response.addCookie(jwtCookie);
    }
}
