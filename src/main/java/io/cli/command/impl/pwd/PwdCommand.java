package io.cli.command.impl.pwd;

import io.cli.command.Command;
import io.cli.exception.OutputException;

import java.io.*;
import java.nio.file.Paths;

public class PwdCommand implements Command {
    private OutputStream outputStream = System.out;

    /**
     * Default constructor for PwdCommand.
     */
    public PwdCommand() {
    }

    /**
     * Executes the {@code pwd} command: prints the current working directory.
     */
    @Override
    public void execute() {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
        try {
            writer.write(Paths.get("").toAbsolutePath().toString());
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            throw new OutputException(e.getMessage());
        }
    }

    /**
     * Sets the input stream for this command.
     * PwdCommand typically doesn't handle input, so this method does nothing.
     *
     * @param newInputStream Ignored by this method.
     */
    @Override
    public void setInputStream(InputStream newInputStream) {
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
