package io.cli.command.impl.exit;

import io.cli.command.Command;
import io.cli.exception.ExitException;
import io.cli.parser.token.Token;
import io.cli.parser.token.TokenType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExitCommandTest {
    @Test
    void textExitWithoutArgs() {
        Command exitCommand = new ExitCommand(List.of(new Token(TokenType.COMMAND, "exit")));
        ExitException e = assertThrows(ExitException.class, exitCommand::execute);
        assertEquals(0, e.getExitCode());
    }

    @Test
    void textExitWithArgs() {
        Command exitCommand = new ExitCommand(Stream.of("exit", "42")
                .map(v -> new Token(TokenType.COMMAND, v))
                .toList());
        ExitException e = assertThrows(ExitException.class, exitCommand::execute);
        assertEquals(42, e.getExitCode());
    }
}