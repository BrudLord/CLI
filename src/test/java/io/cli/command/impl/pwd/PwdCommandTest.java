package io.cli.command.impl.pwd;

import io.cli.command.Command;
import io.cli.context.Context;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PwdCommandTest {

    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() {
        outputStream = new ByteArrayOutputStream();
    }

    @Test
    void testPwdCommand() {
        var context = Context.initial();
        Command pwdCommand = new PwdCommand(context);
        String currentAbsoluteFilepath = Paths.get("").toAbsolutePath().toString();

        pwdCommand.setOutputStream(outputStream);

        assertDoesNotThrow(pwdCommand::execute, "Exit code should be 0 on success");
        assertEquals(
            currentAbsoluteFilepath,
            outputStream.toString().trim(),
            "Output should match current working directory"
        );
    }
}