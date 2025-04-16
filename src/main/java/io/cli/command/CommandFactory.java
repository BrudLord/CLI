package io.cli.command;

import io.cli.parser.token.Token;

import java.util.List;
import java.util.Optional;

public interface CommandFactory {
    Optional<Command> newCommand(List<Token> args);
}
