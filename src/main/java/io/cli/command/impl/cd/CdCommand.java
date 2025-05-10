package io.cli.command.impl.cd;

import io.cli.command.Command;
import io.cli.context.Context;
import io.cli.context.Variables;
import io.cli.exception.CommandIllegalStateException;
import io.cli.exception.OutputException;
import io.cli.parser.token.Token;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class CdCommand implements Command {
    private OutputStream outputStream = System.out;
    private final Context context;
    private final List<Token> args;
    private final Boolean checkFs;

    /**
     * Creates {@code CdCommand} instance.
     *
     * @param checkFs tells whether to verify that the resulting pwd exists is a directory
     */
    public CdCommand(List<Token> args, Boolean checkFs, Context context) {
        this.args = args;
        this.checkFs = checkFs;
        this.context = context;
    }

    /**
     * Returns a directory to change to in accordance to the `cd` man page
     * from the args.
     * See: <a href="https://man7.org/linux/man-pages/man1/cd.1p.html">...</a>
     */
    private String getDirectory() throws CommandIllegalStateException {
        var argsDroppedCmdName = args.subList(1, args.size());
        if (argsDroppedCmdName.size() > 1) {
            throw new CommandIllegalStateException(
                    "Invalid number of arguments for `cd` command: "
                            +"expected 0-1, got " + args.size());
        }

        String newDirectory;

        // Step 1-2: Handle no directory argument case
        if (argsDroppedCmdName.isEmpty()) {
            String home = System.getenv("HOME");
            if (home == null || home.isEmpty()) {
                // Step 1: HOME is empty or undefined - use system property as implementation-defined behavior
                newDirectory = System.getProperty("user.home");
            }
            else {
                // Step 2: Use HOME environment variable
                newDirectory = home;
            }
        }
        else {
            newDirectory = argsDroppedCmdName.getFirst().getInput();
        }

        // handle the case of `cd -` command
        if (newDirectory.equals("-")) {
            // equivalent to executing: `cd "$OLDPWD" && pwd`
            String oldPwd = context.oldpwd();
            if (oldPwd == null || oldPwd.isEmpty()) {
                throw new CommandIllegalStateException("cd: " + Variables.PREVIOUS_WORKING_DIRECTORY_VARIABLE_NAME + " not set");
            }

            // update the directory to OLDPWD and do printing
            newDirectory = oldPwd;
            writeToOutput(newDirectory);
        }
        else {
            // if the new directory is relative to pwd,
            // then update it an absolute path
            if (!Paths.get(newDirectory).isAbsolute()) {
                String pwd = context.pwd();
                newDirectory = Paths.get(pwd, newDirectory).normalize().toAbsolutePath().toString();
            }
        }

        // check whether it exists and is a directory
        var path = Paths.get(newDirectory);

        if (checkFs) {
            if (!Files.exists(path)) {
                throw new CommandIllegalStateException("cd: " + path + ": No such file or directory");
            }
            else if (!Files.isDirectory(path)) {
                throw new CommandIllegalStateException("cd: " + path + ": Not a directory");
            }
        }

        // normalize the resulting absolute path
        newDirectory = path.normalize().toString();

        return newDirectory;
    }

    @Override
    public void execute() {
        // get the directory from arguments
        String newDirectory = getDirectory();
        // update PWD in env context
        context.pwd(newDirectory);
        context.setSuccessfulStatusCode();
    }

    private void writeToOutput(String value) {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
        try {
            writer.write(value);
            writer.newLine();
            writer.flush();
        }
        catch (IOException e) {
            throw new OutputException(e.getMessage());
        }
    }

    /**
     * No-op for this command.
     *
     * @param newInputStream The {@code InputStream} to be used by the command.
     */
    @Override
    public void setInputStream(InputStream newInputStream) {}

    @Override
    public void setOutputStream(OutputStream newOutputStream) {
        this.outputStream = newOutputStream;
    }
}
