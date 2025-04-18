package io.cli.command.impl.cat;

import io.cli.command.Command;
import io.cli.exception.InputException;
import io.cli.exception.InvalidOptionException;
import io.cli.exception.OutputException;
import io.cli.parser.token.Token;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
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
        for (var arg : args) {
            if (arg.getInput().startsWith("-")) {
                throw new InvalidOptionException(arg.getInput());
            }
        }

        if (args.size() == 1) {

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
            cat(reader, writer);

            return 0;
        }

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));

        for (int i = 1; i < args.size(); i++) {

            String filename = args.get(i).getInput();

            try (BufferedReader fileReader = Files.newBufferedReader(Path.of(filename))) {
                cat(fileReader, writer);
            } catch (IOException e) {
                throw new InputException(e.getMessage());
            }
        }

        return 0;
    }

    private void cat(BufferedReader reader, BufferedWriter writer) {
        while (true) {
            String line;

            try {
                line = reader.readLine();
            } catch (IOException e) {
                throw new InputException(e.getMessage());
            }

            if (line == null) {
                break;
            }

            try {
                writer.write(line);
                writer.newLine();
                writer.flush();
            } catch (IOException e) {
                throw new OutputException(e.getMessage());
            }
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
