package ru.whitebeef.meridianbot.command;

import org.jline.reader.Candidate;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;
import org.jline.reader.impl.completer.StringsCompleter;
import ru.whitebeef.meridianbot.MeridianBot;

import java.util.Arrays;
import java.util.List;

public class CustomTabCompleter extends StringsCompleter {

    public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
        String[] words = line.words().toArray(new String[0]);
        if (words.length == 0) {
            return;
        }
        AbstractCommand command = MeridianBot.getInstance().getCommandRegistry().getCommand(words[0]);

        if (command == null) {
            return;
        }
        candidates.addAll(command.tabComplete(Arrays.stream(words).skip(1).toArray(String[]::new)).stream().map(Candidate::new).toList());

    }
}
