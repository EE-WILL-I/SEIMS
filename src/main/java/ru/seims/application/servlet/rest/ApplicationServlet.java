package ru.seims.application.servlet.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.seims.application.Main;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.seims.utils.json.JSONBuilder;

@RestController
public class ApplicationServlet implements ApplicationContextAware {
    private ApplicationContext context;

    @GetMapping("/app/status")
    public String getStatus() {
        return new JSONBuilder().addAVP("status", "OK").getString();
    }

    @PostMapping("app/shutdown")
    public void shutdownApplication() {
        ((ConfigurableApplicationContext)context).close();
    }

    @PostMapping("app/restart/{args}")
    public void restartApplicationWithArgs(@PathVariable(required = false) String [] args) throws Exception {
        Main.restart(args);
    }

    @PostMapping("/app/restart")
    public void restartApplication() throws Exception {
        restartApplicationWithArgs(new String[0]);
    }

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        this.context = ctx;

    }
}
