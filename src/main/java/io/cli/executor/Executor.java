package io.cli.executor;

import io.cli.command.Command;
import io.cli.context.Context;
import io.cli.exception.CLIException;
import io.cli.exception.PipeException;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
        if (commands.isEmpty()) return;

        List<PipedOutputStream> outputStreams = new ArrayList<>();
        List<PipedInputStream> inputStreams = new ArrayList<>();


        List<Future<Void>> futures = new ArrayList<>();

        try (ExecutorService executor = Executors.newFixedThreadPool(commands.size())) {
            for (int i = 0; i < commands.size() - 1; i++) {
                PipedOutputStream out = new PipedOutputStream();
                PipedInputStream in = new PipedInputStream(out);
                outputStreams.add(out);
                inputStreams.add(in);
            }

            outputStreams.add(null);  // Last command: output = System.out
//            inputStreams.add(null);

            for (int i = 0; i < commands.size(); i++) {
                final Command command = commands.get(i);

                InputStream inputStream = (i == 0) ? System.in : inputStreams.get(i - 1);
                PipedOutputStream outputStream = outputStreams.get(i);

                command.setInputStream(inputStream);
                command.setOutputStream(outputStream != null ? outputStream : System.out);

                Future<Void> future = executor.submit(() -> {
                    try {
                        command.execute();
                        return null;
                    } catch (CLIException e) {
                        context.setVar("?", Integer.toString(e.getExitCode()));
                        throw e;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    } finally {
                        if (outputStream != null) {
                            try {
                                outputStream.close();
                            } catch (IOException ignored) {
                            }
                        }
                    }
                });

                futures.add(future);
            }

            // Waiting for all commands to stop
            for (Future<Void> future : futures) {
                future.get();  // Also throws exceptions from commands
            }

            context.setVar("?", "0");

        } catch (IOException | InterruptedException e) {
            throw new PipeException(e.getMessage());
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof CLIException) {
                throw (CLIException) cause;
            } else {
                throw new PipeException(cause.getMessage());
            }
        } finally {
            // Close all pipes
            try {
                for (PipedInputStream in : inputStreams) {
                    if (in != null) in.close();
                }
            } catch (IOException e) {
                throw new PipeException(e.getMessage());
            }
        }
    }
}
