package io.cli.command.impl.pwd;

import io.cli.command.Command;
import io.cli.command.CommandFactory;
import io.cli.context.Context;
import io.cli.parser.token.Token;

import java.util.List;
import java.util.Optional;

/**
 * Factory class for creating instances of {@link PwdCommand}.
 */
public class PwdCommandFactory implements CommandFactory {
    private final Context context;

    /**
     * Default constructor for PwdCommandFactory.
     */
    public PwdCommandFactory(Context context) {
        this.context = context;
    }

    private static boolean checkArgs(List<Token> args) {
        return !args.isEmpty() && "pwd".equals(args.getFirst().getInput());
    }

    /**
     * Creates a new instance of {@link PwdCommand} if the arguments are valid.
     *
     * @param args The list of tokens representing command-line arguments.
     * @return An Optional containing a new `PwdCommand` instance if valid, otherwise an empty Optional.
     */
    @Override
    public Optional<Command> newCommand(List<Token> args) {
        if (!checkArgs(args)) {
            return Optional.empty();
        }
        return Optional.of(new PwdCommand(context));
    }
}
