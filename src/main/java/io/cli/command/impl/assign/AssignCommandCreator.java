package io.cli.command.impl.assign;

import io.cli.command.Command;
import io.cli.command.CommandCreator;
import io.cli.context.Context;
import io.cli.parser.token.Token;

import java.util.List;
import java.util.Optional;

public class AssignCommandCreator implements CommandCreator {
    private final Context context;

    public AssignCommandCreator(Context context) {
        this.context = context;
    }

    @Override
    public Optional<Command> newCommand(List<Token> args) {
        if (args.isEmpty()) {
            return Optional.empty();
        }

        String assignment = args.getFirst().getCommand();

        if (assignment.isEmpty() || !Character.isAlphabetic(assignment.charAt(0))) {
            return Optional.empty();
        }

        String key = null;
        StringBuilder keyBuilder = new StringBuilder();
        keyBuilder.append(assignment.charAt(0));

        StringBuilder valueBuilder = new StringBuilder();

        for (int i = 1; i < assignment.length(); i++) {
            char currentChar = assignment.charAt(i);
            if (Character.isAlphabetic(currentChar) || Character.isDigit(currentChar)) {
                keyBuilder.append(currentChar);
            } else if (currentChar == '=') {
                key = keyBuilder.toString();
                valueBuilder.append(assignment.substring(i + 1));
                break;
            } else {
                return Optional.empty();
            }
        }

        if (key == null) {
            return Optional.empty();
        }

        for (int i = 1; i < args.size(); i++) {
            valueBuilder.append(args.get(i).getCommand());
        }

        String value = valueBuilder.toString();
        AssignCommand assignCommand = new AssignCommand(context, key, value);

        return Optional.of(assignCommand);
    }
}
