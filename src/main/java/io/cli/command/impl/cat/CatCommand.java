package io.cli.command.impl.cat;

import io.cli.command.Command;
import io.cli.parser.token.Token;
import io.cli.command.util.CommandErrorHandler;
import io.cli.command.util.FileProcessor;

import java.io.*;
import java.util.List;


/**
 * The CatCommand class implements the `cat` command, which reads 
 * from standard input or files and writes their content to standard output.
 */
public class CatCommand implements Command {
    private final List<Token> args;
    private InputStream inputStream = System.in;
    private OutputStream outputStream = System.out;


    /**
     * Constructs a CatCommand instance with the given list of arguments.
     *
     * @param args The list of tokens representing command-line arguments.
     */
    public CatCommand(List<Token> args) {
        this.args = args;
    }


    /**
     * Executes the `cat` command.
     * - If no filenames are provided, reads from standard input.
     * - Otherwise, reads from the specified files and writes their content to standard output.
     * - Handles invalid options and missing files gracefully.
     *
     * @return 0 on success, 1 if there were file errors, and 2 for invalid options.
     */
    @Override
    public int execute() {
        if (args.stream().anyMatch(t -> t.getInput().startsWith("-"))) {
            return CommandErrorHandler.handleInvalidOption("cat");
        }

        boolean hasFileErrors = false;

        InputStream effectiveInput = (inputStream == System.in)
                ? FileProcessor.nonCloseable(inputStream)
                : inputStream;
        OutputStream effectiveOutput = (outputStream == System.out || outputStream == System.err)
                ? FileProcessor.nonCloseable(outputStream)
                : outputStream;

        if (args.size() == 1) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(effectiveInput));
                 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(effectiveOutput))) {

                cat(reader, writer);
            } catch (IOException e) {
                CommandErrorHandler.handleFileError("cat", "stdin/stdout", e.getMessage());
                hasFileErrors = true;
            }
        } else {
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(effectiveOutput))) {
                for (int i = 1; i < args.size(); i++) {
                    String filename = args.get(i).getInput();
                    try (BufferedReader fileReader = FileProcessor.getReader(filename)) {
                        cat(fileReader, writer);
                    } catch (IOException e) {
                        CommandErrorHandler.handleFileError("cat", "input", e.getMessage());
                        hasFileErrors = true;
                    }
                }
            } catch (IOException e) {
                CommandErrorHandler.handleFileError("cat", "output", e.getMessage());
                hasFileErrors = true;
            }
        }

        return hasFileErrors ? 1 : 0;
    }

    private void cat(BufferedReader reader, BufferedWriter writer) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            FileProcessor.writeOutput(writer, line);
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
