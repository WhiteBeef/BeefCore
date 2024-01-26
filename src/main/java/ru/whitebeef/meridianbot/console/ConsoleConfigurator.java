package ru.whitebeef.meridianbot.console;

import lombok.extern.log4j.Log4j2;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.whitebeef.meridianbot.MeridianBot;
import ru.whitebeef.meridianbot.command.AbstractCommand;
import ru.whitebeef.meridianbot.command.CustomTabCompleter;

import java.util.Arrays;

@Log4j2
@Component
public class ConsoleConfigurator implements CommandLineRunner {
    @Override
    public void run(String... args) {
        try (Terminal terminal = TerminalBuilder
                .builder()
                .system(true)
                .build()) {
            LineReader lineReader = LineReaderBuilder
                    .builder()
                    .terminal(terminal)
                    .completer(new CustomTabCompleter())
                    .build();

            String userInput = "";

            do {
                try {
                    userInput = lineReader.readLine("> ");
                    if (userInput.isEmpty()) {
                        continue;
                    }
                    String[] commandArray = userInput.split(" ");
                    if (commandArray.length == 0) {
                        continue;
                    }
                    AbstractCommand command = MeridianBot.getInstance().getCommandRegistry().getCommand(commandArray[0]);
                    if (command == null) {
                        log.warn("Команда '" + userInput + "' не найдена!");
                        continue;
                    }
                    command.execute(commandArray[0], Arrays.stream(commandArray).skip(1).toArray(String[]::new));
                } catch (UserInterruptException e) {
                    break;
                } catch (Exception e) {
                    log.error("Ошибка при вводе команды '" + userInput + "': ");
                    e.printStackTrace();
                }
            } while (!userInput.startsWith("stop"));
        } catch (
                Exception e) {
            log.error("Ошибка ввода!");
            e.printStackTrace();
        }
        System.exit(0);
    }
}