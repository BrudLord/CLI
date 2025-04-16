package io.cli.executor;

import io.cli.command.Command;
import io.cli.context.Context;
import io.cli.exception.PipeException;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.List;

public class Executor {
    private final Context context;

    public Executor(Context context) {
        this.context = context;
    }

    public void pipeAndExecuteCommands(List<Command> commands) {
        if (commands.isEmpty()) {
            return;
        }

        InputStream currentInput = System.in;
        PipedOutputStream prevOutput = null;
        PipedInputStream prevInput = null;

        for (int i = 0; i < commands.size(); i++) {
            Command command = commands.get(i);

            if (prevOutput != null) {
                try {
                    prevOutput.close();
                } catch (IOException e) {
                    throw new PipeException(e.getMessage());
                }
            }

            if (i < commands.size() - 1) {
                prevOutput = new PipedOutputStream();

                try {
                    prevInput = new PipedInputStream(prevOutput);
                } catch (IOException e) {
                    throw new PipeException(e.getMessage());
                }

                command.setOutputStream(prevOutput);
            } else {
                command.setOutputStream(System.out);
            }

            command.setInputStream(currentInput);

            int exitCode = command.execute();
            context.setVar("?", Integer.toString(exitCode));

            if (exitCode != 0) {
                return;
            }

            currentInput = prevInput;
        }
    }
}
