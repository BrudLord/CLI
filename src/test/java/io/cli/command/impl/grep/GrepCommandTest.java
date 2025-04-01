package io.cli.command.impl.grep;

import io.cli.parser.token.Token;
import io.cli.parser.token.TokenType;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

public class GrepCommandTest {

    @TempDir
    Path tempDir;

    private ByteArrayOutputStream outputStream;
    private ByteArrayInputStream inputStream;
    private PrintStream originalErr;
    private ByteArrayOutputStream errorStream;

    @BeforeEach
    void setUp() {
        outputStream = new ByteArrayOutputStream();
        errorStream = new ByteArrayOutputStream();
        originalErr = System.err;
        System.setErr(new PrintStream(errorStream));
    }

    @AfterEach
    void tearDown() {
        System.setErr(originalErr);
    }

    /**
     * Test grep reading from standard input (no file arguments).
     * Example: search for "foo" (case-sensitive) in the provided input.
     */
    @Test
    void testGrepFromStandardInput() throws IOException {
        String inputContent = "Hello world\nfoo bar baz\nAnother foo line\n";
        inputStream = new ByteArrayInputStream(inputContent.getBytes());

        GrepCommand grepCommand = new GrepCommand(
                Collections.singletonList(new Token(TokenType.COMMAND, "foo"))
        );
        grepCommand.setInputStream(inputStream);
        grepCommand.setOutputStream(outputStream);

        int exitCode = grepCommand.execute();
        assertEquals(0, exitCode, "Grep should succeed on standard input");

        String result = outputStream.toString();
        String[] lines = result.split(System.lineSeparator());
        assertEquals(2, lines.length, "Should output 2 matching lines");
        assertEquals("foo bar baz", lines[0].trim());
        assertEquals("Another foo line", lines[1].trim());
    }

    /**
     * Test grep reading from a single file.
     * Creates a temporary file and searches for a pattern in it.
     */
    @Test
    void testGrepFromSingleFile() throws IOException {
        String fileContent = "Line one\nLine two with pattern\nLine three\n";
        Path testFile = tempDir.resolve("testFile.txt");
        Files.writeString(testFile, fileContent);

        GrepCommand grepCommand = new GrepCommand(
                Arrays.asList(
                        new Token(TokenType.COMMAND, "pattern"),
                        new Token(TokenType.COMMAND, testFile.toString())
                )
        );
        grepCommand.setOutputStream(outputStream);

        int exitCode = grepCommand.execute();
        assertEquals(0, exitCode, "Grep should succeed for single file input");

        String result = outputStream.toString();
        assertTrue(result.contains("Line two with pattern"), "Output should contain the matching line");
    }

    /**
     * Test grep with ignore-case option (-i).
     * The pattern search should be case-insensitive.
     */
    @Test
    void testGrepIgnoreCase() throws IOException {
        String inputContent = "Hello\nWORLD\nhello world\n";
        inputStream = new ByteArrayInputStream(inputContent.getBytes());
        GrepCommand grepCommand = new GrepCommand(
                Arrays.asList(
                        new Token(TokenType.COMMAND, "-i"),
                        new Token(TokenType.COMMAND, "world")
                )
        );
        grepCommand.setInputStream(inputStream);
        grepCommand.setOutputStream(outputStream);

        int exitCode = grepCommand.execute();
        assertEquals(0, exitCode, "Grep with -i should succeed");

        String result = outputStream.toString();
        String[] lines = result.split(System.lineSeparator());
        assertEquals(2, lines.length, "Should match 2 lines ignoring case");
    }

    /**
     * Test grep with whole-word matching (-w).
     * Only complete word matches should be returned.
     */
    @Test
    void testGrepWholeWord() throws IOException {
        String inputContent = "cat\nconcatenate\ncatapult\n";
        inputStream = new ByteArrayInputStream(inputContent.getBytes());
        GrepCommand grepCommand = new GrepCommand(
                Arrays.asList(
                        new Token(TokenType.COMMAND, "-w"),
                        new Token(TokenType.COMMAND, "cat")
                )
        );
        grepCommand.setInputStream(inputStream);
        grepCommand.setOutputStream(outputStream);

        int exitCode = grepCommand.execute();
        assertEquals(0, exitCode, "Grep with -w should succeed");

        String result = outputStream.toString();
        String[] lines = result.split(System.lineSeparator());
        assertEquals(1, lines.length, "Should only match one whole word line");
        assertEquals("cat", lines[0].trim());
    }

    /**
     * Test grep printing context lines using the -A option.
     * Verifies that matching line plus specified number of lines after are printed.
     */
    @Test
    void testGrepWithContext() throws IOException {
        String inputContent = "line 1\nmatch here\nline 3\nline 4\n";
        inputStream = new ByteArrayInputStream(inputContent.getBytes());
        GrepCommand grepCommand = new GrepCommand(
                Arrays.asList(
                        new Token(TokenType.COMMAND, "-A"),
                        new Token(TokenType.COMMAND, "2"),
                        new Token(TokenType.COMMAND, "match")
                )
        );
        grepCommand.setInputStream(inputStream);
        grepCommand.setOutputStream(outputStream);

        int exitCode = grepCommand.execute();
        assertEquals(0, exitCode, "Grep with context (-A) should succeed");

        String result = outputStream.toString();
        String[] lines = result.split(System.lineSeparator());
        assertEquals(3, lines.length, "Should output matching line plus 2 context lines");
        assertEquals("match here", lines[0].trim());
        assertEquals("line 3", lines[1].trim());
        assertEquals("line 4", lines[2].trim());
    }
}