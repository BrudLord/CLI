package io.cli.command.impl.pwd;

import io.cli.command.Command;
import io.cli.exception.ExitException;
import io.cli.parser.token.Token;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

public class PwdCommand implements Command {
    private final List<Token> args;

    private InputStream inputStream = System.in;
    private OutputStream outputStream = System.out;

    public PwdCommand(List<Token> args) {
        this.args = args;
    }

    @Override
    public int execute() {
        PrintWriter printWriter = new PrintWriter(outputStream);
        String currentDirectory = System.getProperty("user.dir");
        printWriter.println(currentDirectory);
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
