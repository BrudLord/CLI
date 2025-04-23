package io.cli.command.impl.grep;

import io.cli.command.Command;
import io.cli.exception.InputException;
import io.cli.exception.InvalidOptionException;
import io.cli.exception.NonZeroExitCodeException;
import io.cli.parser.token.Token;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The {@code GrepCommand} class implements a simplified grep-like command. Supported params:
 * <ul>
 * <li>Regular expressions</li>
 * <li>Whole-word matching ({@code -w})</li>
 * <li>Case-insensitive search ({@code -i})</li>
 * <li>Printing lines after a match ({@code -A})</li>
 * </ul>
 */
@CommandLine.Command(
        name = "grep",
        description = "Search for patterns in files or input stream",
        mixinStandardHelpOptions = true
)
public class GrepCommand implements Command, Callable<Integer> {

    private final List<Token> args;
    @Option(names = {"-w", "--word-regexp"}, description = "Match only whole words")
    private boolean wholeWord;
    @Option(names = {"-i", "--ignore-case"}, description = "Ignore case distinctions")
    private boolean ignoreCase;
    @Option(names = {"-A"}, description = "Print <n> lines after each match", paramLabel = "<n>")
    private int linesAfterMatch = 0;
    @Parameters(index = "0", description = "Search pattern", arity = "1")
    private String pattern;
    @Parameters(index = "1..*", description = "Input files (if empty, use stdin)")
    private List<String> files;
    private InputStream inputStream = System.in;
    private OutputStream outputStream = System.out;

    /**
     * Constructor for GrepCommand.
     *
     * @param args The arguments passed to the command.
     */
    public GrepCommand(List<Token> args) {
        this.args = args;
        new CommandLine(this).parseArgs(args
                .stream()
                .skip(1)
                .map(Token::getInput)
                .toArray(String[]::new)
        );
    }

    /**
     * Executes the {@code grep} command.
     */
    @Override
    public void execute() {
        int exitCode = call();
        if (exitCode != 0) {
            throw new NonZeroExitCodeException(exitCode);
        }
    }

    /**
     * The main entry point for the grep command via Picocli's Callable interface.
     *
     * @return Exit code: 0 if no errors, 1 otherwise.
     */
    @Override
    public Integer call() {
        String finalRegex = pattern;
        if (wholeWord) {
            finalRegex = "(?<!\\p{L})" + pattern + "(?!\\p{L})";
        }

        int flags = Pattern.UNICODE_CHARACTER_CLASS;
        if (ignoreCase) {
            flags = Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE;
        }
        Pattern compiledPattern;
        try {
            compiledPattern = Pattern.compile(finalRegex, flags);
        } catch (Exception e) {
            throw new InvalidOptionException("Invalid regular expression: " + e.getMessage());
        }
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));

        if (files == null || files.isEmpty()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            try {
                grepStream(reader, compiledPattern, writer);
                writer.flush();
            } catch (IOException e) {
                throw new InputException(e.getMessage());
            }
        } else {
            try {
                for (String fileName : files) {
                    Path path = Paths.get(fileName);
                    try (BufferedReader reader = Files.newBufferedReader(path)) {
                        grepStream(reader, compiledPattern, writer);
                    } catch (IOException e) {
                        throw new InputException("Have problem in file: " + fileName + ", problem: " + e.getMessage());
                    }
                }
                writer.flush();
            } catch (IOException e) {
                throw new InputException("Have output: " + e.getMessage());
            }
        }

        return 0;
    }

    /**
     * Reads all lines from the given reader, finds matches based on pattern,
     * and writes matching lines plus context lines to the provided writer.
     *
     * @param reader  The BufferedReader to read input from.
     * @param pattern The compiled Pattern to match against.
     * @param writer  The BufferedWriter for output.
     * @throws IOException If an I/O error occurs.
     */
    private void grepStream(BufferedReader reader, Pattern pattern, BufferedWriter writer) throws IOException {
        List<String> lines = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }

        if (lines.isEmpty()) {
            return;
        }

        Set<Integer> matchLineIndices = new TreeSet<>();
        for (int i = 0; i < lines.size(); i++) {
            Matcher matcher = pattern.matcher(lines.get(i));
            if (matcher.find()) {
                matchLineIndices.add(i);
            }
        }

        if (matchLineIndices.isEmpty()) {
            return;
        }

        int lastPrintedLine = -1;
        for (int matchLine : matchLineIndices) {
            int fromLine = Math.max(matchLine, lastPrintedLine + 1);
            int toLine = Math.min(lines.size() - 1, matchLine + linesAfterMatch);

            for (int j = fromLine; j <= toLine; j++) {
                writer.write(lines.get(j));
                writer.newLine();
            }
            lastPrintedLine = toLine;
        }
    }

    @Override
    public void setInputStream(InputStream newInputStream) {
        this.inputStream = newInputStream;
    }

    @Override
    public void setOutputStream(OutputStream newOutputStream) {
        this.outputStream = newOutputStream;
    }
}
