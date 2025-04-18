package io.cli.executor;

import io.cli.command.Command;
import io.cli.context.Context;
import io.cli.exception.PipeException;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.List;

/**
 * Executes a list of commands, handling piping between them.
 */
public class Executor {
    private final Context context;

    /**
     * Constructs an Executor with the given context.
     *
     * @param context the shared context for storing variables and state.
     */
    public Executor(Context context) {
        this.context = context;
    }

    /**
     * Pipes and executes a list of commands sequentially.
     *
     * @param commands the list of commands to execute.
     * @throws PipeException if there is an issue creating or closing pipes.
     */
    public void pipeAndExecuteCommands(List<Command> commands) {
        if (commands.isEmpty()) {
            return;
        }

        InputStream currentInput = System.in;
        PipedOutputStream prevOutput = null;
        PipedInputStream prevInput = null;

        try {
            for (int i = 0; i < commands.size(); i++) {
                Command command = commands.get(i);

                // Close the previous output stream.
                if (prevOutput != null) {
                    prevOutput.close();
                }

                // Set up a new pipe if this is not the last command.
                if (i < commands.size() - 1) {
                    prevOutput = new PipedOutputStream();

                    try {
                        prevInput = new PipedInputStream(prevOutput);
                    } catch (IOException e) {
                        throw new PipeException(e.getMessage());
                    }

                    command.setOutputStream(prevOutput);
                } else {
                    // For the last command, set output to System.out.
                    command.setOutputStream(System.out);
                }

                // Set the input stream for the command.
                command.setInputStream(currentInput);

                // Execute the command and retrieve its exit code.
                int exitCode = command.execute();
                context.setVar("?", Integer.toString(exitCode));

                // If a command fails, terminate execution.
                if (exitCode != 0) {
                    return;
                }

                // Update the current input stream for the next command.
                currentInput = prevInput;
            }
        } catch (IOException e) {
            throw new PipeException(e.getMessage());
        } finally {
            // Ensure all streams are properly closed in the end.
            try {
                if (prevOutput != null) {
                    prevOutput.close();
                }
                if (prevInput != null) {
                    prevInput.close();
                }
            } catch (IOException e) {
                throw new PipeException(e.getMessage());
            }
        }
    }
}
