package io.cli.command.impl.exit;

import io.cli.command.Command;
import io.cli.exception.ExitException;
import io.cli.parser.token.Token;
import io.cli.parser.token.TokenType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ExitCommandTest {
    @Test
    void testExitWithoutArgs() {
        Command exitCommand = new ExitCommand(List.of(new Token(TokenType.COMMAND, "exit")));
        ExitException e = assertThrows(ExitException.class, exitCommand::execute);
        assertEquals(0, e.getExitCode());
    }

    @Test
    void testExitWithArgs() {
        Command exitCommand = new ExitCommand(Stream.of("exit", "42")
                .map(v -> new Token(TokenType.COMMAND, v))
                .toList());
        ExitException e = assertThrows(ExitException.class, exitCommand::execute);
        assertEquals(42, e.getExitCode());
    }

    @Test
    void testNotIntegerArg() {
        Command exitCommand = new ExitCommand(Stream.of("exit", "abc")
                .map(v -> new Token(TokenType.COMMAND, v))
                .toList());
        ExitException e = assertThrows(ExitException.class, exitCommand::execute);
        assertNotEquals(0, e.getExitCode());
    }

    @Test
    void testTooManyArgs() {
        Command exitCommand = new ExitCommand(Stream.of("exit", "1", "2", "3")
                .map(v -> new Token(TokenType.COMMAND, v))
                .toList());
        ExitException e = assertThrows(ExitException.class, exitCommand::execute);
        assertNotEquals(0, e.getExitCode());
    }
}