package io.cli.parser;

import io.cli.command.CommandFactory;
import io.cli.context.Context;
import io.cli.parser.innerparser.PipeParser;
import io.cli.parser.innerparser.QuoteParser;
import io.cli.parser.innerparser.Substitutor;
import io.cli.parser.token.Token;

import java.util.List;

public class ParserOrchestrator {
    private final PipeParser pipeParser;
    private final QuoteParser quoteParser;
    private final Substitutor substitutor;
    private final Context context;

    public ParserOrchestrator(PipeParser pipeParser,
                              QuoteParser quoteParser,
                              Substitutor substitutor,
                              Context context
    ) {
        this.pipeParser = pipeParser;
        this.quoteParser = quoteParser;
        this.substitutor = substitutor;
        this.context = context;
    }

    public List<List<Token>> parse(String input) {
        return pipeParser.parsePipe(substitutor.substitute(quoteParser.parseQuote(input), context));
    }
}