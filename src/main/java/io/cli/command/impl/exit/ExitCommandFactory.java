package io.cli.command.impl.exit;

import io.cli.command.Command;
import io.cli.command.CommandFactory;
import io.cli.parser.token.Token;

import java.util.List;
import java.util.Optional;

public class ExitCommandFactory implements CommandFactory {
    public ExitCommandFactory() {
    }

    private static boolean checkArgs(List<Token> args) {
        if (args.size() != 1) {
            return false;
        }

        return args.getFirst().getInput().equals("exit");
    }

    @Override
    public Optional<Command> newCommand(List<Token> args) {
        if (!checkArgs(args)) {
            return Optional.empty();
        }

        Command command = new ExitCommand(args);
        return Optional.of(command);
    }
}
