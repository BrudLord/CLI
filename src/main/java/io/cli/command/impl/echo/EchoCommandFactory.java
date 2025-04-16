package io.cli.command.impl.echo;

import io.cli.command.Command;
import io.cli.command.CommandFactory;
import io.cli.parser.token.Token;

import java.util.List;
import java.util.Optional;

/**
 * Factory class for creating instances of {@link EchoCommand}.
 */
public class EchoCommandFactory implements CommandFactory {
    /**
     * Default constructor for EchoCommandFactory.
     */
    public EchoCommandFactory() {
    }

    private static boolean checkArgs(List<Token> args) {
        if (args.isEmpty()) {
            return false;
        }

        return args.getFirst().getInput().strip().equals("echo");
    }

    /**
     * Creates a new instance of {@link EchoCommand} if the arguments are valid.
     *
     * @param args The list of tokens representing command-line arguments.
     * @return An Optional containing a new `CatCommand` instance if valid, otherwise an empty Optional.
     */
    @Override
    public Optional<Command> newCommand(List<Token> args) {
        if (!checkArgs(args)) {
            return Optional.empty();
        }

        Command command = new EchoCommand(args);
        return Optional.of(command);
    }
}
