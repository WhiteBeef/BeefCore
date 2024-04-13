package ru.whitebeef.beefcore.command;

import lombok.extern.log4j.Log4j2;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;
import org.jline.utils.AttributedString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.whitebeef.beefcore.registry.CommandRegistry;

import java.util.Arrays;
import java.util.List;

@Log4j2
@Component
public class CustomTabCompleter implements Completer {


    private final CommandRegistry commandRegistry;

    @Autowired
    public CustomTabCompleter(CommandRegistry commandRegistry) {
        this.commandRegistry = commandRegistry;
    }

    @Override
    public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
        String[] words = line.words().toArray(new String[0]);
        if (words.length == 0) {
            return;
        }
        AbstractCommand command = commandRegistry.getCommand(words[0]);
        if (command == null) {
            candidates.addAll(commandRegistry.getRegisteredCommands().stream().map(s -> new Candidate(AttributedString.stripAnsi(s.getName()), s.getName(), null, null, null, null, true)).toList());
            return;
        }
        candidates.addAll(command.tabComplete(Arrays.stream(words).skip(1).toArray(String[]::new)).stream().map(s -> new Candidate(AttributedString.stripAnsi(s), s, null, null, null, null, true)).toList());

    }
}
