package ru.seims.application.security.service;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.security.core.AuthenticationException;
import ru.seims.database.UserService;
import ru.seims.database.entitiy.User;
import ru.seims.database.proccessing.SQLExecutor;
import ru.seims.utils.logging.Logger;
import ru.seims.utils.properties.PropertyReader;
import ru.seims.utils.properties.PropertyType;
import org.springframework.http.HttpHeaders;

import java.sql.SQLException;
import java.util.Base64;

public class AuthenticationService {
    private String encodedUser;
    private final SQLExecutor executor = SQLExecutor.getInstance();
    private static AnnotationConfigApplicationContext authenticationServiceContext;
    private  UserService userService;

    public static AuthenticationService getInstance() {
        if(authenticationServiceContext == null)
            authenticationServiceContext = new AnnotationConfigApplicationContext(AuthenticationService.class);
        return authenticationServiceContext.getBean(AuthenticationService.class);
    }

    public User authenticateUser(String login, String passwd) throws AuthenticationException {
        Logger.log(AuthenticationService.class, "Checking credentials for username: : " + login, 3);
        /*ResultSet resultSet = executor.executeSelect(executor.loadSQLResource("get_user.sql"),
                "*", login, password);
        try {
            if(resultSet.next()) {
                int userId = resultSet.getInt("id");
                String roleId = String.valueOf(resultSet.getInt("roleId"));
                String fname = resultSet.getString("fname");
                String lname = resultSet.getString("lname");
                String pname = resultSet.getString("pname");
                String params = resultSet.getString("params");
                return new User(userId, roleId, login, fname, lname, pname, params);
            }
            throw new AuthenticationException("User with given credentials not found");
        } catch (Exception e) {
            throw new AuthenticationException("Error while getting user from DB");
        }*/
        if(userService == null)
            userService = new AnnotationConfigApplicationContext(UserService.class).getBean(UserService.class);
        User user = (User) userService.loadUserByUsername(login);
        String username = user.getUsername();
        String password = user.getPassword();

        if(login.equals(username) && passwd.equals(password))
            return user;
        else
            return null;
    }

    public String signInUser(User user, String pass) throws SQLException {
        executor.executeInsert(executor.loadSQLResource("insert_users.sql"), "users",
                user.getRoleId(), user.getUsername(), pass, user.getFirstName(), user.getLastName(),
                user.getPatronymic(), user.getParamString());
        return executor.executeSelectSimple("users", "id",
                String.format("login like '%s'", user.getUsername())).getString("id");
    }

    public boolean loadConfiguredServiceUserCredentials() {
        String user = PropertyReader.getPropertyValue(PropertyType.SERVER, "servauth.user");
        String pass = PropertyReader.getPropertyValue(PropertyType.SERVER, "servauth.pass");
        if(user.isEmpty() || pass.isEmpty())
            return false;
        encodedUser = encodeCredentials(user, pass);
        return true;
    }

    public void loadDefaultServiceUserCredentials() {
        encodedUser = encodeCredentials("wsainternaluser", "WSAINTERNALUER01");
    }

    public String encodeCredentials(String user, String pass) {
        return  Base64.getEncoder().encodeToString((user + ":" + pass).getBytes());
    }

    public String getDefaultUserCredentialsEncoded() {
        return encodedUser;
    }

    public boolean authenticateServiceUser(String credentials) {
        return encodedUser.equals(credentials);
    }

    public HttpHeaders getHeaders() {
        return new HttpHeaders() {{
            String authHeader = "Basic " + encodedUser;
            set("Authorization", authHeader);
        }};
    }
}
