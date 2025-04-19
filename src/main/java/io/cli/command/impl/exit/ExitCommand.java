package io.cli.command.impl.exit;

import io.cli.command.Command;
import io.cli.exception.ExitException;
import io.cli.parser.token.Token;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public final class ExitCommand implements Command {
    private final List<Token> args;

    /**
     * Constructs an ExitCommand instance with the given list of arguments.
     *
     * @param args The list of tokens representing command-line arguments.
     */
    public ExitCommand(List<Token> args) {
        this.args = args;
    }

    @Override
    public void execute() {
        int exitCode = 0;
        if (args.size() == 2) {
            exitCode = Integer.parseInt(args.get(1).getInput());
        }
        throw new ExitException(exitCode);
    }

    @Override
    public void setInputStream(InputStream newInputStream) {
    }

    @Override
    public void setOutputStream(OutputStream newOutputStream) {
    }
}
