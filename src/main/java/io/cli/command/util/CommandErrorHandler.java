package io.cli.command.util;

import java.io.PrintStream;

/**
 * Utility class for handling errors in command execution.
 */
public final class CommandErrorHandler {
    private static final PrintStream ERROR_STREAM = System.out;

    private CommandErrorHandler() {
    }

    /**
     * Prints an error message for file-related issues.
     *
     * @param commandName The name of the command where the error occurred.
     * @param filename    The name of the file that caused the error.
     * @param message     The error message.
     */
    public static void handleFileError(String commandName, String filename, String message) {
        ERROR_STREAM.println("Error: " + commandName + ": " + filename + ": " + message);
        ERROR_STREAM.flush();
    }


    /**
     * Handles a general error by printing an error message and returning a custom error code.
     *
     * @param commandName The name of the command where the error occurred.
     * @param message     The error message.
     */
    public static void handleGeneralError(String commandName, String message) {
        ERROR_STREAM.println("Error: " + commandName + ": " + message);
        ERROR_STREAM.flush();
    }
}
