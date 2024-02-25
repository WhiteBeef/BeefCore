package ru.whitebeef.meridianbot.registry;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.whitebeef.meridianbot.plugin.Plugin;
import ru.whitebeef.meridianbot.utils.Pair;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Consumer;

@Component
public class ButtonRegistry extends ListenerAdapter {

    private final Map<Button, Pair<String, Consumer<Button>>> buttons = new WeakHashMap<>();

    @Autowired
    public ButtonRegistry(JDA jda) {
        jda.addEventListener(this);
    }

    public void registerButton(@NotNull Plugin plugin, @NotNull Button button, @NotNull Consumer<Button> onClick) {
        if (buttons.containsKey(button)) {
            throw new IllegalArgumentException("Button already registered!");
        }
        buttons.put(button, new Pair<>(plugin.getInfo().getName().toLowerCase(), onClick));
    }

    public void onButtonInteraction(ButtonInteractionEvent event) {
        Button button = event.getButton();
        if (!buttons.containsKey(button)) {
            return;
        }
        buttons.get(button).right().accept(button);
    }

}
