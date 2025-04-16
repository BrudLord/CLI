package io.cli.exception;

public class CLIException extends RuntimeException {
    public CLIException(String message) {
        super(message);
    }

    public int getExitCode() {
        return 1;
    }
}
