package ru.seims.application;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.util.ResourceUtils;
import ru.seims.application.context.GlobalApplicationContext;
import ru.seims.utils.FileResourcesUtils;
import ru.seims.utils.logging.Logger;
import ru.seims.utils.properties.PropertyReader;
import ru.seims.application.security.service.AuthenticationService;
import ru.seims.application.security.handler.SecurityHandlerInterceptor;
import ru.seims.mailservice.MNSAuthenticator;
import ru.seims.database.connection.DatabaseConnector;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import ru.seims.utils.properties.PropertyType;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

@SpringBootApplication
public class Main {
    private static ConfigurableApplicationContext ctx;
    private static boolean isInitialized = false;
    @Bean
    public SecurityHandlerInterceptor securityHandlerInterceptor() {
        return new SecurityHandlerInterceptor();
    }

    @Bean
    public WebMvcConfigurerAdapter adapter() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                //add interceptor on page pre-load state to check if user is authorized
                registry.addInterceptor(securityHandlerInterceptor());
            }
        };
    }

    public static void main(String[] args) throws Exception {
        init();
        start(args);
    }

    private static void init() throws IOException  {
        if(isInitialized)
            return;
        System.out.println("Loading server properties");
        String resPath = "";

        try {
            //try load resources from JAR
            resPath = ResourceUtils.getFile("classpath:application.properties").getParent() + "/";
            //PropertyReader.loadServerProps();
        } catch (Exception e) {
            //load resources from .WAR directory
            System.out.println("Loading properties from local directories");
            resPath = new File(System.getProperty("java.class.path")).getAbsoluteFile().getAbsolutePath();
            //remove file name from path
            resPath = resPath.replaceAll("[^/|^\\\\]*$", "");
            resPath = resPath.replaceAll("\\\\", "/");
            System.out.println("Executable path: " + resPath);
        }
        FileResourcesUtils.RESOURCE_PATH = resPath;
        PropertyReader.loadServerProps();
        FileResourcesUtils.UPLOAD_PATH = PropertyReader.getPropertyValue(PropertyType.SERVER, "app.uploadPath");
        MNSAuthenticator.loadProvidedUserCredentials();
        String contextSizeStr = PropertyReader.getPropertyValue(PropertyType.SERVER, "cache.maxSizeBytes");
        int contextSize = contextSizeStr.isEmpty() ? 0 : Integer.parseInt(contextSizeStr);
        GlobalApplicationContext.setContextMaxSize(contextSize);
        if(!AuthenticationService.getInstance().loadConfiguredServiceUserCredentials())
            AuthenticationService.getInstance().loadDefaultServiceUserCredentials();
        isInitialized = true;
    }

    private static void start(String[] args) throws Exception {
        StringBuilder argsStr = new StringBuilder();
        for(String arg : args)
            argsStr.append(arg);
        Logger.log(Main.class, "Starting the server with args: " + argsStr.toString(), 1);
        ctx = new SpringApplicationBuilder(Main.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
        ctx.getBean(TerminateBean.class);
        Logger.log(Main.class,"Spring application started", 1);
        //connect to DB
        try {
            DatabaseConnector.getInstance().getConnection();
        } catch (Exception e) {
            e.printStackTrace();
            stop();
        }
        Logger.log(Main.class, "Server successfully started");
    }

    public static void stop() throws Exception {
        Logger.log(Main.class, "Stopping application", 1);
        try {
            DatabaseConnector.getInstance().closeConnection();
        } catch (Exception e) { Logger.log(Main.class,"Can't close DB connection. " + e.getMessage(), 2); }
        ctx.close();
        SpringApplication.exit(ctx, () -> 0);
    }

    public static void restart(String [] args) throws Exception {
        try {
            DatabaseConnector.getInstance().closeConnection();
        } catch (Exception e) { Logger.log(Main.class,"Can't close DB connection." + e.getMessage(), 2); }

        Thread thread = new Thread(() -> {
            ctx.close();
            try {
                init();
                DatabaseConnector.getInstance().setConnection(args);
                start(args);
            } catch (Exception e) {
                System.out.println("Restarting server ended up with an error. " + e.getMessage());
            }
        });

        thread.setDaemon(false);
        thread.start();
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            Logger.log(Main.class, "Starting from command line with args: " + args, 1);
            init();
        };
    }

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource dataSource(DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().build();
    }
}
