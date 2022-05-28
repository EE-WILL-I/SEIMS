package ru.seims.application.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import ru.seims.utils.properties.PropertyReader;
import ru.seims.utils.properties.PropertyType;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Configuration
@ComponentScan("ru.seims.application.*")
public class ApplicationContextConfig {

    @Bean(name = "viewResolver")
    public InternalResourceViewResolver getViewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix(PropertyReader.getPropertyValue(PropertyType.APPLICATION, "spring.mvc.view.prefix"));
        viewResolver.setSuffix(PropertyReader.getPropertyValue(PropertyType.APPLICATION, "spring.mvc.view.suffix"));
        return viewResolver;
    }

}