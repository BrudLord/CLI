package io.cli.exception;

public class PipeException extends CLIException {
    public PipeException(String message) {
        super("Pipe error: " + message);
    }
}
