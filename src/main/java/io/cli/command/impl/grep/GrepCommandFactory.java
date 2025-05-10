package io.cli.command.impl.grep;

import io.cli.command.Command;
import io.cli.command.CommandFactory;
import io.cli.context.Context;
import io.cli.fs.PathFsApi;
import io.cli.parser.token.Token;

import java.util.List;
import java.util.Optional;

/**
 * Factory class for creating instances of {@link GrepCommand}.
 */
public class GrepCommandFactory implements CommandFactory {
    private final PathFsApi fs;
    private final Context context;

    /**
     * Default constructor for GrepCommandFactory.
     */
    public GrepCommandFactory(PathFsApi fs, Context context) {
        this.fs = fs;
        this.context = context;
    }

    private static boolean checkArgs(List<Token> args) {
        return !args.isEmpty() && "grep".equals(args.getFirst().getInput());
    }

    /**
     * Creates a new instance of {@link GrepCommand} if the arguments are valid.
     *
     * @param args The list of tokens representing command-line arguments.
     * @return An Optional containing a new `GrepCommand` instance if valid, otherwise an empty Optional.
     */
    @Override
    public Optional<Command> newCommand(List<Token> args) {
        if (!checkArgs(args)) {
            return Optional.empty();
        }
        var grep = new GrepCommand(args, fs, context);
        return Optional.of(grep);
    }
}
