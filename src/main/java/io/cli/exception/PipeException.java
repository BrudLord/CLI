package io.cli.exception;

/**
 * Exception thrown for errors related to piping in the CLI.
 */
public class PipeException extends CLIException {

    /**
     * Constructor accepting a message describing the pipe error.
     *
     * @param message the message describing the pipe error.
     */
    public PipeException(String message) {
        super("Pipe error: " + message);
    }
}
