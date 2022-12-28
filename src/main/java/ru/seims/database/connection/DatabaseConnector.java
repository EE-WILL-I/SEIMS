package ru.seims.database.connection;

import com.mysql.cj.exceptions.ConnectionIsClosedException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.seims.application.context.GlobalApplicationContext;
import ru.seims.utils.logging.Logger;
import ru.seims.utils.properties.PropertyReader;
import ru.seims.utils.properties.PropertyType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;

public class DatabaseConnector {
    private Connection connection;
    private Statement statement;
    private boolean isConnectionSet = false;
    private static AnnotationConfigApplicationContext databaseConnectorContext;
    private final String JDBC_DRIVER = PropertyReader.getPropertyValue(PropertyType.DATABASE, "datasource.driver-class-name");
    private final String DATABASE_URL = PropertyReader.getPropertyValue(PropertyType.DATABASE, "datasource.url");
    private final String DATABASE_SCHEMA = PropertyReader.getPropertyValue(PropertyType.DATABASE, "datasource.schema");
    private final String CONNECTION_ARGS = PropertyReader.getPropertyValue(PropertyType.DATABASE, "connection.args");
    private final String USER = PropertyReader.getPropertyValue(PropertyType.DATABASE, "datasource.username");
    private final String PASSWORD = PropertyReader.getPropertyValue(PropertyType.DATABASE, "datasource.password");

    public static DatabaseConnector getInstance() {
        if(databaseConnectorContext == null)
            databaseConnectorContext = new AnnotationConfigApplicationContext(DatabaseConnector.class);
        return databaseConnectorContext.getBean(DatabaseConnector.class);
    }

    public Connection getConnection() throws ConnectionIsClosedException {
        if(!isConnectionSet) {
            try {
                isConnectionSet = setConnection();
            } catch (Exception e) {
                Logger.log(DatabaseConnector.class, e.getMessage(), 2);
                throw new ConnectionIsClosedException(e.getMessage());
            }
        }
        return connection;
    }

    public void closeConnection() throws SQLException {
        if(isConnectionSet) {
            Logger.log(DatabaseConnector.class, "Closing connection and releasing resources", 1);
            statement.close();
            connection.close();
            isConnectionSet = false;
        }
    }

    public boolean setConnection(String... args) throws ClassNotFoundException {
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
            Logger.log(DatabaseConnector.class, "Connecting to " + DATABASE_URL + DATABASE_SCHEMA + CONNECTION_ARGS, 1);
            statement = connection.createStatement();
            Logger.log(DatabaseConnector.class, "Database connection successfully created", 1);
            GlobalApplicationContext.setParameter("connected_to_db", "true");
            return true;
        } catch (SQLException e) {
            Logger.log(DatabaseConnector.class, "Unable to connect to database. " + e.getMessage(), 2);
            GlobalApplicationContext.setParameter("connected_to_db", "false");
            return false;
        }
    }

    @Override
    protected void finalize() throws SQLException {
        //closeConnection();
    }
}
