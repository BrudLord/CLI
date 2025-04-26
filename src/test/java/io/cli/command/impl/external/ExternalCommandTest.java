package io.cli.command.impl.external;

import io.cli.context.Context;
import io.cli.exception.CLIException;
import io.cli.exception.NonZeroExitCodeException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class ExternalCommandTest {

    private Context context;
    private ByteArrayOutputStream outputStream;
    private Path successScriptFile;
    private Path errorExitCodeScriptFile;
    private Path errorStreamScriptFile;
    private Path nonExecutableScriptFile;

    @BeforeEach
    void setUp() throws IOException {
        context = new Context();
        context.setVar("text", "hello world");
        outputStream = new ByteArrayOutputStream();

        successScriptFile = createScriptFile("success_script", "echo \"$text\"", "@echo %text%");
        errorExitCodeScriptFile = createScriptFile("error_exit_script", "exit 1", "exit 1");
        errorStreamScriptFile = createScriptFile("error_stream_script", "echo \"error\" 1>&2; exit 1", "@echo error 1>&2 & exit 1");
        nonExecutableScriptFile = createScriptFile("non_executable_script", "echo \"not executable\"", "@echo not executable");
        if (!System.getProperty("os.name").toLowerCase().contains("win")) {
            Files.setPosixFilePermissions(nonExecutableScriptFile, PosixFilePermissions.fromString("rw-r--r--"));
        }
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(successScriptFile);
        Files.deleteIfExists(errorExitCodeScriptFile);
        Files.deleteIfExists(errorStreamScriptFile);
        Files.deleteIfExists(nonExecutableScriptFile);
    }

    private Path createScriptFile(String fileName, String linuxContent, String windowsContent) throws IOException {
        Path tempFile = Files.createTempFile(fileName, System.getProperty("os.name").toLowerCase().contains("win") ? ".bat" : ".sh");
        String scriptContent = System.getProperty("os.name").toLowerCase().contains("win") ? windowsContent : linuxContent;
        Files.writeString(tempFile, scriptContent, StandardCharsets.UTF_8);
        if (!System.getProperty("os.name").toLowerCase().contains("win")) {
            Files.setPosixFilePermissions(tempFile, PosixFilePermissions.fromString("rwxr-xr-x"));
        }
        return tempFile;
    }

    @Test
    void scriptOutputsEnvironmentVariable() {
        ExternalCommand externalCommand = new ExternalCommand(context, Collections.singletonList(successScriptFile.toString()));
        externalCommand.setOutputStream(outputStream);
        externalCommand.execute();
        assertEquals("hello world" + System.lineSeparator(), outputStream.toString(StandardCharsets.UTF_8));
        assertEquals("0", context.getVar("?"));
    }

    @Test
    void scriptReturnsNonZeroExitCodeThrowsException() {
        ExternalCommand externalCommand = new ExternalCommand(context, Collections.singletonList(errorExitCodeScriptFile.toString()));
        externalCommand.setOutputStream(new ByteArrayOutputStream());
        NonZeroExitCodeException e = assertThrows(NonZeroExitCodeException.class, externalCommand::execute);
        assertEquals(1, e.getExitCode());
    }

    @Test
    void scriptWritesToErrorStreamThrowsException() {
        ExternalCommand externalCommand = new ExternalCommand(context, Collections.singletonList(errorStreamScriptFile.toString()));
        externalCommand.setOutputStream(new ByteArrayOutputStream());

        CLIException e = assertThrows(CLIException.class, externalCommand::execute);

        assertEquals(1, e.getExitCode());
    }

    @Test
    void nonExistingFileThrowsException() {
        ExternalCommand externalCommand = new ExternalCommand(context, Collections.singletonList("non_existent_script.sh"));
        CLIException e = assertThrows(CLIException.class, externalCommand::execute);
        assertTrue(e.getMessage().contains("Cannot run program") || e.getMessage().contains("CreateProcess error"));
    }
}