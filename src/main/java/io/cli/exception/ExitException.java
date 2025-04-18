package io.cli.exception;

/**
 * Exception signaling the termination of the application.
 *
 * This exception is used to exit the application with a specific exit code.
 */
public class ExitException extends CLIException {
    private final int exitCode; // Exit code indicating the reason for termination

    /**
     * Constructor that creates an exception with the specified exit code.
     *
     * @param exitCode the exit code with which the application terminates.
     */
    public ExitException(int exitCode) {
        super("logout (exit code %d)".formatted(exitCode)); // Formatted error message
        this.exitCode = exitCode;
    }

    /**
     * Returns the exit code associated with this exception.
     *
     * @return the exit code.
     */
    @Override
    public int getExitCode() {
        return exitCode;
    }
}
