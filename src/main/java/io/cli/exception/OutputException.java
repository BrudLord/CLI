package io.cli.exception;

public class OutputException extends CLIException {
    public OutputException(String message) {
        super("Output error: " + message);
    }
}
