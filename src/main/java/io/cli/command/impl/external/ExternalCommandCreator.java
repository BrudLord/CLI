package io.cli.command.impl.external;

import io.cli.command.Command;
import io.cli.command.CommandCreator;
import io.cli.context.Context;
import io.cli.parser.token.Token;

import java.util.List;
import java.util.Optional;

public class ExternalCommandCreator implements CommandCreator {
    private final Context context;

    public ExternalCommandCreator(Context context) {
        this.context = context;
    }

    @Override
    public Optional<Command> newCommand(List<Token> args) {
        return Optional.of(new ExternalCommand(context, args.stream().map(Token::getInput).toList()));
    }
}
