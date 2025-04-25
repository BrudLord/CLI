package io.cli.command.impl.grep;

import io.cli.exception.InputException;
import io.cli.exception.InvalidOptionException;
import io.cli.exception.NonZeroExitCodeException;
import io.cli.parser.token.Token;
import io.cli.parser.token.TokenType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link GrepCommand} implementation.
 * Tests cover standard input, file operations, flags, and error cases.
 */
public class GrepCommandTest {

    @TempDir
    Path tempDir;

    private Token grepToken;
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() {
        grepToken = new Token(TokenType.COMMAND, "grep");
        outputStream = new ByteArrayOutputStream();
    }

    @AfterEach
    void tearDown() {
        // nothing to clean up
    }

    private Path createTempFile(String name, String content) throws IOException {
        Path file = tempDir.resolve(name);
        Files.writeString(file, content);
        return file;
    }

    @Test
    void testGrepFromStandardInputSimpleMatch() throws Exception {
        String input = "foo bar\nbaz foo baz\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());

        // grep foo
        GrepCommand cmd = new GrepCommand(
                Arrays.asList(
                        grepToken,
                        new Token(TokenType.COMMAND, "foo")
                )
        );
        cmd.setInputStream(in);
        cmd.setOutputStream(outputStream);

        assertDoesNotThrow(cmd::execute);
        String result = outputStream.toString();
        assertTrue(result.contains("foo bar"));
        assertTrue(result.contains("baz foo baz"));
    }

    @Test
    void testGrepIgnoreCaseFlag() throws Exception {
        String input = "Hello\nhello\nHeLLo\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());

        // grep -i hello
        GrepCommand cmd = new GrepCommand(
                Arrays.asList(
                        grepToken,
                        new Token(TokenType.COMMAND, "-i"),
                        new Token(TokenType.COMMAND, "hello")
                )
        );
        cmd.setInputStream(in);
        cmd.setOutputStream(outputStream);

        assertDoesNotThrow(cmd::execute);
        String[] lines = outputStream.toString().split("\\R");
        assertEquals(3, lines.length);
    }

    @Test
    void testGrepWholeWordFlag() throws Exception {
        String input = "theretherex\nx there y\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());

        // grep -w there
        GrepCommand cmd = new GrepCommand(
                Arrays.asList(
                        grepToken,
                        new Token(TokenType.COMMAND, "-w"),
                        new Token(TokenType.COMMAND, "there")
                )
        );
        cmd.setInputStream(in);
        cmd.setOutputStream(outputStream);

        assertDoesNotThrow(cmd::execute);
        String result = outputStream.toString().trim();
        assertEquals("x there y", result);
    }

    @Test
    void testGrepLinesAfterMatchFlag() throws Exception {
        String input = String.join("\n", "one", "two match", "three", "four", "five");
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());

        // grep -A 2 match
        GrepCommand cmd = new GrepCommand(
                Arrays.asList(
                        grepToken,
                        new Token(TokenType.COMMAND, "-A"),
                        new Token(TokenType.COMMAND, "2"),
                        new Token(TokenType.COMMAND, "match")
                )
        );
        cmd.setInputStream(in);
        cmd.setOutputStream(outputStream);

        assertDoesNotThrow(cmd::execute);
        String[] lines = outputStream.toString().split("\\R");
        // should print the matching line and 2 lines after
        assertArrayEquals(new String[]{"two match", "three", "four"}, lines);
    }

    @Test
    void testGrepSingleFile() throws Exception {
        Path file = createTempFile("test.txt", "apple\nbanana\napple pie\n");

        // grep apple test.txt
        GrepCommand cmd = new GrepCommand(
                Arrays.asList(
                        grepToken,
                        new Token(TokenType.COMMAND, "apple"),
                        new Token(TokenType.COMMAND, file.toString())
                )
        );
        cmd.setOutputStream(outputStream);

        assertDoesNotThrow(cmd::execute);
        String out = outputStream.toString();
        assertTrue(out.contains("apple"));
        assertTrue(out.contains("apple pie"));
    }

    @Test
    void testGrepMultipleFiles() throws Exception {
        Path f1 = createTempFile("a.txt", "x\ny match\n");
        Path f2 = createTempFile("b.txt", "match z\nq\n");

        // grep match a.txt b.txt
        GrepCommand cmd = new GrepCommand(
                Arrays.asList(
                        grepToken,
                        new Token(TokenType.COMMAND, "match"),
                        new Token(TokenType.COMMAND, f1.toString()),
                        new Token(TokenType.COMMAND, f2.toString())
                )
        );
        cmd.setOutputStream(outputStream);

        assertDoesNotThrow(cmd::execute);
        String result = outputStream.toString();
        assertTrue(result.contains("y match"));
        assertTrue(result.contains("match z"));
    }

    @Test
    void testGrepInvalidRegex() {
        // grep "(*" should throw InvalidOptionException
        GrepCommand cmd = new GrepCommand(
                Arrays.asList(
                        grepToken,
                        new Token(TokenType.COMMAND, "(*")
                )
        );
        cmd.setInputStream(new ByteArrayInputStream("data".getBytes()));
        cmd.setOutputStream(outputStream);

        InvalidOptionException ex = assertThrows(InvalidOptionException.class, cmd::execute);
        assertTrue(ex.getMessage().contains("Invalid regular expression"));
    }

    @Test
    void testGrepNonexistentFile() {
        String missing = tempDir.resolve("no.txt").toString();
        GrepCommand cmd = new GrepCommand(
                Arrays.asList(
                        grepToken,
                        new Token(TokenType.COMMAND, "test"),
                        new Token(TokenType.COMMAND, missing)
                )
        );
        cmd.setOutputStream(outputStream);

        InputException ex = assertThrows(InputException.class, cmd::execute);
        assertTrue(ex.getMessage().contains(missing));
    }

    @Test
    void testGrepEmptyInput_noMatch() throws Exception {
        ByteArrayInputStream in = new ByteArrayInputStream(new byte[0]);

        GrepCommand cmd = new GrepCommand(
                Arrays.asList(
                        grepToken,
                        new Token(TokenType.COMMAND, "anything")
                )
        );
        cmd.setInputStream(in);
        cmd.setOutputStream(outputStream);

        // should not throw and produce no output
        assertDoesNotThrow(cmd::execute);
        assertEquals(0, outputStream.size());
    }
}
