package io.cli.command.impl.echo;

import io.cli.command.Command;
import io.cli.parser.token.Token;
import io.cli.parser.token.TokenType;
import io.cli.command.util.CommandErrorHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class EchoCommandTest {

    private ByteArrayOutputStream outputStream;
    private PrintStream originalErr;

    @BeforeEach
    void setUp() {
        outputStream = new ByteArrayOutputStream();
        originalErr = System.err;
    }

    @AfterEach
    void tearDown() {
        System.setErr(originalErr);
    }

    @Test
    void testEchoValidArguments() {
        Token echoToken = new Token(TokenType.COMMAND, "echo");
        Token arg1 = new Token(TokenType.COMMAND, "Hello");
        Token arg2 = new Token(TokenType.COMMAND, "World");
        Command echoCommand = new EchoCommand(Arrays.asList(echoToken, arg1, arg2));
        echoCommand.setOutputStream(outputStream);

        int exitCode = echoCommand.execute();
        assertEquals(0, exitCode, "Valid echo should return 0 exit code");
        String output = outputStream.toString();
        assertEquals("HelloWorld" + System.lineSeparator(), output, "Output should be concatenation of arguments with newline");
    }

    @Test
    void testEchoNoArguments() {
        Token echoToken = new Token(TokenType.COMMAND, "echo");
        Command echoCommand = new EchoCommand(Collections.singletonList(echoToken));
        echoCommand.setOutputStream(outputStream);

        int exitCode = echoCommand.execute();
        assertEquals(0, exitCode, "Even with no arguments, echo should return 0 exit code");
        String output = outputStream.toString();
        assertEquals(System.lineSeparator(), output, "Output should be just a newline when no arguments are provided");
    }

    @Test
    void testEchoInvalidOption() {
        Token echoToken = new Token(TokenType.COMMAND, "echo");
        Token invalidArg = new Token(TokenType.COMMAND, "-n");
        Command echoCommand = new EchoCommand(Arrays.asList(echoToken, invalidArg));
        echoCommand.setOutputStream(outputStream);

        int exitCode = echoCommand.execute();
        assertNotEquals(0, exitCode, "Echo invoked with an option should return non-zero exit code");
    }
}