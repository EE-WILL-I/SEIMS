package ru.seims.application;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

public class ServletInitializer extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Main.class);
        //return builder.sources(Main.class);
    }
}/* extends AbstractAnnotationConfigDispatcherServletInitializer {
    @Override
    protected Class<?>[] getRootConfigClasses() {
        System.out.println("Security Servlet init");
        return new Class[] {  };
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        System.out.println("Config Servlet init");
        return new Class[] {  };
    }

    @Override
    protected String[] getServletMappings() {
        return new String[] { "/" };
    }

}*/
