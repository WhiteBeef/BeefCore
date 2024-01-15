package ru.whitebeef.meridianbot.console;

import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.springframework.boot.CommandLineRunner;
import ru.whitebeef.meridianbot.MeridianBot;
import ru.whitebeef.meridianbot.command.AbstractCommand;
import ru.whitebeef.meridianbot.command.CustomTabCompleter;

import java.util.Arrays;

@Log4j2
public class ConsoleConfigurator implements CommandLineRunner {
    @Override
    public void run(String... args) {
        try (LoggerContext context = Configurator.initialize("ConsoleAppender", "log4j2.properties");
             Terminal terminal = TerminalBuilder.builder().build()) {
            LineReader lineReader = LineReaderBuilder.builder().terminal(terminal).completer(new CustomTabCompleter()).build();

            String userInput;
            do {
                userInput = lineReader.readLine();

                String[] commandArray = userInput.split(" ");

                AbstractCommand command = MeridianBot.getInstance().getCommandRegistry().getCommand(commandArray[0]);
                if (command == null) {
                    log.warn("Команда '" + userInput + "' не найдена!");
                    continue;
                }
                command.execute(commandArray[0], Arrays.stream(commandArray).skip(1).toArray(String[]::new));
            } while (!"stop".equalsIgnoreCase(userInput));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.exit(0);
    }
}