package io.cli.command.impl.external;

import io.cli.command.Command;
import io.cli.context.Context;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExternalCommand implements Command {
    private final Context context;
    private final List<String> args;

    private InputStream inputStream = System.in;
    private OutputStream outputStream = System.out;

    private final ExecutorService streamsExecutor = Executors.newFixedThreadPool(3);

    public ExternalCommand(Context context, List<String> args) {
        this.context = context;
        this.args = args;
    }

    @Override
    public int execute() {
        ProcessBuilder processBuilder = new ProcessBuilder(args);
        processBuilder.environment().putAll(context.getEnvironment());

        try {
            Process process = processBuilder.start();
            streamsExecutor.submit(() -> {
                try (OutputStream processInput = process.getOutputStream()) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        processInput.write(buffer, 0, bytesRead);
                        processInput.flush();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            streamsExecutor.submit(() -> {
                try (InputStream processOutput = process.getInputStream()) {
                    processOutput.transferTo(outputStream);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            streamsExecutor.submit(() -> {
                try (InputStream processError = process.getErrorStream()) {
                    processError.transferTo(System.err);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            int exitCode = process.waitFor();
            streamsExecutor.shutdown();

            return exitCode;

        } catch (IOException | InterruptedException e) {
            return 1;
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
