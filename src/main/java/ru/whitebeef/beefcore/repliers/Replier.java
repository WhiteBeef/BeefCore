package ru.whitebeef.beefcore.repliers;

import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;

@FunctionalInterface
public interface Replier {

    void reply(IReplyCallback replyCallback);

}
