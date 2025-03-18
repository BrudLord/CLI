package io.cli.parsers;

import io.cli.command.Command;
import io.cli.parsers.innerparser.PipeParser;
import io.cli.parsers.innerparser.QouteParser;
import io.cli.parsers.innerparser.Substitutor;
import io.cli.parsers.token.Token;

import java.util.List;

class MainParser {
    public List<Command> parse(String str) {
        return tokenize(
                PipeParser.parsePipe(
                        Substitutor.substitute(
                                QouteParser.parseQoute(str)
                        )
                )
        );
    }

    private List<Command> tokenize(List<List<Token>> tokens) {
        return List.of();
    }
}