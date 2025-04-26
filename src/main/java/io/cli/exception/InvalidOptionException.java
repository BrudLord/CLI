package io.cli.exception;

/**
 * Exception thrown when an invalid option is encountered in the CLI.
 */
public class InvalidOptionException extends CLIException {

    /**
     * Constructor accepting a message describing the invalid option error.
     *
     * @param message the message describing the invalid option error.
     */
    public InvalidOptionException(String message) {
        super("Invalid option: " + message);
    }
}
