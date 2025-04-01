package io.cli.command.impl.pwd;

import io.cli.command.Command;
import io.cli.parser.token.Token;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PwdCommandTest {

    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() {
        outputStream = new ByteArrayOutputStream();
    }

    @Test
    void testPwdCommand() {
        Command pwdCommand = new PwdCommand();

        pwdCommand.setOutputStream(outputStream);

        int exitCode = pwdCommand.execute();
        assertEquals(0, exitCode, "Exit code should be 0 on success");
        assertEquals(
            Paths.get("").toAbsolutePath().toString(),
            outputStream.toString().trim(),
            "Output should match current working directory"
        );
    }
}