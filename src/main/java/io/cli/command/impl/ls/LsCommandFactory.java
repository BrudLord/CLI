package io.cli.command.impl.ls;

import io.cli.command.Command;
import io.cli.command.CommandFactory;
import io.cli.context.Context;
import io.cli.fs.PathFsApi;
import io.cli.parser.token.Token;

import java.util.List;
import java.util.Optional;

public class LsCommandFactory implements CommandFactory {
    private final PathFsApi fs;
    private final Context context;

    public LsCommandFactory(PathFsApi fs, Context context) {
        this.fs = fs;
        this.context = context;
    }

    private static boolean checkArgs(List<Token> args) {
        // `ls` supports either zero or one argument (plus an argument
        // that represents this command), which is a fs path
        return (args.size() <= 2) && args.getFirst().getInput().equals("ls");
    }

    @Override
    public Optional<Command> newCommand(List<Token> args) {
        if (!checkArgs(args)) {
            return Optional.empty();
        }
        return Optional.of(new LsCommand(args, fs, context));
    }
}
