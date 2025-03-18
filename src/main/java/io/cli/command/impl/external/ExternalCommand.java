package io.cli.command.impl.external;

import io.cli.command.Command;
import io.cli.context.Context;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class ExternalCommand implements Command {
    private final Context context;
    private final List<String> args;

    private InputStream inputStream = System.in;
    private OutputStream outputStream = System.out;

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

            Thread inputThread = new Thread(() -> {
                try (OutputStream processInput = process.getOutputStream()) {
                    inputStream.transferTo(processInput);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            Thread outputThread = new Thread(() -> {
                try (InputStream processOutput = process.getInputStream()) {
                    processOutput.transferTo(outputStream);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            inputThread.start();
            outputThread.start();

            inputThread.join();
            outputThread.join();

            return process.waitFor();

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
