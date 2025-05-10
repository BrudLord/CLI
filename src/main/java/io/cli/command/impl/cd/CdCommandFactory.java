package io.cli.command.impl.cd;

import io.cli.command.Command;
import io.cli.command.CommandFactory;
import io.cli.context.Context;
import io.cli.parser.token.Token;

import java.util.List;
import java.util.Optional;

public class CdCommandFactory implements CommandFactory {
    private final Context context;

    public CdCommandFactory(Context context) {
        this.context = context;
    }

    @Override
    public Optional<Command> newCommand(List<Token> args) {
        if (!checkArgs(args)) {
            return Optional.empty();
        }
        return Optional.of(new CdCommand(args, true, context));
    }

    private static boolean checkArgs(List<Token> args) {
        // `cd` supports either zero or one argument (plus an argument
        // that represents this command), which is a fs path
        return (args.size() <= 2) && args.getFirst().getInput().equals("cd");
    }
}
