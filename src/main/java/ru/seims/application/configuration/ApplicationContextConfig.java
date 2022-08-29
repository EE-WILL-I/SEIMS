package ru.seims.application.configuration;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import ru.seims.utils.properties.PropertyReader;
import ru.seims.utils.properties.PropertyType;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Configuration
@ComponentScan("ru.seims.application.*")
@EnableJpaRepositories("ru.seims.database.repository")
@EntityScan("ru.seims.database.*")
public class ApplicationContextConfig {

    @Bean(name = "viewResolver")
    public InternalResourceViewResolver getViewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix(PropertyReader.getPropertyValue(PropertyType.SERVER, "spring.mvc.view.prefix"));
        viewResolver.setSuffix(PropertyReader.getPropertyValue(PropertyType.SERVER, "spring.mvc.view.suffix"));
        return viewResolver;
    }

}