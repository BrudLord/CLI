package io.cli;

import io.cli.command.CommandFactory;
import io.cli.command.impl.assign.AssignCommandFactory;
import io.cli.command.impl.cat.CatCommandFactory;
import io.cli.command.impl.echo.EchoCommandFactory;
import io.cli.command.impl.exit.ExitCommandFactory;
import io.cli.command.impl.external.ExternalCommandFactory;
import io.cli.command.impl.grep.GrepCommandFactory;
import io.cli.command.impl.pwd.PwdCommandFactory;
import io.cli.command.impl.wc.WcCommandFactory;
import io.cli.context.Context;
import io.cli.exception.CLIException;
import io.cli.exception.ExitException;
import io.cli.executor.Executor;
import io.cli.parser.ParserOrchestrator;
import io.cli.parser.innerparser.PipeParser;
import io.cli.parser.innerparser.QuoteParser;
import io.cli.parser.innerparser.Substitutor;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Main class for running the CLI application.
 */
public final class Main {
    private Main() {
    }

    /**
     * Entry point for the CLI application.
     * Initializes the necessary components and starts the main input loop.
     *
     * @param args Command-line arguments (not used in this implementation).
     */
    public static void main(String[] args) {
        Context context = new Context(); // Shared context for variables and state.
        MainOrchestrator mainOrchestrator = getMainOrchestrator(context); // Setup main orchestrator.

        // Use try-with-resources to ensure Scanner is closed properly.
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                try {
                    System.out.print("> "); // Prompt for input.

                    String input;
                    try {
                        input = scanner.nextLine();
                    } catch (NoSuchElementException ignored) {
                        // Handle case where input stream is closed.
                        break;
                    }

                    if (scanner.ioException() != null) {
                        throw scanner.ioException(); // Handle IO exceptions from Scanner.
                    }

                    // Process the input using the orchestrator.
                    mainOrchestrator.processInput(input);

                } catch (CLIException e) {
                    // Handle custom CLI exceptions, display error messages, and update exit code variable.
                    System.out.println(e.getMessage());

                    if (e instanceof ExitException) {
                        // Break the loop on exit command.
                        break;
                    }
                }
            }
        } catch (Throwable e) {
            // Catch-all for unexpected errors.
            System.out.println("Unexpected error: " + e.getMessage());
        }
    }

    /**
     * Sets up and returns the MainOrchestrator with all necessary components.
     *
     * @param context The shared context for the application.
     * @return A fully configured MainOrchestrator instance.
     */
    public static MainOrchestrator getMainOrchestrator(Context context) {
        // Initialize parsers for handling pipes, quotes, and substitutions.
        PipeParser pipeParser = new PipeParser();
        QuoteParser quoteParser = new QuoteParser();
        Substitutor substitutor = new Substitutor();

        // Set up the parser orchestrator with the parsers and shared context.
        ParserOrchestrator parserOrchestrator = new ParserOrchestrator(pipeParser, quoteParser, substitutor, context);

        // Define the available command factories.
        List<CommandFactory> commandFactories = List.of(
                new AssignCommandFactory(context),
                new CatCommandFactory(),
                new EchoCommandFactory(),
                new ExitCommandFactory(),
                new GrepCommandFactory(),
                new PwdCommandFactory(),
                new WcCommandFactory(),
                new ExternalCommandFactory(context) // ExternalCommandFactory must be last.
        );

        // Initialize the executor for command execution.
        Executor executor = new Executor(context);

        // Return a new MainOrchestrator instance.
        return new MainOrchestrator(parserOrchestrator, commandFactories, executor);
    }
}
