package io.cli.command.impl.assign;

import io.cli.command.Command;
import io.cli.command.CommandFactory;
import io.cli.context.Context;
import io.cli.parser.token.Token;

import java.util.List;
import java.util.Optional;

/**
 * Implements the {@code CommandFactory} interface to create {@code AssignCommand} instances.
 * This factory parses a list of tokens to identify assignment commands of the form
 * {@code variable=value}.
 */
public class AssignCommandFactory implements CommandFactory {
    private static final String assignCommandRegex = "^[a-zA-Z_][a-zA-Z0-9_]*=[^=]*$";
    private final Context context;

    /**
     * Constructs an {@code AssignCommandFactory} with the given {@code Context}.
     *
     * @param context The {@code Context} that will be passed to created {@code AssignCommand} instances.
     */
    public AssignCommandFactory(Context context) {
        this.context = context;
    }

    /**
     * Creates an {@code AssignCommand} if the provided list of tokens represents a
     * valid assignment command. The expected format is a single token like {@code key=value}
     * that matches the following regular expression: {@code ^[a-zA-Z_][a-zA-Z0-9_]*=[^=]*$}.
     *
     * @param args A {@code List} of {@code Token} objects. The first token is expected
     *             to contain the assignment in the format {@code key=value}. Subsequent
     *             tokens are appended to the value.
     * @return An {@code Optional} containing a new {@code AssignCommand} if the tokens
     * represent a valid assignment, otherwise an empty {@code Optional}.
     */
    @Override
    public Optional<Command> newCommand(List<Token> args) {
        if (args.size() != 1) {
            return Optional.empty();
        }

        String token = args.getFirst().getInput();

        if (!token.matches(assignCommandRegex)) {
            return Optional.empty();
        }

        String[] keyValuePair = token.split("=");

        String key = keyValuePair[0];
        String value = keyValuePair[1];

        return Optional.of(new AssignCommand(context, key, value));
    }
}