package ru.whitebeef.beefcore.command.registrars;

import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.whitebeef.beefcore.command.AbstractCommand;
import ru.whitebeef.beefcore.command.Alias;
import ru.whitebeef.beefcore.command.SimpleCommand;
import ru.whitebeef.beefcore.plugin.Plugin;
import ru.whitebeef.beefcore.plugin.PluginRegistry;
import ru.whitebeef.beefcore.registry.CommandRegistry;

@Component
@Log4j2
public class PluginsCommandRegistrar {
    private final PluginRegistry pluginRegistry;
    private final CommandRegistry commandRegistry;

    @Autowired
    public PluginsCommandRegistrar(PluginRegistry pluginRegistry, CommandRegistry commandRegistry) {
        this.pluginRegistry = pluginRegistry;
        this.commandRegistry = commandRegistry;
    }

    @PostConstruct
    public void registerCommand() {
        AbstractCommand.builder("plugins", SimpleCommand.class)
                .addAlias(Alias.of("pl", false))
                .setDescription("Показать список плагинов")
                .setUsageMessage("plugins")
                .setOnCommand((args) -> {
                    log.info("Список плагинов:");
                    for (Plugin plugin : pluginRegistry.getLoadedPlugins()) {
                        log.info(plugin.getInfo().getName() + ": " + (plugin.isEnabled() ? "✓" : "×"));
                    }
                })
                .build().register(commandRegistry);
    }
}
