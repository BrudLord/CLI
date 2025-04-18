package io.cli.command.impl.pwd;

import io.cli.command.Command;
import io.cli.command.util.FileProcessor;

import java.io.*;
import java.nio.file.Paths;

public class PwdCommand implements Command {
    private InputStream inputStream = System.in;
    private OutputStream outputStream = System.out;

    /**
     * Default constructor for PwdCommand.
     */
    public PwdCommand() {
    }

    /**
     * Executes the `pwd` command.
     * - The command prints the current working directory.
     *
     * @return 0 on success, 1 if there were file errors.
     */
    @Override
    public int execute() {
        OutputStream effectiveOutput = (outputStream == System.out || outputStream == System.err)
                ? FileProcessor.nonCloseable(outputStream)
                : outputStream;
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(effectiveOutput))) {
            writer.write(Paths.get("").toAbsolutePath().toString());
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            return 1;
        }
        return 0;
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
