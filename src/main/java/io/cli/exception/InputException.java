package io.cli.exception;

public class InputException extends CLIException {
    public InputException(String message) {
        super("Input error: " + message);
    }
}
