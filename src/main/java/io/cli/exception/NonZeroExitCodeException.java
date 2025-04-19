package io.cli.exception;

public class NonZeroExitCodeException extends CLIException {
    private final int exitCode;

    /**
     * Constructor accepting an exit code != 0.
     *
     * @param exitCode the exit code of the command.
     */
    public NonZeroExitCodeException(int exitCode) {
        super("Program finished with exit code: %d".formatted(exitCode));
        this.exitCode = exitCode;
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }
}
