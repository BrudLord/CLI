package io.cli.command.util;
import java.io.PrintStream;

/**
 * Utility class for handling errors in command execution.
 */
public class CommandErrorHandler {
    private static PrintStream errorStream = System.err;

    /**
     * Sets a custom error output stream.
     *
     * @param stream The PrintStream to be used for error messages.
     */
    public static void setErrorStream(PrintStream stream) {
        if (stream != null) {
            errorStream = stream;
        }
    }

    /**
     * Prints an error message for invalid options and returns an error code.
     *
     * @param commandName The name of the command where the error occurred.
     * @return Exit code `2`, indicating an invalid option.
     */
    public static int handleInvalidOption(String commandName) {
        errorStream.println(commandName + ": invalid option");
        errorStream.flush();
        return 2;
    }

    /**
     * Prints an error message for file-related issues.
     *
     * @param commandName The name of the command where the error occurred.
     * @param filename    The name of the file that caused the error.
     * @param message     The error message.
     */
    public static void handleFileError(String commandName, String filename, String message) {
        errorStream.println(commandName + ": " + filename + ": " + message);
        errorStream.flush();
    }


    /**
     * Handles a general error by printing an error message and returning a custom error code.
     *
     * @param commandName The name of the command where the error occurred.
     * @param message     The error message.
     * @return The specified error code.
     */
    public static void handleGeneralError(String commandName, String message) {
        errorStream.println(commandName + ": " +  message);
        errorStream.flush();
    }
}