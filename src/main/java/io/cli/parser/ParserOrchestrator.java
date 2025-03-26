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
    private final PipeParser pipeParser;
    private final QuoteParser quoteParser;
    private final Substitutor substitutor;

    public ParserOrchestrator(List<CommandCreator> commandsCreators,
                              PipeParser pipeParser,
                              QuoteParser quoteParser,
                              Substitutor substitutor) {
        this.commandsCreators = commandsCreators;
        this.pipeParser = pipeParser;
        this.quoteParser = quoteParser;
        this.substitutor = substitutor;
    }

    public List<Command> parse(String str, Context context) {
        return tokenize(
                pipeParser.parsePipe(
                        substitutor.substitute(
                                quoteParser.parseQuote(str),
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