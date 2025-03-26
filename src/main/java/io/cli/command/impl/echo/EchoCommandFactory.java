package io.cli.command.impl.echo;

import io.cli.command.Command;
import io.cli.command.CommandFactory;
import io.cli.parser.token.Token;

import java.util.List;
import java.util.Optional;

public class EchoCommandFactory implements CommandFactory {
    public EchoCommandFactory() {
    }

    private static boolean checkArgs(List<Token> args) {
        if (args.isEmpty()) {
            return false;
        }

        return args.getFirst().getInput().strip().equals("echo");
    }

    @Override
    public Optional<Command> newCommand(List<Token> args) {
        if (!checkArgs(args)) {
            return Optional.empty();
        }

        Command command = new EchoCommand(args);
        return Optional.of(command);
    }
}
