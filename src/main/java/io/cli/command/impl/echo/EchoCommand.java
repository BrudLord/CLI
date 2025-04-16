package io.cli.command.impl.echo;

import io.cli.command.Command;
import io.cli.exception.InputException;
import io.cli.exception.InvalidOptionException;
import io.cli.parser.token.Token;

import java.io.*;
import java.util.List;

public class EchoCommand implements Command {
    private final List<Token> args;

    private OutputStream outputStream = System.out;

    public EchoCommand(List<Token> args) {
        this.args = args;
    }

    /**
     * Executes the `echo` command.
     * - The command prints the arguments passed to it.
     *
     * @return 0 on success, 1 on error.
     */
    @Override
    public int execute() {
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

            return 0;

        } catch (IOException e) {
            throw new InputException(e.getMessage());
        }
    }

    /**
     * Sets a new input stream for the command.
     *
     * @param newInputStream The new input stream to use.
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
