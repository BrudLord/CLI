package io.cli.command.impl.wc;

import io.cli.command.Command;
import io.cli.exception.InputException;
import io.cli.exception.InvalidOptionException;
import io.cli.exception.NonZeroExitCodeException;
import io.cli.exception.OutputException;
import io.cli.parser.token.Token;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class WcCommand implements Command {
    private static final int TOTAL_COUNTS_LEN = 3;
    private final List<Token> args;
    private InputStream inputStream = System.in;
    private OutputStream outputStream = System.out;

    /**
     * Constructs an {@code WcCommand} instance with the given list of arguments.
     *
     * @param args The list of tokens representing command-line arguments.
     */
    public WcCommand(List<Token> args) {
        this.args = args;
    }

    /**
     * Executes the `wc` command.
     * - If no filenames are provided, reads from standard input.
     * - Otherwise, it counts lines, words, and bytes in the input files.
     * - Handles invalid options and missing files gracefully.
     */
    @Override
    public void execute() {
        for (var arg : args) {
            if (arg.getInput().startsWith("-")) {
                throw new InvalidOptionException(arg.getInput());
            }
        }

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));

        if (args.size() == 1) {

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            try {
                long[] counts = count(reader);
                writeCount(counts, "", writer);
            } catch (IOException e) {
                throw new InputException(e.getMessage());
            }

            return;

        }

        boolean hasFileErrors = false;
        long[] totalCounts = new long[TOTAL_COUNTS_LEN];

        for (int i = 1; i < args.size(); i++) {

            String filename = args.get(i).getInput();

            try (BufferedReader fileReader = Files.newBufferedReader(Path.of(filename))) {

                long[] fileCounts = count(fileReader);

                totalCounts[0] += fileCounts[0];
                totalCounts[1] += fileCounts[1];
                totalCounts[2] += fileCounts[2];

                writeCount(fileCounts, filename, writer);

            } catch (IOException e) {

                try {
                    writer.write("wc: " + e.getMessage());
                    writer.newLine();
                    writer.flush();
                } catch (IOException ex) {
                    throw new OutputException(ex.getMessage());
                }

                hasFileErrors = true;
            }
        }

        if (args.size() > 2) {
            writeCount(totalCounts, "total", writer);
        }

        if (hasFileErrors) {
            throw new NonZeroExitCodeException(1);
        }
    }

    private long[] count(BufferedReader reader) throws IOException {
        long lines = 0;
        long words = 0;
        long bytes = 0;

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

    private void writeCount(long[] counts, String label, BufferedWriter writer) {
        String output = String.format("%7d %7d %7d %s", counts[0], counts[1], counts[2], label);
        try {
            writer.write(output);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            throw new OutputException(e.getMessage());
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
