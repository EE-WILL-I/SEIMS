package ru.seims.application.servlet.rest;

import org.springframework.web.bind.annotation.*;
import ru.seims.application.Main;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import ru.seims.application.context.GlobalApplicationContext;
import ru.seims.utils.json.JSONBuilder;

@RestController
@RequestMapping("/api/app")
public class ApplicationRestServlet implements ApplicationContextAware {
    private ApplicationContext context;

    @GetMapping("/status")
    public String getStatus() {
        return new JSONBuilder().addAVP("status", "OK").getString();
    }

    @PostMapping("/shutdown")
    public void shutdownApplication() {
        ((ConfigurableApplicationContext)context).close();
    }

    @PostMapping("/restart/{args}")
    public void restartApplicationWithArgs(@PathVariable(required = false) String [] args) throws Exception {
        Main.restart(args);
    }

    @PostMapping("/restart")
    public void restartApplication() throws Exception {
        restartApplicationWithArgs(new String[0]);
    }

    @PostMapping("/clearContext")
    public void clearContext() throws Exception {
        GlobalApplicationContext.clearContext();
    }

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        this.context = ctx;

    }
}
