package io.cli.exception;

/**
 * Exception thrown for errors related to input processing in the CLI.
 */
public class InputException extends CLIException {

    /**
     * Constructor accepting a message describing the input error.
     *
     * @param message the message describing the input error.
     */
    public InputException(String message) {
        super("Input error: " + message);
    }
}
