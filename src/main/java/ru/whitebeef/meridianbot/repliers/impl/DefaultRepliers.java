package ru.whitebeef.meridianbot.repliers.impl;

import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import ru.whitebeef.meridianbot.repliers.Replier;

public enum DefaultRepliers {

    SUCCESS((replyCallback) -> {
        replyCallback.reply("Успех!").setEphemeral(true).queue();
    });

    private final Replier replier;

    DefaultRepliers(Replier replier) {
        this.replier = replier;
    }

    public void reply(IReplyCallback replyCallback) {
        replier.reply(replyCallback);
    }
}
