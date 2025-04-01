package io.cli.command.impl.wc;

import io.cli.command.Command;
import io.cli.parser.token.Token;
import io.cli.command.util.CommandErrorHandler;
import io.cli.command.util.FileProcessor;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class WcCommand implements Command {
    private final List<Token> args;
    private InputStream inputStream = System.in;
    private OutputStream outputStream = System.out;

    public WcCommand(List<Token> args) {
        this.args = args;
    }

    /**
     * Executes the `wc` command.
     * - If no filenames are provided, reads from standard input.
     * - Otherwise, it counts lines, words, and bytes in the input files.
     * - Handles invalid options and missing files gracefully.
     *
     * @return 0 on success, 1 if there were file errors, and 2 for invalid options.
     */
    @Override
    public int execute() {
        if (args.stream().anyMatch(t -> t.getInput().startsWith("-"))) {
            return CommandErrorHandler.handleInvalidOption("wc");
        }

        boolean hasFileErrors = false;
        List<long[]> allFileCounts = new ArrayList<>();
        List<String> filenames = new ArrayList<>();
        long[] totalCounts = new long[3];

        if (args.size() == 1) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream))) {
                long[] counts = count(reader);
                writeCount(counts, "", writer);
            } catch (IOException e) {
                CommandErrorHandler.handleFileError("wc", "stdin/stdout", e.getMessage());
                hasFileErrors = true;
            }
        } else {
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream))) {
                for (int i = 1; i < args.size(); i++) {
                    String filename = args.get(i).getInput();
                    filenames.add(filename);

                    try (BufferedReader fileReader = FileProcessor.getReader(filename)) {
                        long[] fileCounts = count(fileReader);
                        allFileCounts.add(fileCounts);
                        totalCounts[0] += fileCounts[0];
                        totalCounts[1] += fileCounts[1];
                        totalCounts[2] += fileCounts[2];
                        writeCount(fileCounts, filename, writer);
                    } catch (IOException e) {
                        CommandErrorHandler.handleFileError("wc", "input", e.getMessage());
                        hasFileErrors = true;
                    }
                }

                if (filenames.size() > 1) {
                    writeCount(totalCounts, "total", writer);
                }
            } catch (IOException e) {
                CommandErrorHandler.handleFileError("wc", "output", e.getMessage());
                hasFileErrors = true;
            }

        }

        return hasFileErrors ? 1 : 0;
    }

    private long[] count(BufferedReader reader) throws IOException {
        long lines = 0, words = 0, bytes = 0;
        String line;

        while ((line = reader.readLine()) != null) {
            lines++;
            bytes += line.getBytes().length + 1; 
            if (!line.trim().isEmpty()) {
                words += line.trim().split("\\s+").length;
            }
        }

        return new long[]{lines, words, bytes};
    }

    private void writeCount(long[] counts, String label, BufferedWriter writer) throws IOException {
        String output = String.format("%7d %7d %7d %s", counts[0], counts[1], counts[2], label);
        FileProcessor.writeOutput(writer, output);
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
