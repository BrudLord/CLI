package io.cli.command.impl.ls;

import io.cli.context.Context;
import io.cli.fs.PathFsApi;
import io.cli.parser.token.Token;
import io.cli.parser.token.TokenType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class LsCommandTest {

    @TempDir
    Path tempDir;

    private Token lsToken;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalErr;
    private PathFsApi fs;

    @BeforeEach
    void setUp() {
        outputStream = new ByteArrayOutputStream();
        originalErr = System.err;
        lsToken = new Token(TokenType.COMMAND, "ls");
        fs = new PathFsApi();
    }

    @AfterEach
    void tearDown() {
        System.setErr(originalErr);
    }

    @Test
    void testLsCommandOnWorkingDirectory() {
        var pwd = getResource("/commands/cd");
        var context = testContext(pwd);

        var args = Collections.singletonList(lsToken);

        LsCommand lsCommand = new LsCommand(args, fs, context);
        lsCommand.setOutputStream(outputStream);

        assertDoesNotThrow(lsCommand::execute, "Exit code should be 0 on success");
        assertEquals("dummy.txt    inner1    inner2", outputStream.toString().trim(), "`ls` output should not be empty");
    }

    @Test
    void testLsCommandOnSingleFile() {
        var pwd = getResource("/commands/cd");
        var context = testContext(pwd);

        var args = List.of(
            lsToken,
            new Token(TokenType.COMMAND, "./dummy.txt")
        );

        LsCommand lsCommand = new LsCommand(args, fs, context);
        lsCommand.setOutputStream(outputStream);

        assertDoesNotThrow(lsCommand::execute, "Exit code should be 0 on success");
        assertEquals("./dummy.txt", outputStream.toString().trim(), "`ls` output should not be empty");
    }

    @Test
    void testLsCommandOnSingleAbsoluteFilepath() {
        var pwd = getResource("/commands/cd");
        var context = testContext(pwd);

        String filepath = pwd.resolve("inner1/file1.txt").toAbsolutePath().toString();

        var args = List.of(
                lsToken,
                new Token(TokenType.COMMAND, filepath)
        );

        LsCommand lsCommand = new LsCommand(args, fs, context);
        lsCommand.setOutputStream(outputStream);

        assertDoesNotThrow(lsCommand::execute, "Exit code should be 0 on success");
        assertEquals(filepath, outputStream.toString().trim(), "`ls` output should not be empty");
    }

    @Test
    void testLsCommandOnSingleDirectory() {
        var pwd = getResource("/commands/cd");
        var context = testContext(pwd);

        String output =
                 "dir1\n"
                +"file1.txt\n"
                +"file2.txt";

        var args = List.of(
            lsToken,
            new Token(TokenType.COMMAND, "inner1")
        );

        LsCommand lsCommand = new LsCommand(args, fs, context);
        lsCommand.setOutputStream(outputStream);

        assertDoesNotThrow(lsCommand::execute, "Exit code should be 0 on success");
        assertEquals(output, outputStream.toString().trim(), "`ls` output should not be empty");
    }

    @Test
    void testLsCommandOnMultipleEntries() {
        var pwd = getResource("/commands/cd");
        var context = testContext(pwd);

        String output =
                "inner1:\n"
                +"dir1\n"
                +"file1.txt\n"
                +"file2.txt\n\n"
                +"./inner2:\n"
                +"file2.txt\n\n"
                +"dummy.txt";

        var args = List.of(
                lsToken,
                new Token(TokenType.COMMAND, "inner1"),
                new Token(TokenType.COMMAND, "./inner2"),
                new Token(TokenType.COMMAND, "dummy.txt")
        );

        LsCommand lsCommand = new LsCommand(args, fs, context);
        lsCommand.setOutputStream(outputStream);

        assertDoesNotThrow(lsCommand::execute, "Exit code should be 0 on success");
        assertEquals(output, outputStream.toString().trim(), "`ls` output should not be empty");
    }

    private Context testContext(Path pwd) {
        var context = Context.initial();
        context.pwd(pwd.toAbsolutePath().toString());
        return context;
    }

    private Path getResource(String name) {
        try {
            var url = Objects.requireNonNull(this.getClass().getResource(name)).toURI();
            return Paths.get(url);
        }
        catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}