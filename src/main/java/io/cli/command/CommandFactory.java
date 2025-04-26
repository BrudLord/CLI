package io.cli.command;

import io.cli.parser.token.Token;

import java.util.List;
import java.util.Optional;

/**
 * Represents a factory that creates {@code Command} instance based on a list of tokens.
 * This interface is responsible for determining if parsed tokens correspond
 * to a specific command.
 */
public interface CommandFactory {

    /**
     * Creates a new {@code Command} instance if the provided list of tokens matches
     * a command that this factory can produce.
     *
     * @param args A {@code List} of {@code Token} objects representing the arguments
     *             passed to the command in the CLI.
     * @return An {@code Optional} containing the newly created {@code Command} if the
     * tokens are recognized by this factory. If the tokens do not correspond
     * to a command this factory can handle, an empty {@code Optional} is returned.
     */
    Optional<Command> newCommand(List<Token> args);
}
