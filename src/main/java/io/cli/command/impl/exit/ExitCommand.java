package io.cli.command.impl.exit;

import io.cli.command.Command;
import io.cli.context.Context;
import io.cli.parser.token.Token;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class ExitCommand implements Command {
    private final Context context;
    private final List<Token> args;

    private InputStream inputStream = System.in;
    private OutputStream outputStream = System.out;

    public ExitCommand(Context context, List<Token> args) {
        this.context = context;
        this.args = args;
    }

    @Override
    public void execute() {
        System.exit(0);
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
