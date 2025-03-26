package io.cli.command.impl.echo;

import io.cli.command.Command;
import io.cli.parser.token.Token;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

public class EchoCommand implements Command {
    private final List<Token> args;

    private InputStream inputStream = System.in;
    private OutputStream outputStream = System.out;

    public EchoCommand(List<Token> args) {
        this.args = args;
    }

    @Override
    public int execute() {
        PrintWriter printWriter = new PrintWriter(outputStream);
        for (int i = 1; i < args.size(); i++) {
            printWriter.print(args.get(i).getInput());
            printWriter.print(' ');
        }
        printWriter.println();
        printWriter.flush();
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
