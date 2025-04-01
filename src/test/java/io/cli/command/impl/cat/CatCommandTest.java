package io.cli.command.impl.cat;

import io.cli.parser.token.Token;
import io.cli.parser.token.TokenType;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import io.cli.command.util.CommandErrorHandler;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link CatCommand} implementation.
 * Tests cover standard input handling, file operations, and error cases.
 */
public class CatCommandTest {
    
    @TempDir
    Path tempDir;
    
    private Token catToken;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalErr;
    private ByteArrayOutputStream errorStream;
    
    @BeforeEach
    void setUp() {
        outputStream = new ByteArrayOutputStream();
        errorStream = new ByteArrayOutputStream();
        originalErr = System.err;
        System.setErr(new PrintStream(errorStream));
        CommandErrorHandler.setErrorStream(System.err);
        catToken = new Token(TokenType.COMMAND, "cat");
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
    
    /**
     * Tests reading from standard input (no file arguments).
     * Verifies correct content is copied to output.
     */
    @Test
    void testCatFromStandardInput() throws IOException {
        String inputContent = "Dance, dance, dance\nto the radio\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(inputContent.getBytes());
        
        CatCommand catCommand = new CatCommand(Collections.singletonList(catToken));
        catCommand.setInputStream(inputStream);
        catCommand.setOutputStream(outputStream);
        
        assertEquals(0, catCommand.execute());
        assertEquals(inputContent, normalize(outputStream.toString()));
    }
    
    /**
     * Tests reading from a single file.
     * Verifies file content is correctly output.
     */
    @Test
    void testCatFromSingleFile() throws IOException {
        String fileContent = "Я снова маленький, солнце яркое\nМама опять сильнее всех в мире\n";
        Path testFile = createTempFile("testFile.txt", fileContent);
        
        CatCommand catCommand = new CatCommand(Arrays.asList(catToken, new Token(TokenType.COMMAND, testFile.toString())));
        catCommand.setOutputStream(outputStream);
        
        assertEquals(0, catCommand.execute());
        assertEquals(fileContent, normalize(outputStream.toString()));
    }
    
    /**
     * Tests reading from multiple files.
     * Verifies concatenation of multiple files in order.
     */
    @Test
    void testCatFromMultipleFiles() throws IOException {
        Path file1 = createTempFile("file1.txt", "Hello\nWorld\n");
        Path file2 = createTempFile("file2.txt", "Goodbye\nMoon\n");
        
        CatCommand catCommand = new CatCommand(Arrays.asList(catToken, 
                new Token(TokenType.COMMAND, file1.toString()),
                new Token(TokenType.COMMAND, file2.toString())));
        catCommand.setOutputStream(outputStream);
        
        assertEquals(0, catCommand.execute());
        assertEquals("Hello\nWorld\nGoodbye\nMoon\n", normalize(outputStream.toString()));
    }
    
    /**
     * Tests handling of non-existent files.
     * Verifies proper error message and exit code.
     */
    @Test
    void testCatNonExistentFile() {
        Path testFile = tempDir.resolve("missing.txt");
        CatCommand catCommand = new CatCommand(Arrays.asList(catToken, new Token(TokenType.COMMAND, testFile.toString())));
        catCommand.setOutputStream(outputStream);
        
        assertEquals(1, catCommand.execute());
        assertTrue(errorStream.toString().contains("cat: input: " + testFile.toString()));
    }
    
    /**
     * Tests mixed scenario with existing and non-existing files.
     * Verifies partial success and proper error reporting.
     */
    @Test
    void testCatMixOfExistingAndNonExistingFiles() throws IOException {
        Path file = createTempFile("test1.txt", "file content\n");
        String missingFile = tempDir.resolve("missing.txt").toString();
        
        CatCommand catCommand = new CatCommand(Arrays.asList(catToken, 
                new Token(TokenType.COMMAND, file.toString()),
                new Token(TokenType.COMMAND, missingFile)));
        catCommand.setOutputStream(outputStream);
        
        assertEquals(1, catCommand.execute());
        assertTrue(errorStream.toString().contains("cat: input: " + missingFile));
        assertEquals("file content\n", normalize(outputStream.toString()));
    }
    
    /**
     * Tests error handling during standard input reading.
     * Verifies proper error reporting when input stream fails.
     */
    @Test
    void testErrorInStandardInput() {
        InputStream errorInputStream = new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException("Mock IO Exception");
            }
        };
        
        CatCommand catCommand = new CatCommand(Collections.singletonList(catToken));
        catCommand.setInputStream(errorInputStream);
        catCommand.setOutputStream(outputStream);

        assertEquals(1, catCommand.execute());
        assertTrue(errorStream.toString().contains("cat: stdin/stdout: "));
    }
    
    private String normalize(String text) {
        return text.replace(System.lineSeparator(), "\n");
    }
}
