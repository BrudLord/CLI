package io.cli.command.impl.exit;

import io.cli.command.Command;
import io.cli.exception.ExitException;
import io.cli.parser.token.Token;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class ExitCommand implements Command {
    private final List<Token> args;

    private InputStream inputStream = System.in;
    private OutputStream outputStream = System.out;

    public ExitCommand(List<Token> args) {
        this.args = args;
    }

    @Override
    public int execute() {
        int exitCode = 0;
        if (args.size() == 2) {
            exitCode = Integer.parseInt(args.get(1).getInput());
        }
        throw new ExitException(exitCode);
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
