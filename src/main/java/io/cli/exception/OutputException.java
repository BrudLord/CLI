package io.cli.exception;

/**
 * Exception thrown for errors related to output processing in the CLI.
 */
public class OutputException extends CLIException {

    /**
     * Constructor accepting a message describing the output error.
     *
     * @param message the message describing the output error.
     */
    public OutputException(String message) {
        super("Output error: " + message);
    }
}
