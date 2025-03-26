package io.cli;

import io.cli.command.CommandFactory;
import io.cli.command.impl.assign.AssignCommandFactory;
import io.cli.command.impl.exit.ExitCommandFactory;
import io.cli.command.impl.external.ExternalCommandFactory;
import io.cli.context.Context;
import io.cli.exception.ExitException;
import io.cli.executor.Executor;
import io.cli.parser.ParserOrchestrator;
import io.cli.parser.innerparser.PipeParser;
import io.cli.parser.innerparser.QuoteParser;
import io.cli.parser.innerparser.Substitutor;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Context context = new Context();
        MainOrchestrator mainOrchestrator = getMainOrchestrator(context);

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                try {
                    System.out.print("> ");
                    String input = scanner.nextLine();
                    mainOrchestrator.processInput(input);
                } catch (ExitException e) {
                    System.out.println(e.getMessage());
                    break;
                } catch (Throwable e) {
                    e.printStackTrace(System.err);
                }
            }
        }
    }

    private static MainOrchestrator getMainOrchestrator(Context context) {
        PipeParser pipeParser = new PipeParser();
        QuoteParser quoteParser = new QuoteParser();
        Substitutor substitutor = new Substitutor();

        ParserOrchestrator parserOrchestrator = new ParserOrchestrator(pipeParser, quoteParser, substitutor, context);

        List<CommandFactory> commandFactories = List.of(
                new AssignCommandFactory(context),
                new ExitCommandFactory(),
                new ExternalCommandFactory(context)  // External
        );

        Executor executor = new Executor(context);

        return new MainOrchestrator(parserOrchestrator, commandFactories, executor);
    }
}