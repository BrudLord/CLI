package io.cli.command.impl.assign;

import io.cli.command.Command;
import io.cli.context.Context;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Implements the {@code Command} interface to handle variable assignment.
 * This command takes a key-value pair and stores it in the provided {@code Context}.
 */
public class AssignCommand implements Command {
    private final Context context;
    private final String key;
    private final String value;

    /**
     * Constructs an {@code AssignCommand} with the specified context, key, and value.
     *
     * @param context The {@code Context} where the variable will be stored.
     * @param key     The name of the variable to assign.
     * @param value   The value to assign to the variable.
     */
    public AssignCommand(Context context, String key, String value) {
        this.context = context;
        this.key = key;
        this.value = value;
    }

    /**
     * Executes the assignment command. It sets the given key-value pair in the
     * associated {@code Context}.
     *
     * @return 0, indicating successful execution.
     */
    @Override
    public int execute() {
        context.setVar(key, value);
        return 0;
    }

    /**
     * Sets the input stream for this command.
     * Assignment typically doesn't handle input, so this method does nothing.
     *
     * @param newInputStream Ignored by this method.
     */
    @Override
    public void setInputStream(InputStream newInputStream) {
    }

    /**
     * Sets the output stream for this command.
     * Assignment typically doesn't produce output, so this method does nothing.
     *
     * @param newOutputStream Ignored by this method.
     */
    @Override
    public void setOutputStream(OutputStream newOutputStream) {
    }
}
