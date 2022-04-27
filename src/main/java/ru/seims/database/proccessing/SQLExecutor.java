package ru.seims.database.proccessing;

import ru.seims.utils.FileResourcesUtils;
import ru.seims.utils.logging.Logger;
import ru.seims.utils.properties.PropertyReader;
import ru.seims.utils.properties.PropertyType;
import ru.seims.application.configurations.SQLExecutorConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class SQLExecutor {
    public final String SQL_RESOURCE_PATH;
    public static boolean useDummy = false;
    private final Connection connection;
    private static AnnotationConfigApplicationContext sqlExecutorContext;
    private String lastLoadedResource;
    private final ArrayList<String> argumentConstants = new ArrayList<>(Arrays.asList("null","default"));

    @Autowired
    public SQLExecutor(Connection connection, String sqlPath) {
        this.connection = connection;
        SQL_RESOURCE_PATH = sqlPath;
        useDummy = PropertyReader.getPropertyValue(PropertyType.DATABASE, "sql.useDummyExecutor").toLowerCase(Locale.ROOT).equals("true");
    }

    public static SQLExecutor getInstance() {
        if(useDummy)
            return SQLExecutorDummy.getInstance();
        if(sqlExecutorContext == null)
            sqlExecutorContext = new AnnotationConfigApplicationContext(SQLExecutorConfiguration.class);
        return sqlExecutorContext.getBean(SQLExecutor.class);
    }

    public Connection getConnection() {
        return connection;
    }

    public PreparedStatement prepareStatement(String query, String... args) throws SQLException, IllegalArgumentException {
        if(query == null || query.isEmpty())
            throw new IllegalArgumentException("Empty SQL query");
        query = insertArgs(query, args);
        return connection.prepareStatement(query);
    }

    public String insertArgs(String query, String[] args) { return  insertArgs(query, args, 0); }

    public String insertArgs(String query, String[] args, int argsBias) {
        if(args != null) {
            for (int i = argsBias; i < args.length; i++) {
                boolean skip = false;
                if (query.contains("@a" + i)) {
                    for(String constant : argumentConstants) {
                        if (query.contains("@a" + i + constant)) {
                            query = query.replace("@a" + i + constant, constant);
                            skip = true;
                            //break;
                        }
                    }
                    if(!skip)
                        query = query.replace("@a" + i, args[i - argsBias]);
                } else {
                    throw new IllegalArgumentException(lastLoadedResource + " don't receives " + i + " parameter(s)");
                }
            }
        }
        query = query.replaceAll("'@a(\\d*)'", "null");
        query = query.replaceAll("@a(\\d*)", "null");
        return query;
    }

    public ResultSet executeSelect(String query, String... args) {
        if(!validateQuery(query))
            return null;
        ResultSet resultSet;
        try {
            PreparedStatement statement = prepareStatement(query, args);
            resultSet = executeSelect(statement);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return resultSet;
    }

    public ResultSet executeSelectSimple(String tableName, String column, String condition) {
        return executeSelect(loadSQLResource("select_any.sql"), column,
                tableName + (condition.isEmpty() ? "" : " where " + condition));
    }

    public ResultSet executeSelect(PreparedStatement statement, String... args) {
        ResultSet resultSet;
        try {
            if(args.length > 0)
                statement = prepareStatement(statement.toString(), args);
            logBeforeExecution(statement.toString());
            resultSet = statement.executeQuery();
            logAfterExecution(true);
        } catch (SQLException e) {
            Logger.log(SQLExecutor.class, e.getMessage(), 2);
            logAfterExecution(false);
            return null;
        }
        return resultSet;
    }

    public boolean executeUpdate(String query, String... args) throws SQLException {
        if(!validateQuery(query))
            return false;
        PreparedStatement statement = prepareStatement(query, args);
        return executeUpdate(statement);
    }

    public boolean executeUpdate(PreparedStatement statement, String... args) throws SQLException {
        try {
            if(args.length > 0)
                statement = prepareStatement(statement.toString(), args);
            logBeforeExecution(statement.toString());
            statement.executeUpdate();
            logAfterExecution(true);
        } catch (SQLException e) {
            logAfterExecution(false);
            Logger.log(SQLExecutor.class, e.getMessage(), 2);
            throw e;
        }
        return true;
    }

    public boolean executeInsert(String query, String table, String... args) throws SQLException {
        logBeforeExecution(query);
        InsertQueryBuilder queryBuilder = new InsertQueryBuilder(table, query);
        queryBuilder.addRow(args);
        PreparedStatement statement = queryBuilder.getStatement();
        return executeUpdate(statement);
    }

    public boolean executeCall(String query, String... args) {
        try {
            PreparedStatement statement = prepareStatement(query, args);
            logBeforeExecution();
            logBeforeExecution(statement.toString());
            statement.executeQuery();
            logAfterExecution(true);
        } catch (SQLException e) {
            logAfterExecution(false);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean uploadImage(File image) {
        String name = image.getName();
        String ext = "jpg";
        try {
            PreparedStatement statement = connection.prepareStatement(loadSQLResource("insert_image.sql"));
            statement.setString(1, name);
            statement.setString(2, ext);
            statement.setBinaryStream(3, FileResourcesUtils.getFileAsStream(image));
            statement.execute();
            return true;
        } catch (Exception e) {
            Logger.log(this, e.getMessage(), 2);
            return false;
        }
    }

    public String loadSQLResource(String resourceName) {
        try {
            lastLoadedResource = resourceName;
            return FileResourcesUtils.getFileDataAsString(SQL_RESOURCE_PATH + resourceName);
        } catch (IOException e) {
            Logger.log(SQLExecutor.class, "Can't load resource: " + e.getLocalizedMessage(), 2);
            return "";
        }
    }

    private boolean validateQuery(String query) {
        if(query == null || query.isEmpty()) {
            Logger.log(SQLExecutor.class, "Invalid query found", 3);
            return false;
        }
        return true;
    }

    private void logBeforeExecution() {
        Logger.log(this, "Executing query: " + lastLoadedResource, 3);
    }

    private void logBeforeExecution(String statement) {
        Logger.log(this, "Executing statement: " + statement, 3);
    }

    private void logAfterExecution(boolean successful) {
        if(successful)
            Logger.log(this, "Query executed", 4);
        else
            Logger.log(this,"Query execution failed: " + lastLoadedResource, 2);
    }
}
