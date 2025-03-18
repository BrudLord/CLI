package io.cli.parsers;

import io.cli.command.Command;
import io.cli.parsers.innerparser.PipeParser;
import io.cli.parsers.innerparser.QuoteParser;
import io.cli.parsers.innerparser.Substitutor;
import io.cli.parsers.token.Token;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class InputParser {
    private final List<Command> commands;

    public List<Command> parse(String str) {
        return tokenize(
                PipeParser.parsePipe(
                        Substitutor.substitute(
                                QuoteParser.parseQuote(str)
                        )
                )
        );
    }

    private List<Command> tokenize(List<List<Token>> tokens) {
        return tokens.stream()
                .map(tokenList -> commands.stream()
                        .map(command -> command.newInstance(tokenList))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .findAny()
                        .orElseThrow()
                ).toList();
    }
}