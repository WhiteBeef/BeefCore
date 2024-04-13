package ru.whitebeef.beefcore.provider;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextProvider implements ApplicationContextAware {
    @Getter
    private static ApplicationContext context;

    public static AnnotationConfigApplicationContext getAnnotationConfigApplicationContext() {
        return (AnnotationConfigApplicationContext) context;
    }

    @Override
    @Autowired
    public void setApplicationContext(@NotNull ApplicationContext applicationContext)
            throws BeansException {
        context = applicationContext;
    }
}