package io.cli.command.impl.grep;

import io.cli.parser.token.Token;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import io.cli.command.util.FileProcessor;
import io.cli.command.Command;
import io.cli.command.util.CommandErrorHandler;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The GrepCommand class implements a simplified grep-like command,
 * supporting:
 *   - Regular expressions
 *   - Whole-word matching (-w)
 *   - Case-insensitive search (-i)
 *   - Printing lines after a match (-A)
 */
@CommandLine.Command(
    name = "grep",
    description = "Search for patterns in files or input stream",
    mixinStandardHelpOptions = true 
)
public class GrepCommand implements Command, Callable<Integer> {
    
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
    
    private final List<Token> args;
    private InputStream inputStream = System.in;
    private OutputStream outputStream = System.out;
    
    /**
     * Constructor for GrepCommand.
     * 
     * @param args The arguments passed to the command.
     */
    public GrepCommand(List<Token> args) {
        this.args = args;
        new CommandLine(this).parseArgs(args.stream().map(Token::getInput).toArray(String[]::new));
    }
    
    /**
     * Executes the `grep` command.
     *
     * @return 0 if successful, otherwise 1 if any error (e.g. file I/O) occurred.
     */
    @Override
    public int execute() {
        return call();
    }
    
    /**
     * The main entry point for the grep command via Picocli's Callable interface.
     *
     * @return Exit code: 0 if no errors, 1 otherwise.
     */
    @Override
    public Integer call() {
        boolean success = true;
        String finalRegex = pattern;
        if (wholeWord) {
            finalRegex = "\\b" + pattern + "\\b";
        }
    
        // Compile the pattern with correct flags.
        int flags = 0;
        if (ignoreCase) {
            flags = Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE;
        }
        Pattern compiledPattern;
        try {
            compiledPattern = Pattern.compile(finalRegex, flags);
        } catch (Exception e) {
            CommandErrorHandler.handleGeneralError("grep", "invalid regular expression: " + e.getMessage());
            return 1;
        }
    
        // Wrap standard streams using FileProcessor wrappers.
        InputStream effectiveInput = (inputStream == System.in)
                ? FileProcessor.nonCloseable(inputStream)
                : inputStream;
        OutputStream effectiveOutput = (outputStream == System.out || outputStream == System.err)
                ? FileProcessor.nonCloseable(outputStream)
                : outputStream;
    
        if (files == null || files.isEmpty()) {
            // Read from standard input.
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(effectiveInput));
                 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(effectiveOutput))) {
                grepStream(reader, compiledPattern, writer);
                writer.flush();
            } catch (IOException e) {
                CommandErrorHandler.handleFileError("grep", "stdin/stdout", e.getMessage());
                success = false;
            }
        } else {
            // Read from each file; use one writer for all files.
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(effectiveOutput))) {
                for (String fileName : files) {
                    Path path = Paths.get(fileName);
                    try (BufferedReader reader = Files.newBufferedReader(path)) {
                        grepStream(reader, compiledPattern, writer);
                    } catch (IOException e) {
                        CommandErrorHandler.handleFileError("grep", fileName, e.getMessage());
                        success = false;
                    }
                }
                writer.flush();
            } catch (IOException e) {
                CommandErrorHandler.handleFileError("grep", "output", e.getMessage());
                success = false;
            }
        }
    
        return success ? 0 : 1;
    }
    
    /**
     * Reads all lines from the given reader, finds matches based on pattern,
     * and writes matching lines plus context lines to the provided writer.
     * 
     * @param reader   The BufferedReader to read input from.
     * @param pattern  The compiled Pattern to match against.
     * @param writer   The BufferedWriter for output.
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
    
        // Collect indices of lines that match.
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
    
    /**
     * Sets a new input stream for the command.
     *
     * @param newInputStream The new input stream to use.
     */
    @Override
    public void setInputStream(InputStream newInputStream) {
        this.inputStream = newInputStream;
    }
    
    /**
     * Sets a new output stream for the command.
     *
     * @param newOutputStream The new output stream to use.
     */
    @Override
    public void setOutputStream(OutputStream newOutputStream) {
        this.outputStream = newOutputStream;
    }
}