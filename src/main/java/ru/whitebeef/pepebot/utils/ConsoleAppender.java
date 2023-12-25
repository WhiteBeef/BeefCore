package ru.whitebeef.pepebot.utils;

import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.io.Serializable;

@Plugin(name = "ConsoleAppender", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE, printObject = true)
public class ConsoleAppender extends AbstractAppender {

    private static ConsoleAppender instance;

    private Terminal terminal;
    private LineReader reader;

    protected ConsoleAppender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions) {
        super(name, filter, layout, ignoreExceptions, Property.EMPTY_ARRAY);
        instance = this;
        try {
            this.terminal = TerminalBuilder.builder().dumb(true).build();
            this.reader = LineReaderBuilder.builder().appName(name).terminal(terminal).build();
            this.reader.setOpt(LineReader.Option.BRACKETED_PASTE);
            this.reader.unsetOpt(LineReader.Option.INSERT_TAB);
        } catch (IllegalStateException | IOException e) {
            e.printStackTrace();
        }
    }

    public static ConsoleAppender getInstance() {
        return instance;
    }

    @Contract("_, _, !null, _ -> new")
    @PluginFactory
    public static @NotNull ConsoleAppender createAppender(
            @PluginAttribute("name") String name,
            @PluginElement("Filter") Filter filter,
            @PluginElement("Layout") Layout<? extends Serializable> layout,
            @PluginAttribute(value = "ignoreExceptions", defaultBoolean = true) boolean ignoreExceptions) {

        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }

        return new ConsoleAppender(name, filter, layout, ignoreExceptions);
    }

    public Terminal getTerminal() {
        return terminal;
    }

    public LineReader getReader() {
        return reader;
    }

    @Override
    public void append(LogEvent event) {
        if (terminal == null) {
            return;
        }
        if (reader == null) {
            terminal.writer().write(getLayout().toSerializable(event).toString());
        } else {
            reader.printAbove(getLayout().toSerializable(event).toString());
        }
    }

    public void close() throws IOException {
        if (terminal == null) {
            return;
        }
        try {
            terminal.close();
        } finally {
            terminal = null;
        }
    }
}