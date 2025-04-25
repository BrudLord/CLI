package io.cli.parser;

import io.cli.context.Context;
import io.cli.parser.innerparser.*;
import io.cli.parser.token.Token;
import io.cli.parser.token.TokenType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static io.cli.parser.token.TokenType.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class InputParserTest {
    private ParserOrchestrator parserOrchestrator;
    Context context;

    @BeforeEach
    public void prepareInputParser() {
        context = new Context();
        // Initialize parsers for handling pipes, quotes, and substitutions.
        PipeParser pipeParser = new PipeParser();
        QuoteParser quoteParser = new QuoteParser();
        Substitutor substitutor = new Substitutor();

        // Set up the parser orchestrator with the parsers and shared context.
        parserOrchestrator = new ParserOrchestrator(pipeParser, quoteParser, substitutor, context);
    }

    @Test
    public void testSubstituteInDoubleQuotes() {
        context.setVar("x", "12");
        context.setVar("y", "404");
        String input = "x=\"$x\" some text \" more text$x text\"";
        List<List<Token>> expect = List.of(List.of(
                new Token(COMMAND, "x=12"),
                new Token(COMMAND, "some"),
                new Token(COMMAND, "text"),
                new Token(DOUBLE_QUOTES, " more text12 text")
        ));
        assertEquals(expect, parserOrchestrator.parse(input));
    }

    @Test
    public void testSubstituteInSingleQuotes() {
        context.setVar("x", "12");
        context.setVar("y", "404");
        String input = "x='$x' some text ' more text$x text'";
        List<List<Token>> expect = List.of(List.of(
                new Token(COMMAND, "x=$x"),
                new Token(COMMAND, "some"),
                new Token(COMMAND, "text"),
                new Token(SINGLE_QUOTES, " more text$x text")
        ));
        assertEquals(expect, parserOrchestrator.parse(input));
    }

    @Test
    public void testSubstituteInPipe() {
        context.setVar("x", "echo");
        context.setVar("y", "404");
        String input = "$x $y | $x text | echo $y | $x $x $y";
        List<List<Token>> expect = List.of(
                List.of(
                        new Token(COMMAND, "echo"),
                        new Token(COMMAND, "404")
                ),
                List.of(
                        new Token(COMMAND, "echo"),
                        new Token(COMMAND, "text")
                ),

                List.of(
                        new Token(COMMAND, "echo"),
                        new Token(COMMAND, "404")
                ),

                List.of(
                        new Token(COMMAND, "echo"),
                        new Token(COMMAND, "echo"),
                        new Token(COMMAND, "404")
                )
        );
        assertEquals(expect, parserOrchestrator.parse(input));
    }
}