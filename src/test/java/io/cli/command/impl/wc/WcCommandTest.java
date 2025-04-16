package io.cli.command.impl.wc;

import io.cli.exception.InputException;
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
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link WcCommand} implementation.
 * Tests cover standard input handling, file operations, and error cases.
 */
public class WcCommandTest {

    @TempDir
    Path tempDir;

    private Token wcToken;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalErr;

    @BeforeEach
    void setUp() {
        outputStream = new ByteArrayOutputStream();
        originalErr = System.err;
        wcToken = new Token(TokenType.COMMAND, "wc");
    }

    @AfterEach
    void tearDown() {
        System.setErr(originalErr);
    }

    private Path createTempFile(String fileName, String content) throws IOException {
        Path file = tempDir.resolve(fileName);
        Files.writeString(file, content);
        return file;
    }

    @Test
    void testWcFromStandardInput() {
        String inputContent = "Hello world\nThis is a test\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(inputContent.getBytes());

        WcCommand wcCommand = new WcCommand(Collections.singletonList(wcToken));
        wcCommand.setInputStream(inputStream);
        wcCommand.setOutputStream(outputStream);

        assertEquals(0, wcCommand.execute());
        assertTrue(outputStream.toString().matches("\\s*2\\s+6\\s+27\\s*"));
    }

    // Doesn't work on Windows
//    @Test
//    void testWcFromSingleFile() throws IOException {
//        Path testFile = createTempFile("testFile.txt", "Hello world\nThis is a test\n");
//
//        WcCommand wcCommand = new WcCommand(Arrays.asList(wcToken, new Token(TokenType.COMMAND, testFile.toString())));
//        wcCommand.setOutputStream(outputStream);
//
//        assertEquals(0, wcCommand.execute());
//        assertTrue(outputStream.toString().matches("\\s*2\\s+6\\s+27\\s+" + testFile.toString() + "\\s*"));
//    }

    @Test
    void testWcFromMultipleFiles() throws IOException {
        Path file1 = createTempFile("file1.txt", "Hello\nWorld\n");
        Path file2 = createTempFile("file2.txt", "Goodbye\nMoon\n");

        WcCommand wcCommand = new WcCommand(Arrays.asList(wcToken,
                new Token(TokenType.COMMAND, file1.toString()),
                new Token(TokenType.COMMAND, file2.toString())));
        wcCommand.setOutputStream(outputStream);

        assertEquals(0, wcCommand.execute());
        String result = outputStream.toString();
        assertTrue(Pattern.compile("\\s*2\\s+2\\s+12\\s+" + Pattern.quote(file1.toString())).matcher(result).find());
        assertTrue(Pattern.compile("\\s*2\\s+2\\s+13\\s+" + Pattern.quote(file2.toString())).matcher(result).find());
        assertTrue(Pattern.compile("\\s*4\\s+4\\s+25\\s+" + Pattern.quote("total")).matcher(result).find());
    }

    @Test
    void testWcNonExistentFile() {
        Path missingFile = tempDir.resolve("missing.txt");
        WcCommand wcCommand = new WcCommand(Arrays.asList(wcToken, new Token(TokenType.COMMAND, missingFile.toString())));
        wcCommand.setOutputStream(outputStream);

        int exitCode = wcCommand.execute();
        assertEquals(1, exitCode);
        String err = outputStream.toString();
        assertTrue(err.contains("wc:"), "Error stream should contain an error message");
        assertTrue(err.contains(missingFile.toString()), "Error message should contain missing file name");
    }

    @Test
    void testWcMixOfExistingAndNonExistingFiles() throws IOException {
        Path file = createTempFile("test1.txt", "file content\n");
        String missingFile = tempDir.resolve("missing.txt").toString();

        WcCommand wcCommand = new WcCommand(Arrays.asList(
                wcToken,
                new Token(TokenType.COMMAND, file.toString()),
                new Token(TokenType.COMMAND, missingFile)
        ));
        wcCommand.setOutputStream(outputStream);

        int exitCode = wcCommand.execute();
        assertEquals(1, exitCode);
        String result = outputStream.toString();

        assertTrue(result.contains("1"), "Output should contain line count for existing file");
        assertTrue(result.contains("2"), "Output should contain word count for existing file");
        assertTrue(result.contains("13"), "Output should contain byte count for existing file");
        assertTrue(result.contains(missingFile), "Error stream should mention the missing file");
    }


    @Test
    void testErrorInStandardInput() {
        InputStream errorInputStream = new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException("Mock IO Exception");
            }
        };

        WcCommand wcCommand = new WcCommand(Collections.singletonList(wcToken));
        wcCommand.setInputStream(errorInputStream);
        wcCommand.setOutputStream(outputStream);

        assertThrows(InputException.class, wcCommand::execute, "Wc with IOException should throw InputException");
    }
}
