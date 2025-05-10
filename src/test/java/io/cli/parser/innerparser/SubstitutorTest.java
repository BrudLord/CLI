package io.cli.parser.innerparser;

import io.cli.context.Context;
import io.cli.parser.token.Token;
import io.cli.parser.token.TokenType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubstitutorTest {
    @Test
    public void testBaseSubstitute() {
        Context context = Context.initial();
        context.setVar("x", "12");
        context.setVar("y", "404");
        var res = (new Substitutor()).substitute(List.of(new Token(TokenType.COMMAND, "x=$x;y=$y")), context);
        assertEquals(List.of(new Token(TokenType.COMMAND, "x=12;y=404")), res);
    }
    
    @Test
    public void testMissingVar() {
        Context context = Context.initial();
        var res = (new Substitutor()).substitute(List.of(new Token(TokenType.COMMAND, "x=$x;y=$y")), context);
        assertEquals(List.of(new Token(TokenType.COMMAND, "x=$x;y=$y")), res);
    }
}