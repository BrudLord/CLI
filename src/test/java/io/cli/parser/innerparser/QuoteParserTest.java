package io.cli.parser.innerparser;


import io.cli.parser.token.Token;
import io.cli.parser.token.TokenType;
import org.junit.jupiter.api.Test;


import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class QuoteParserTest {
    @Test
    public void testOneToken() {
        String strToParse = "1 2 3 4 5";
        List<Token> expected = List.of(new Token(TokenType.COMMAND, strToParse));
        assertEquals(expected, QuoteParser.parseQuote(strToParse));
    }

    @Test
    public void testDoubleQuotes() {
        String strToParse = "\"1 2\" 3 \"4 5\"";
        List<Token> expected = List.of(
                new Token(TokenType.DOUBLE_QUOTES, "1 2"),
                new Token(TokenType.COMMAND, " 3 "),
                new Token(TokenType.DOUBLE_QUOTES, "4 5")
        );
        assertEquals(expected, QuoteParser.parseQuote(strToParse));
    }

    @Test
    public void testSingleQuotes() {
        String strToParse = "'1 2' 3 '4 5'";
        List<Token> expected = List.of(
                new Token(TokenType.SINGLE_QUOTES, "1 2"),
                new Token(TokenType.COMMAND, " 3 "),
                new Token(TokenType.SINGLE_QUOTES, "4 5")
        );
        assertEquals(expected, QuoteParser.parseQuote(strToParse));
    }

    @Test
    public void testSingleInDoubleQuotes() {
        String strToParse = "\"1 '2' 3\"";
        List<Token> expected = List.of(
                new Token(TokenType.DOUBLE_QUOTES, "1 '2' 3")
        );
        assertEquals(expected, QuoteParser.parseQuote(strToParse));
    }

    @Test
    public void testDoubleInSingleQuotes() {
        String strToParse = "'1 \"2\" 3'";
        List<Token> expected = List.of(
                new Token(TokenType.SINGLE_QUOTES, "1 \"2\" 3")
        );
        assertEquals(expected, QuoteParser.parseQuote(strToParse));
    }
}