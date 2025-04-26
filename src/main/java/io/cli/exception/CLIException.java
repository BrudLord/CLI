package io.cli.exception;

/**
 * Base exception for all errors related to the command-line interface.
 */
public class CLIException extends RuntimeException {

    /**
     * Constructor accepting an error message.
     *
     * @param message the message describing the error.
     */
    public CLIException(String message) {
        super(message);
    }

    /**
     * Returns the exit code associated with this exception.
     * The default exit code is 1. Subclasses can override this method
     * to return different values.
     *
     * @return the exit code.
     */
    public int getExitCode() {
        return 1; // Default exit code
    }
}
