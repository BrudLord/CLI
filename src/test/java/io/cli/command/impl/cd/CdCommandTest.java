package io.cli.command.impl.cd;

import io.cli.command.Command;
import io.cli.context.Context;
import io.cli.parser.token.Token;
import io.cli.parser.token.TokenType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class CdCommandTest {
    private final Token cdToken = new Token(TokenType.COMMAND, "cd");
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() {
        outputStream = new ByteArrayOutputStream();
    }

    @Test
    void testCdCommandRelativePath() {
        var pwd = getResource("/commands/cd/dummy.txt").getParent();
        var context = testContext(pwd);

        var args = Arrays.asList(
                cdToken,
                new Token(TokenType.COMMAND, "./inner1/nonexistent/../")
        );

        String expectedOldPwd = pwd.toAbsolutePath().toString();
        String expectedPwd = Paths.get(pwd.toString(), "inner1").toAbsolutePath().toString();

        // we don't check fs for cd command because we work with resource fixtures, not actual files
        Command cdCommand = new CdCommand(args, false, context);
        cdCommand.setOutputStream(outputStream);

        assertDoesNotThrow(cdCommand::execute, "Exit code should be 0 on success");
        assertEquals(expectedOldPwd, context.oldpwd(), "Output should match previous working directory");
        assertEquals(expectedPwd, context.pwd(), "Output should match current working directory");
        assertEquals("", outputStream.toString(), "`cd` output should be empty");
    }

    @Test
    void testCdCommandAbsolutePath() {
        var pwd = getResource("/commands/cd/dummy.txt").getParent();
        var context = testContext(pwd);

        var abspath = pwd.resolve("inner1").toAbsolutePath().toString();
        var args = Arrays.asList(
                cdToken,
                new Token(TokenType.COMMAND, abspath)
        );

        String expectedOldPwd = pwd.toAbsolutePath().toString();

        // we don't check fs for cd command because we work with resource fixtures, not actual files
        Command cdCommand = new CdCommand(args, false, context);
        cdCommand.setOutputStream(outputStream);

        assertDoesNotThrow(cdCommand::execute, "Exit code should be 0 on success");
        assertEquals(expectedOldPwd, context.oldpwd(), "Output should match previous working directory");
        assertEquals(abspath, context.pwd(), "Output should match current working directory");
        assertTrue(outputStream.toString().isEmpty(), "`cd` output should be empty");
    }

    @Test
    void testCdCommandHyphenMinusParam() {
        // we expect the solution to update pwd with the `OLDPWD` and output it,
        // i.e., equivalent to: `cd "$OLDPWD" && pwd`
        // see: https://man7.org/linux/man-pages/man1/cd.1p.html
        var pwd = getResource("/commands/cd/dummy.txt").getParent();
        var context = testContext(pwd);

        var args = Arrays.asList(
                cdToken,
                new Token(TokenType.COMMAND, "-")
        );

        // sequence of changes (pwd, oldpwd), where the initial value is A and `pwd` set by me is B:
        // 1. initial: (A, A)
        // 2. testContext: (B, A)
        // 3. `cd -`: (A, B) and outputs `A`

        // B
        String expectedOldPwd = pwd.toAbsolutePath().toString();
        // A
        String expectedPwd = context.oldpwd();

        // we don't check fs for cd command because we work with resource fixtures, not actual files
        Command cdCommand = new CdCommand(args, false, context);
        cdCommand.setOutputStream(outputStream);

        assertDoesNotThrow(cdCommand::execute, "Exit code should be 0 on success");
        assertEquals(expectedOldPwd, context.oldpwd(), "Output should match previous working directory");
        assertEquals(expectedPwd, context.pwd(), "Output should match current working directory");
        assertEquals(expectedPwd, outputStream.toString().trim(), "`cd` output should contain `PWD`");
    }

    private Context testContext(Path pwd) {
        var context = Context.initial();
        context.pwd(pwd.toAbsolutePath().toString());
        return context;
    }

    private Path getResource(String name) {
        try {
            var url = Objects.requireNonNull(this.getClass().getResource(name)).toURI();
            return Paths.get(url);
        }
        catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}