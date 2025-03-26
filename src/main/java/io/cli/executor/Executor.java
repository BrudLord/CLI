package io.cli.executor;

import io.cli.command.Command;
import io.cli.context.Context;

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

    public void pipeAndExecuteCommands(List<Command> commands) throws IOException {
        if (commands.isEmpty()) {
            return;
        }

        InputStream currentInput = System.in;
        PipedOutputStream prevOutput = null;
        PipedInputStream prevInput = null;

        for (int i = 0; i < commands.size(); i++) {
            Command command = commands.get(i);

            if (prevOutput != null) {
                prevOutput.close();
            }

            if (i < commands.size() - 1) {
                prevOutput = new PipedOutputStream();
                prevInput = new PipedInputStream(prevOutput);
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
