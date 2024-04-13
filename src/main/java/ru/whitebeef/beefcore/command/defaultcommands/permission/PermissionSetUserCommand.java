package ru.whitebeef.beefcore.command.defaultcommands.permission;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;
import ru.whitebeef.beefcore.command.AbstractCommand;
import ru.whitebeef.beefcore.command.Alias;
import ru.whitebeef.beefcore.command.DefaultConsumers;
import ru.whitebeef.beefcore.entities.Permission;
import ru.whitebeef.beefcore.provider.ApplicationContextProvider;
import ru.whitebeef.beefcore.registry.UserRegistry;
import ru.whitebeef.beefcore.utils.StringsUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

@Log4j2
public class PermissionSetUserCommand extends AbstractCommand {

    public PermissionSetUserCommand(@NotNull String name, @NotNull String description, @NotNull String usageMessage, Consumer<String[]> onCommand, Function<String[], List<String>> onTabComplete, Map<String, AbstractCommand> subCommands, List<Alias> aliases, int minArgsCount) {
        super(name, description, usageMessage, onCommand, onTabComplete, subCommands, aliases, minArgsCount);

    }

    @Override
    protected void onCommand(String[] args) {

        ApplicationContext applicationContext = ApplicationContextProvider.getContext();
        if (args.length < 3) {
            DefaultConsumers.NO_ARGS.getConsumer().accept(args);
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
        Permission permission = Permission.of(args[1], false);
        Permission.State permissionState;
        try {
            permissionState = Permission.State.valueOf(args[2]);
        } catch (Exception exception) {
            log.warn("Состояние " + args[2] + " не найдено!");
            StringBuilder availableStates = new StringBuilder("");
            Arrays.stream(Permission.State.values()).forEach(state -> availableStates.append(state + " "));
            log.warn("Доступные состояния: " + availableStates);
            return;
        }
        userRegistry.getUserByDiscordUser(discordUser).setPermission(permission, permissionState);
        Permission.of(permission.getPermission(), true);
        log.info("Пользователю " + discordUser.getName() + " применено право: " + (permissionState.toBoolean() ? "+ '" : "- '") + permission.getPermission() + "'");
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
        } else if (args.length == 2) {
            Permission permission = Permission.of(args[1], false);
            if (permission.isRootPermission()) {
                return Permission.getRegisteredPermissions().keySet().stream()
                        .filter(permissionName -> StringsUtils.startsWithIgnoreCase(permissionName, args[1]))
                        .toList();
            }
            return permission.getParent().getChildren().stream()
                    .map(Permission::getPermission)
                    .filter(permissionName -> StringsUtils.startsWithIgnoreCase(permissionName, args[1])).toList();
        } else if (args.length == 3) {
            return List.of("ALLOWED", "DENIED", "NOT_SET");
        } else {
            return Collections.emptyList();
        }
    }

}
