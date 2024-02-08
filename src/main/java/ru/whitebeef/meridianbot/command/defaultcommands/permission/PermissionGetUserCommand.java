package ru.whitebeef.meridianbot.command.defaultcommands.permission;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;
import ru.whitebeef.meridianbot.command.AbstractCommand;
import ru.whitebeef.meridianbot.command.Alias;
import ru.whitebeef.meridianbot.command.StandardConsumers;
import ru.whitebeef.meridianbot.provider.ApplicationContextProvider;
import ru.whitebeef.meridianbot.registry.UserRegistry;
import ru.whitebeef.meridianbot.utils.StringsUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

@Log4j2
public class PermissionGetUserCommand extends AbstractCommand {


    public PermissionGetUserCommand(@NotNull String name, @NotNull String description, @NotNull String usageMessage, Consumer<String[]> onCommand, Function<String[], List<String>> onTabComplete, Map<String, AbstractCommand> subCommands, List<Alias> aliases, int minArgsCount) {
        super(name, description, usageMessage, onCommand, onTabComplete, subCommands, aliases, minArgsCount);

    }

    @Override
    protected void onCommand(String[] args) {
        ApplicationContext applicationContext = ApplicationContextProvider.getContext();
        if (args.length == 0) {
            StandardConsumers.NO_ARGS.getConsumer().accept(args);
            return;
        }
        JDA jda = applicationContext.getBean(JDA.class);
        String username = args[0];
        User discordUser = null;
        List<User> users = jda.getUsersByName(username, true);
        if (users.size() != 0) {
            discordUser = users.get(0);
        }
        if (discordUser == null) {
            log.warn("Не найден пользователь с ником " + args[0]);
            return;
        }
        UserRegistry userRegistry = applicationContext.getBean(UserRegistry.class);

        log.info("Права пользователя " + discordUser.getName() + ":");
        userRegistry.getUserByDiscordUser(discordUser).getPermissions().forEach((permission, state) ->
                log.info((state.toBoolean() ? "+ '" : "- '") + permission.getPermission() + "'"));
    }

    @Override
    protected List<String> onTabComplete(String[] args) {

        ApplicationContext applicationContext = ApplicationContextProvider.getContext();
        UserRegistry userRegistry = applicationContext.getBean(UserRegistry.class);
        if (args.length == 0) {
            return userRegistry.getLoadedUsers().stream()
                    .map(userRegistry::getDiscordUserByUser)
                    .map(User::getName)
                    .toList();
        } else if (args.length == 1) {
            return userRegistry.getLoadedUsers().stream()
                    .map(userRegistry::getDiscordUserByUser)
                    .map(User::getName)
                    .filter(name -> StringsUtils.startsWithIgnoreCase(name, args[0])).toList();
        } else {
            return Collections.emptyList();
        }
    }
}
