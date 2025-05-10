package io.cli.exception;

/**
 * Exception used to indicate that there is an illegal state in the command's configuration
 */
public class CommandIllegalStateException extends CLIException {
    public CommandIllegalStateException(String message) {
        super(message);
    }
}
