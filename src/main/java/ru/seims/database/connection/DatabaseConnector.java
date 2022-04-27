package ru.seims.database.connection;

import ru.seims.utils.logging.Logger;
import ru.seims.utils.properties.PropertyReader;
import ru.seims.utils.properties.PropertyType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;

public class DatabaseConnector {
    /**
     * JDBC Driver and database url
     */
    public static Connection connection;
    public static Statement statement;
    private static final String JDBC_DRIVER = PropertyReader.getPropertyValue(PropertyType.DATABASE, "datasource.driver-class-name");
    private static final String DATABASE_URL = PropertyReader.getPropertyValue(PropertyType.DATABASE, "datasource.url");
    private static final String DATABASE_SCHEMA = PropertyReader.getPropertyValue(PropertyType.DATABASE, "datasource.schema");
    private static final String CONNECTION_ARGS = PropertyReader.getPropertyValue(PropertyType.DATABASE, "connection.args");

    /**
     * User and Password
     */
    private static final String USER = PropertyReader.getPropertyValue(PropertyType.DATABASE, "datasource.username");
    private static final String PASSWORD = PropertyReader.getPropertyValue(PropertyType.DATABASE, "datasource.password");

    public static boolean setConnection(String... args) throws ClassNotFoundException, SQLException {
        if (PropertyReader.getPropertyValue(PropertyType.SERVER, "app.disableDatabase").toLowerCase(Locale.ROOT).equals("true"))
            return true;
        Logger.log(DatabaseConnector.class, "Registering JDBC driver", 1);
        Class.forName(JDBC_DRIVER);
        Logger.log(DatabaseConnector.class, "Creating database connection", 1);

        try {
            if (args.length == 3)
                connection = DriverManager.getConnection(args[0], args[1], args[2]);
            else
                connection = DriverManager.getConnection(DATABASE_URL + DATABASE_SCHEMA + CONNECTION_ARGS, USER, PASSWORD);
        } catch (SQLException e) {
            Logger.log(DatabaseConnector.class, "Unable connect to database", 2);
            e.printStackTrace();
            return false;
        }

        statement = connection.createStatement();
        return true;
    }

    public static Connection getConnection() {
        if(connection == null) {
            try {
                setConnection();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return connection;
    }

    public static void closeConnection() throws SQLException {
        Logger.log(DatabaseConnector.class,"Closing connection and releasing resources", 1);
        statement.close();
        connection.close();
    }

    @Override
    public void finalize() throws SQLException {
        closeConnection();
    }
}
