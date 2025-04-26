package io.cli;

import io.cli.command.Command;
import io.cli.command.CommandFactory;
import io.cli.executor.Executor;
import io.cli.parser.ParserOrchestrator;
import io.cli.parser.token.Token;

import java.util.List;
import java.util.Optional;

/**
 * MainOrchestrator orchestrates the process of parsing input, creating commands, and executing them.
 */
public class MainOrchestrator {
    private final ParserOrchestrator parserOrchestrator;
    private final List<CommandFactory> commandFactories;
    private final Executor executor;

    /**
     * Constructs a MainOrchestrator with the necessary components.
     *
     * @param parserOrchestrator The parser orchestrator responsible for parsing input.
     * @param commandFactories   A list of command factories to create commands from tokens.
     * @param executor           The executor responsible for executing the commands.
     */
    public MainOrchestrator(
            ParserOrchestrator parserOrchestrator,
            List<CommandFactory> commandFactories,
            Executor executor
    ) {
        this.parserOrchestrator = parserOrchestrator;
        this.commandFactories = commandFactories;
        this.executor = executor;
    }

    /**
     * Processes the input string by parsing it, creating commands, and executing them.
     *
     * @param input The input string to process.
     */
    public void processInput(String input) {
        // Parse the input string into groups of tokens.
        List<List<Token>> parsedTokens = parserOrchestrator.parse(input);

        // Create commands from the parsed tokens.
        List<Command> commands = parsedTokens.stream()
                .map(this::createCommand)
                .toList();

        // Execute the commands using the executor.
        executor.pipeAndExecuteCommands(commands);
    }

    /**
     * Creates a Command instance from a list of tokens using the available command factories.
     *
     * @param tokens The list of tokens representing a command.
     * @return The created Command.
     * @throws IllegalStateException if no command factory can create a command from the tokens.
     */
    private Command createCommand(List<Token> tokens) {
        // Iterate over command factories to find one that can create a command from the tokens.
        return commandFactories.stream()
                .map(commandFactory -> commandFactory.newCommand(tokens)) // Attempt to create a command.
                .filter(Optional::isPresent) // Keep only successfully created commands.
                .map(Optional::get) // Extract the actual Command from the Optional.
                .findFirst() // Get the first valid Command.
                .orElseThrow(() -> new IllegalStateException("No suitable CommandFactory found for tokens: " + tokens));
    }
}
