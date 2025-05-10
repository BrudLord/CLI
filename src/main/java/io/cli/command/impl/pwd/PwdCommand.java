package io.cli.command.impl.pwd;

import io.cli.command.Command;
import io.cli.context.Context;
import io.cli.context.Variables;
import io.cli.exception.CommandIllegalStateException;
import io.cli.exception.OutputException;

import java.io.*;

public class PwdCommand implements Command {
    private OutputStream outputStream = System.out;
    private final Context context;

    /**
     * Default constructor for PwdCommand.
     */
    public PwdCommand(Context context) {
        this.context = context;
    }

    /**
     * Executes the {@code pwd} command: prints the current working directory.
     */
    @Override
    public void execute() {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
        try {
            String pwd = context.pwd();
            // pwd variable is not registered in the context but expected to
            if (pwd == null) {
                throw new CommandIllegalStateException(
                        "Current working directory variable (defined by the key "
                        +Variables.CURRENT_WORKING_DIRECTORY_VARIABLE_NAME
                        +" is not present in the environment context, but expected to be present. "
                        +"See which `Context` instance is provided and how it is created.");
            }
            writer.write(pwd);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            throw new OutputException(e.getMessage());
        }
    }

    /**
     * Sets the input stream for this command.
     * PwdCommand typically doesn't handle input, so this method does nothing.
     *
     * @param newInputStream Ignored by this method.
     */
    @Override
    public void setInputStream(InputStream newInputStream) {
    }

    /**
     * Sets a new output stream for the command.
     *
     * @param newOutputStream The new output stream to use.
     */
    @Override
    public void setOutputStream(OutputStream newOutputStream) {
        this.outputStream = newOutputStream;
    }
}
