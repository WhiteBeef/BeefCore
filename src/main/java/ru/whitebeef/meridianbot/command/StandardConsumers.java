package ru.whitebeef.meridianbot.command;

import lombok.extern.log4j.Log4j2;

import java.util.function.Consumer;

@Log4j2
public enum StandardConsumers {


    NO_ARGS((args) -> {
        log.warn("Недостаточно аргументов!");
    });

    private final Consumer<String[]> consumer;


    StandardConsumers(Consumer<String[]> consumer) {
        this.consumer = consumer;
    }

    public Consumer<String[]> getConsumer() {
        return consumer;
    }
}
