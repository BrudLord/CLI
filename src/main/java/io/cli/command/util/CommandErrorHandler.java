package io.cli.command.util;
import java.io.PrintStream;

/**
 * Utility class for handling errors in command execution.
 */
public class CommandErrorHandler {
    private static final PrintStream errorStream = System.out;

    /**
     * Prints an error message for file-related issues.
     *
     * @param commandName The name of the command where the error occurred.
     * @param filename    The name of the file that caused the error.
     * @param message     The error message.
     */
    public static void handleFileError(String commandName, String filename, String message) {
        errorStream.println("Error: " + commandName + ": " + filename + ": " + message);
        errorStream.flush();
    }


    /**
     * Handles a general error by printing an error message and returning a custom error code.
     *
     * @param commandName The name of the command where the error occurred.
     * @param message     The error message.
     */
    public static void handleGeneralError(String commandName, String message) {
        errorStream.println("Error: " + commandName + ": " +  message);
        errorStream.flush();
    }
}