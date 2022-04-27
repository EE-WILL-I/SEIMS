package ru.seims.application.configurations;

import ru.seims.utils.properties.PropertyReader;
import ru.seims.utils.properties.PropertyType;
import ru.seims.database.connection.DatabaseConnector;
import ru.seims.database.proccessing.SQLExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class SQLExecutorConfiguration {
    @Bean
    @Scope("singleton")
    public SQLExecutor sqlExecutor() {
        return new SQLExecutor(DatabaseConnector.getConnection(), PropertyReader.getPropertyValue(PropertyType.DATABASE, "sql.path"));
    }
}
