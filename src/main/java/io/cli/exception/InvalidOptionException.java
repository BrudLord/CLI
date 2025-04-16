package io.cli.exception;

public class InvalidOptionException extends CLIException {
    public InvalidOptionException(String message) {
        super("Invalid option: " + message);
    }
}
