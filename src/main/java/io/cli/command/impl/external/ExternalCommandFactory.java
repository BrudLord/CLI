package io.cli.command.impl.external;

import io.cli.command.Command;
import io.cli.command.CommandFactory;
import io.cli.context.Context;
import io.cli.parser.token.Token;

import java.util.List;
import java.util.Optional;

public class ExternalCommandFactory implements CommandFactory {
    private final Context context;

    /**
     * Constructs an {@code ExternalCommandFactory} with the given {@code Context}.
     *
     * @param context The {@code Context} that will be passed to created {@code ExternalCommand} instances.
     */
    public ExternalCommandFactory(Context context) {
        this.context = context;
    }

    @Override
    public Optional<Command> newCommand(List<Token> args) {
        return Optional.of(new ExternalCommand(context, args.stream().map(Token::getInput).toList()));
    }
}
