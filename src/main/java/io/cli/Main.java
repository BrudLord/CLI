package io.cli;

import io.cli.command.CommandFactory;
import io.cli.command.impl.assign.AssignCommandFactory;
import io.cli.command.impl.cat.CatCommandFactory;
import io.cli.command.impl.echo.EchoCommandFactory;
import io.cli.command.impl.exit.ExitCommandFactory;
import io.cli.command.impl.external.ExternalCommandFactory;
import io.cli.command.impl.pwd.GrepCommandFactory;
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

public class Main {
    public static void main(String[] args) {
        Context context = new Context();
        MainOrchestrator mainOrchestrator = getMainOrchestrator(context);

        try (Scanner scanner = new Scanner(System.in)) {

            while (true) {
                try {

                    System.out.print("> ");

                    String input;
                    try {
                        input = scanner.nextLine();
                    } catch (NoSuchElementException ignored) {
                        // System.in is closed so stop the loop and exit
                        break;
                    }

                    if (scanner.ioException() != null) {
                        throw scanner.ioException();
                    }

                    mainOrchestrator.processInput(input);

                } catch (CLIException e) {
                    System.out.println(e.getMessage());
                    context.setVar("?", Integer.toString(e.getExitCode()));

                    if (e instanceof ExitException) {
                        break;
                    }
                }
            }

        } catch (Throwable e) {
            System.out.println(e.getMessage());
        }
    }

    private static MainOrchestrator getMainOrchestrator(Context context) {
        PipeParser pipeParser = new PipeParser();
        QuoteParser quoteParser = new QuoteParser();
        Substitutor substitutor = new Substitutor();

        ParserOrchestrator parserOrchestrator = new ParserOrchestrator(pipeParser, quoteParser, substitutor, context);

        List<CommandFactory> commandFactories = List.of(
                new AssignCommandFactory(context),
                new CatCommandFactory(),
                new EchoCommandFactory(),
                new ExitCommandFactory(),
                new GrepCommandFactory(),
                new PwdCommandFactory(),
                new WcCommandFactory(),
                new ExternalCommandFactory(context)  // External command always has to be the last
        );

        Executor executor = new Executor(context);

        return new MainOrchestrator(parserOrchestrator, commandFactories, executor);
    }
}