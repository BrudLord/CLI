package io.cli;

import io.cli.command.Command;
import io.cli.command.CommandFactory;
import io.cli.executor.Executor;
import io.cli.parser.ParserOrchestrator;
import io.cli.parser.token.Token;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class MainOrchestrator {
    private final ParserOrchestrator parserOrchestrator;
    private final List<CommandFactory> commandFactories;
    private final Executor executor;

    public MainOrchestrator(
            ParserOrchestrator parserOrchestrator,
            List<CommandFactory> commandFactories,
            Executor executor
    ) {
        this.parserOrchestrator = parserOrchestrator;
        this.commandFactories = commandFactories;
        this.executor = executor;
    }

    public void processInput(String input) throws IOException {
        List<List<Token>> parsedTokens = parserOrchestrator.parse(input);
        List<Command> commands = parsedTokens.stream().map(this::createCommand).toList();
        executor.pipeAndExecuteCommands(commands);
    }

    private Command createCommand(List<Token> tokens) {
        return commandFactories.stream()
                .map(commandFactory -> commandFactory.newCommand(tokens))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst()
                .orElseThrow();
    }
}
