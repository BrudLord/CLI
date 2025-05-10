package io.cli.parser.innerparser;

import io.cli.parser.token.Token;
import io.cli.parser.token.TokenType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PipeParserTest {
    @Test
    public void testBasePipe() {
        List<Token> tokens = new ArrayList<>();
        tokens.add(new Token(TokenType.COMMAND, "x=12"));
        tokens.add(new Token(TokenType.COMMAND, "1 | 2 | 3"));
        tokens.add(new Token(TokenType.COMMAND, "4 | 5"));
        var actual = (new PipeParser()).parsePipe(tokens);

        List<List<Token>> expect = new ArrayList<>();
        expect.add(List.of(new Token(TokenType.COMMAND, "x=12"), new Token(TokenType.COMMAND, "1 ")));
        expect.add(List.of(new Token(TokenType.COMMAND, " 2 ")));
        expect.add(List.of(new Token(TokenType.COMMAND, " 3"), new Token(TokenType.COMMAND, "4 ")));
        expect.add(List.of(new Token(TokenType.COMMAND, " 5")));

        assertEquals(expect, actual);
    }
}