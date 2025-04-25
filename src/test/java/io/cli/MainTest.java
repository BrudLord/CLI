package io.cli;

import org.junit.jupiter.api.*;

import java.io.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MainTest {
    private static final String OUTPUT_ENDING = System.lineSeparator() + "> ";

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private Future<?> mainFuture;

    private ByteArrayOutputStream fakeOut;
    private PipedOutputStream fakeIn;

    public static boolean equalsIgnoringWhitespace(String str1, String str2) {
        String s1 = str1.replaceAll("\\s+", " ").trim();
        String s2 = str2.replaceAll("\\s+", " ").trim();
        return s1.equals(s2);
    }

    private void sendLine(String line) throws IOException {
        fakeIn.write((line + "\n").getBytes());
        fakeIn.flush();
    }

    private String waitAndGetOutput() throws InterruptedException {
        Thread.sleep(1000);
        String out = fakeOut.toString();
        fakeOut.reset();
        return out;
    }

    @BeforeEach
    void setUpCLI() throws IOException, InterruptedException {
        // Create fake System.in and System.out
        fakeOut = new ByteArrayOutputStream();
        fakeIn = new PipedOutputStream();

        PrintStream fakeOut = new PrintStream(this.fakeOut);
        PipedInputStream fakeIn = new PipedInputStream(this.fakeIn);

        System.setIn(fakeIn);
        System.setOut(fakeOut);

        mainFuture = executor.submit(() -> {
            try {
                Main.main(new String[]{});
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        assertEquals("> ", waitAndGetOutput());
    }

    @AfterEach
    void cleanUp() {
        if (mainFuture != null && !mainFuture.isDone()) {
            mainFuture.cancel(true);
        }
        executor.shutdownNow();
    }

    @Test()
    void testEchoEnRuAndExit() throws IOException, InterruptedException, ExecutionException, TimeoutException {
        sendLine("echo Hello World");
        assertEquals("Hello World" + OUTPUT_ENDING, waitAndGetOutput());

        sendLine("echo Привет,    Мир");
        assertEquals("Привет, Мир" + OUTPUT_ENDING, waitAndGetOutput());

        sendLine("echo \"Привет,    Мир\"");
        assertEquals("Привет,    Мир" + OUTPUT_ENDING, waitAndGetOutput());

        sendLine("exit 2");
        assertEquals("logout (exit code 2)" + System.lineSeparator(), waitAndGetOutput());

        mainFuture.get(5, TimeUnit.SECONDS);
    }

    @Test
    void testInteractiveCatWithPipe() throws IOException, InterruptedException, ExecutionException, TimeoutException {
        sendLine("cat | wc");
        waitAndGetOutput();

        sendLine("hello");
        sendLine("world");
        fakeIn.close();

        assertTrue(equalsIgnoringWhitespace("2 2 12\n>", waitAndGetOutput()));

        mainFuture.get(5, TimeUnit.SECONDS);
    }
}