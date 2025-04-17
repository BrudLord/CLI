package io.cli.exception;

public class ChildProcessException extends CLIException {
    private final int exitCode;

    public ChildProcessException(String message, int exitCode) {
        super(message);
        this.exitCode = exitCode;
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }
}
