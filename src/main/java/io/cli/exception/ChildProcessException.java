package io.cli.exception;

/**
 * Represents an exception thrown by a child process.
 */
public class ChildProcessException extends CLIException {
    private final int exitCode;

    /**
     * Constructs a new ChildProcessException with the specified message and exit code.
     *
     * @param message  the detail message.
     * @param exitCode the exit code returned by the child process.
     */
    public ChildProcessException(String message, int exitCode) {
        super(message);
        this.exitCode = exitCode;
    }

    /**
     * Retrieves the exit code returned by the child process.
     *
     * @return the exit code.
     */
    @Override
    public int getExitCode() {
        return exitCode;
    }

    /**
     * Returns a string representation of this exception, including the exit code.
     *
     * @return a string representation of this exception.
     */
    @Override
    public String toString() {
        return super.toString() + " (exit code: " + exitCode + ")";
    }
}
