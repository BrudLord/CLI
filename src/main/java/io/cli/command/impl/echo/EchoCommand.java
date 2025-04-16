package io.cli.command.impl.echo;

import io.cli.command.Command;
import io.cli.parser.token.Token;
import io.cli.command.util.CommandErrorHandler;
import io.cli.command.util.FileProcessor;

import java.io.*;
import java.util.List;

public class EchoCommand implements Command {
    private final List<Token> args;

    private InputStream inputStream = System.in;
    private OutputStream outputStream = System.out;

    public EchoCommand(List<Token> args) {
        this.args = args;
    }

    /**
     * Executes the `echo` command.
     *  - The command prints the arguments passed to it.
     * @return 0 on success, 1 on error.
     */
    @Override
    public int execute() {
        if (args.stream().anyMatch(t -> t.getInput().startsWith("-"))) {
            return CommandErrorHandler.handleInvalidOption("echo");
        }

        // Use FileProcessor wrapper to avoid closing the standard output.
        OutputStream effectiveOutput = (outputStream == System.out || outputStream == System.err)
                ? FileProcessor.nonCloseable(outputStream)
                : outputStream;

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(effectiveOutput))) {
            for (int idx = 1; idx < args.size(); idx++) {
                writer.write(args.get(idx).getInput());
            }
            writer.newLine();
            writer.flush();
            return 0;
        } catch (IOException e) {
            CommandErrorHandler.handleFileError("echo", "output", e.getMessage());
            return 1;
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
