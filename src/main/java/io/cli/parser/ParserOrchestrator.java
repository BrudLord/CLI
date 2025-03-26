package io.cli.parser;

import io.cli.command.Command;
import io.cli.command.CommandCreator;
import io.cli.context.Context;
import io.cli.parser.innerparser.PipeParser;
import io.cli.parser.innerparser.QuoteParser;
import io.cli.parser.innerparser.Substitutor;
import io.cli.parser.token.Token;

import java.util.List;
import java.util.Optional;

public class ParserOrchestrator {
    private final List<CommandCreator> commandsCreators;

    public ParserOrchestrator(List<CommandCreator> commandsCreators) {
        this.commandsCreators = commandsCreators;
    }

    public List<Command> parse(String str, Context context) {
        return tokenize(
                PipeParser.parsePipe(
                        Substitutor.substitute(
                                QuoteParser.parseQuote(str),
                                context
                        )
                )
        );
    }

    private List<Command> tokenize(List<List<Token>> tokens) {
        return tokens.stream()
                .map(tokenList -> commandsCreators.stream()
                        .map(command -> command.newCommand(tokenList))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .findFirst()
                        .orElseThrow()
                ).toList();
    }
}