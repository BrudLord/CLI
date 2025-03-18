package io.cli.command.impl.exit;

import io.cli.command.Command;
import io.cli.command.CommandCreator;
import io.cli.context.Context;
import io.cli.parser.token.Token;
import io.cli.parser.token.TokenType;

import java.util.List;
import java.util.Optional;

public class ExitCommandCreator implements CommandCreator {
    private final Context context;

    public ExitCommandCreator(Context context) {
        this.context = context;
    }

    private static boolean checkArgs(List<Token> args) {
        if (args.size() != 1) {
            return false;
        }

        Token arg = args.getFirst();
        return arg.getType() != TokenType.COMMAND || !arg.getCommand().equals("exit");
    }

    @Override
    public Optional<Command> newCommand(List<Token> args) {
        if (!checkArgs(args)) {
            return Optional.empty();
        }

        Command command = new ExitCommand(context, args);
        return Optional.of(command);
    }
}
