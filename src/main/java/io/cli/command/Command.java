package io.cli.command;

import io.cli.parsers.token.Token;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;

public interface Command {
    Optional<Command> newInstance(List<Token> tokens);

    void execute();

    void setInputStream(InputStream input);

    void setOutputStream(OutputStream output);
}
