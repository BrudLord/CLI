package io.cli.command.impl.pwd;

import io.cli.command.Command;
import io.cli.command.CommandFactory;
import io.cli.parser.token.Token;

import java.util.List;
import java.util.Optional;

public class PwdCommandFactory implements CommandFactory {
    public PwdCommandFactory() {
    }

    private static boolean checkArgs(List<Token> args) {
        if (args.isEmpty()) {
            return false;
        }

        return args.getFirst().getInput().equals("pwd");
    }

    @Override
    public Optional<Command> newCommand(List<Token> args) {
        if (!checkArgs(args)) {
            return Optional.empty();
        }

        Command command = new PwdCommand(args);
        return Optional.of(command);
    }
}
