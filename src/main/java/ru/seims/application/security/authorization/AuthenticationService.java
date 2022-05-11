package ru.seims.application.security.authorization;

import org.apache.tomcat.websocket.AuthenticationException;
import ru.seims.database.entitiy.User;
import ru.seims.database.proccessing.SQLExecutor;
import ru.seims.utils.logging.Logger;
import ru.seims.utils.properties.PropertyReader;
import ru.seims.utils.properties.PropertyType;
import org.springframework.http.HttpHeaders;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;

public class AuthenticationService {
    private static String encodedUser;
    private static final SQLExecutor executor = SQLExecutor.getInstance();

    public static User authenticateUser(String login, String password) throws AuthenticationException {
        Logger.log(AuthenticationService.class, "Checking credentials for login: : " + login, 3);
        ResultSet resultSet = executor.executeSelect(executor.loadSQLResource("get_user.sql"),
                "*", login, password);
        try {
            if(resultSet.next()) {
                String userId = String.valueOf(resultSet.getInt("id"));
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
        }
    }

    public static String signInUser(User user, String pass) throws SQLException {
        executor.executeInsert(executor.loadSQLResource("insert_users.sql"), "users",
                user.getRoleId(), user.getLogin(), pass, user.getFirstName(), user.getLastName(),
                user.getPatronymic(), user.getParamString());
        return executor.executeSelectSimple("users", "id",
                String.format("login like '%s'", user.getLogin())).getString("id");
    }

    public static boolean loadConfiguredServiceUserCredentials() {
        String user = PropertyReader.getPropertyValue(PropertyType.SERVER, "servauth.user");
        String pass = PropertyReader.getPropertyValue(PropertyType.SERVER, "servauth.pass");
        if(user.isEmpty() || pass.isEmpty())
            return false;
        encodedUser = encodeCredentials(user, pass);
        return true;
    }

    public static void loadDefaultServiceUserCredentials() {
        encodedUser = encodeCredentials("wsainternaluser", "WSAINTERNALUER01");
    }

    public static String encodeCredentials(String user, String pass) {
        return  Base64.getEncoder().encodeToString((user + ":" + pass).getBytes());
    }

    public static String getDefaultUserCredentialsEncoded() {
        return encodedUser;
    }

    public static boolean authenticateServiceUser(String credentials) {
        return encodedUser.equals(credentials);
    }

    public static HttpHeaders getHeaders() {
        return new HttpHeaders() {{
            String authHeader = "Basic " + encodedUser;
            set("Authorization", authHeader);
        }};
    }
}
