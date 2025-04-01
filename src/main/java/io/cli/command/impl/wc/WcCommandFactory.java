package io.cli.command.impl.wc;

import io.cli.command.Command;
import io.cli.command.CommandFactory;
import io.cli.parser.token.Token;

import java.util.List;
import java.util.Optional;

/**
 * Factory class for creating instances of {@link WcCommand}.
 */
public class WcCommandFactory implements CommandFactory {
    /**
     * Default constructor for WcCommandFactory.
     */
    public WcCommandFactory() {
    }

    private static boolean checkArgs(List<Token> args) {
        if (args.isEmpty()) {
            return false;
        }
        return args.getFirst().getInput().equals("wc");
    }

    /**
     * Creates a new instance of {@link WcCommand} if the arguments are valid.
     *
     * @param args The list of tokens representing command-line arguments.
     * @return An Optional containing a new `WcCommand` instance if valid, otherwise an empty Optional.
     */
    @Override
    public Optional<Command> newCommand(List<Token> args) {
        if (!checkArgs(args)) {
            return Optional.empty();
        }

        Command command = new WcCommand(args);
        return Optional.of(command);
    }
}