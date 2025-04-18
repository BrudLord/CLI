package io.cli.parser;

import io.cli.context.Context;
import io.cli.parser.innerparser.PipeParser;
import io.cli.parser.innerparser.QuoteParser;
import io.cli.parser.innerparser.Substitutor;
import io.cli.parser.token.Token;

import java.util.List;

/**
 * Orchestrates the parsing process by coordinating multiple parsers and a substitutor.
 * Combines functionality of parsing pipes, handling quotes, and performing substitutions
 * within the provided context.
 */
public class ParserOrchestrator {
    private final PipeParser pipeParser; // Responsible for parsing pipe ('|') syntax.
    private final QuoteParser quoteParser; // Handles parsing of quoted strings.
    private final Substitutor substitutor; // Performs variable or value substitution.
    private final Context context; // Provides contextual information for substitution.


    /**
     * Constructor for initializing ParserOrchestrator with required parsers and context.
     *
     * @param pipeParser   Parser for pipe syntax.
     * @param quoteParser  Parser for quoted strings.
     * @param substitutor  Substitutor for replacing variables with their values.
     * @param context      Context used during substitution.
     */
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


    /**
     * Parses the given input string by performing the following steps:
     * 1. Parses quoted sections of the input.
     * 2. Substitutes variables or values using the provided context.
     * 3. Parses pipe-separated tokens in the resulting string.
     *
     * @param input The input string to be parsed.
     * @return A list of token groups, where each group represents a segment of the input.
     */
    public List<List<Token>> parse(String input) {
        return pipeParser.parsePipe(substitutor.substitute(quoteParser.parseQuote(input), context));
    }
}