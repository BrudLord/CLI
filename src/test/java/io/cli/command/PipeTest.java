package io.cli.command;

import io.cli.Main;
import io.cli.MainOrchestrator;
import io.cli.context.Context;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PipeTest {
    private MainOrchestrator mainOrchestrator;
    private ByteArrayOutputStream fakeOut;

    public static boolean equalsIgnoringWhitespace(String str1, String str2) {
        String s1 = str1.replaceAll("\\s+", " ").trim();
        String s2 = str2.replaceAll("\\s+", " ").trim();
        return s1.equals(s2);
    }

    @BeforeEach
    void setUp() {
        Context context = new Context();
        mainOrchestrator = Main.getMainOrchestrator(context);
        fakeOut = new ByteArrayOutputStream();
        PrintStream fakeStream = new PrintStream(fakeOut);
        System.setOut(fakeStream);
    }

    @Test
    void testSimplePipe() {
        mainOrchestrator.processInput("echo hello world | wc");
        String output = fakeOut.toString();
        assertTrue(equalsIgnoringWhitespace(output, "1 2 12"));
    }

    @Test
    void testPipeWithCommandWithoutStdin() {
        mainOrchestrator.processInput("echo hello world | pwd");
        String output = fakeOut.toString();
        assertEquals(
                Paths.get("").toAbsolutePath().toString(),
                output.trim(),
                "Output should match current working directory"
        );
    }

    @Test
    void testSeveralPipes() {
        mainOrchestrator.processInput("echo hello world | cat | wc | cat | wc");
        String output = fakeOut.toString();

        // 25 with windows line separators, and 24 on others
        assertTrue(output.matches("\\s+1\\s+3\\s+(24|25)\\s+"));
    }

    @Test
    void testGrepFromStdin() {
        String text = """
                hello,
                world!
                123
                1 + epsilon
                pop
                2a
                """;
        String regexp = "\\d+";
        mainOrchestrator.processInput("echo \"%s\" | grep %s".formatted(text, regexp));

        List<String> actual = List.of(fakeOut.toString().split(System.lineSeparator()));
        List<String> expected = List.of("123", "1 + epsilon", "2a");
        assertLinesMatch(expected, actual);
    }
}
