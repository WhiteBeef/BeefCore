package ru.whitebeef.meridianbot.registry;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
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

    private final Map<String, Pair<String, Consumer<ButtonInteraction>>> buttons = new WeakHashMap<>();

    @Autowired
    public ButtonRegistry(JDA jda) {
        jda.addEventListener(this);
    }

    public void registerButton(@NotNull Plugin plugin, @NotNull Button button, @NotNull Consumer<ButtonInteraction> onClick) {
        if (buttons.containsKey(button.getId())) {
            throw new IllegalArgumentException("Button id already registered!");
        }
        buttons.put(button.getId(), new Pair<>(plugin.getInfo().getName().toLowerCase(), onClick));
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        Button button = event.getButton();
        if (!buttons.containsKey(button.getId())) {
            return;
        }
        buttons.get(button.getId()).right().accept(event.getInteraction());
    }

}
