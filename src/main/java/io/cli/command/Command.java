package io.cli.command;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Represents a command that can be executed within a custom command-line interface (CLI).
 */
public interface Command {
    /**
     * Executes the command.
     *
     * @return The exit code of the command. Conventionally, 0 indicates success,
     * and non-zero values indicate some error.
     */
    int execute();

    /**
     * Sets the input stream for the command. This allows the command to receive
     * input, for example, from the user or from the output of another command (piping).
     *
     * @param newInputStream The {@code InputStream} to be used by the command.
     */
    void setInputStream(InputStream newInputStream);

    /**
     * Sets the output stream for the command. This allows the command to write
     * its output, for example, to the console or to a file.
     *
     * @param newOutputStream The {@code OutputStream} to be used by the command.
     */
    void setOutputStream(OutputStream newOutputStream);
}
