package io.cli.exception;

public class ExitException extends RuntimeException {
    private final int exitCode;

    public ExitException(int exitCode) {
        super("logout (exit code %d)".formatted(exitCode));
        this.exitCode = exitCode;
    }

    public int getExitCode() {
        return exitCode;
    }
}
