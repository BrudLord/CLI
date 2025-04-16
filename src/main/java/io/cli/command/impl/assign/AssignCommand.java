package io.cli.command.impl.assign;

import io.cli.command.Command;
import io.cli.context.Context;

import java.io.InputStream;
import java.io.OutputStream;

public class AssignCommand implements Command {
    private final Context context;
    private final String key;
    private final String value;

    private InputStream inputStream = System.in;
    private OutputStream outputStream = System.out;

    public AssignCommand(Context context, String key, String value) {
        this.context = context;
        this.key = key;
        this.value = value;
    }

    @Override
    public int execute() {
        context.setVar(key, value);
        return 0;
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
