package io.cli.command.impl.echo;

import io.cli.command.Command;
import io.cli.exception.InputException;
import io.cli.exception.InvalidOptionException;
import io.cli.parser.token.Token;

import java.io.*;
import java.util.List;

/**
 * The EchoCommand class implements the `echo` command, which takes its arguments
 * and writes them to standard output.
 */
public class EchoCommand implements Command {
    private final List<Token> args;

    private OutputStream outputStream = System.out;

    /**
     * Constructs an EchoCommand instance with the given list of arguments.
     *
     * @param args The list of tokens representing command-line arguments.
     */
    public EchoCommand(List<Token> args) {
        this.args = args;
    }

    /**
     * Executes the {@code echo} command: prints the arguments passed to it.
     */
    @Override
    public void execute() {
        for (var arg : args) {
            if (arg.getInput().startsWith("-")) {
                throw new InvalidOptionException(arg.getInput());
            }
        }

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
        try {

            List<String> words = args.stream().skip(1).map(Token::getInput).toList();
            String output = String.join(" ", words);

            writer.write(output);
            writer.newLine();
            writer.flush();

        } catch (IOException e) {
            throw new InputException(e.getMessage());
        }
    }

    /**
     * Sets the input stream for this command.
     * PwdCommand typically doesn't handle input, so this method does nothing.
     *
     * @param newInputStream Ignored by this method.
     */
    @Override
    public void setInputStream(InputStream newInputStream) {
    }


    /**
     * Sets a new output stream for the command.
     *
     * @param newOutputStream The new output stream to use.
     */
    @Override
    public void setOutputStream(OutputStream newOutputStream) {
        this.outputStream = newOutputStream;
    }
}
