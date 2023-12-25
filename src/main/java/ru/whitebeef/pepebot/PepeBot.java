package ru.whitebeef.pepebot;

import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import ru.whitebeef.pepebot.command.AbstractCommand;
import ru.whitebeef.pepebot.command.CommandManager;
import ru.whitebeef.pepebot.command.defaultcommands.HelpCommand;
import ru.whitebeef.pepebot.plugin.PluginRegistry;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class PepeBot {

    private static final Logger logger = LogManager.getLogger(PepeBot.class);

    public static void main(String[] args) {
        try (LoggerContext context = Configurator.initialize("ConsoleAppender", "log4j2.xml");
             Terminal terminal = TerminalBuilder.builder().build()) {

            LineReader lineReader = LineReaderBuilder.builder().terminal(terminal).build();

            new PepeBot();

            logger.info("JLine with Log4j2 integration example");
            String userInput;
            do {
                userInput = lineReader.readLine();

                String[] commandArray = userInput.split(" ");

                AbstractCommand command = PepeBot.getInstance().getCommandManager().getCommand(commandArray[0]);
                if (command == null) {
                    logger.warn("Команда '" + userInput + "' не найдена!");
                    continue;
                }
                command.execute(commandArray[0], Arrays.stream(commandArray).skip(1).toArray(String[]::new));
            } while (!"stop".equalsIgnoreCase(userInput));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    @Getter
    private final File mainFolder;
    @Getter
    private final CommandManager commandManager;

    @Getter
    private final PluginRegistry pluginRegistry;
    @Getter
    private static PepeBot instance;

    protected PepeBot() {
        instance = this;

        pluginRegistry = new PluginRegistry();

        try {
            mainFolder = Paths.get(PepeBot.class.getProtectionDomain().getCodeSource().getLocation().toURI()).toFile().getParentFile();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        commandManager = new CommandManager();

        registerCommands();
        pluginRegistry.registerPlugins("plugins");
    }

    private void registerCommands() {
        List<AbstractCommand> abstractCommands = new ArrayList<>();
        abstractCommands.add(
                AbstractCommand.builder("help", HelpCommand.class)
                        .setDescription("Команда для просмотра доступных команд")
                        .setUsageMessage("help")
                        .build());
        abstractCommands.add(
                AbstractCommand.builder("help1", HelpCommand.class)
                        .setDescription("Команда для просмотра доступных команд1")
                        .setUsageMessage("help1")
                        .build());

        abstractCommands.forEach(commandManager::registerCommandWithoutPlugin);
    }

}