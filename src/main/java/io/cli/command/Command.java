package io.cli.command;

import io.cli.parsers.token.Token;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public interface Command {
    public Command newInstance(List<Token> tokens);

    public void execute();

    public void setInputStream(InputStream input);

    public void setOutputStream(OutputStream output);
}
