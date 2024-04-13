package ru.whitebeef.beefcore.command;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.function.Consumer;

@Getter
@Log4j2
public enum DefaultConsumers {


    NO_ARGS((args) -> {
        log.warn("Недостаточно аргументов!");
    });

    private final Consumer<String[]> consumer;


    DefaultConsumers(Consumer<String[]> consumer) {
        this.consumer = consumer;
    }

}
