package io.cli.command.impl.external;

import io.cli.command.Command;
import io.cli.context.Context;
import io.cli.exception.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExternalCommand implements Command {
    private final Context context;
    private final List<String> args;
    private final ExecutorService transfersExecutorService = Executors.newFixedThreadPool(3);
    private final ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
    private InputStream inputStream = System.in;
    private OutputStream outputStream = System.out;

    /**
     * Constructs an {@code AssignCommand} with the specified context and list of arguments.
     *
     * @param context The {@code Context} where the variable will be stored.
     * @param args    The list of CMD arguments.
     */
    public ExternalCommand(Context context, List<String> args) {
        this.context = context;
        this.args = args;
    }

    private void startIOTransfers(
            Process process, boolean transferInput, boolean transferOutput) {

        if (transferInput) {
            transfersExecutorService.submit(() -> {
                try (OutputStream processInput = process.getOutputStream()) {
                    inputStream.transferTo(processInput);
                } catch (IOException e) {
                    throw new InputException(e.getMessage());
                }
            });
        }

        if (transferOutput) {
            transfersExecutorService.submit(() -> {
                try (InputStream processOutput = process.getInputStream()) {
                    processOutput.transferTo(outputStream);
                } catch (IOException e) {
                    throw new OutputException(e.getMessage());
                }
            });
        }

        transfersExecutorService.submit(() -> {
            try (InputStream processError = process.getErrorStream()) {
                processError.transferTo(errorStream);
            } catch (IOException e) {
                throw new OutputException(e.getMessage());
            }
        });
    }

    private Process createAndStartProcess() throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder(args);

        // populate context ENV into process ENV
        processBuilder.environment().putAll(context.getEnvironment());
        // set process' directory to the CLI's pwd
        processBuilder.directory(Paths.get(context.pwd()).toFile());

        // Handle interactive streams
        boolean isInputInteractive = inputStream.equals(System.in);
        boolean isOutputInteractive = outputStream.equals(System.out);

        if (isInputInteractive) {
            processBuilder.redirectInput(ProcessBuilder.Redirect.INHERIT);
        }

        if (isOutputInteractive) {
            processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        }

        Process process = processBuilder.start();

        startIOTransfers(process, !isInputInteractive, !isOutputInteractive);

        return process;
    }

    @Override
    public void execute() {
        try {
            Process process = createAndStartProcess();

            int exitCode = process.waitFor();
            transfersExecutorService.shutdown();

            String errors = errorStream.toString(StandardCharsets.UTF_8);
            if (!errors.isEmpty()) {
                throw new ChildProcessException(errors, exitCode);
            }

            if (exitCode != 0) {
                throw new NonZeroExitCodeException(exitCode);
            }

        } catch (IOException | InterruptedException e) {
            throw new CLIException(e.getMessage());
        }
    }

    @Override
    public void setInputStream(InputStream newInputStream) {
        inputStream = newInputStream;
    }

    @Override
    public void setOutputStream(OutputStream newOutputStream) {
        outputStream = newOutputStream;
    }
}
