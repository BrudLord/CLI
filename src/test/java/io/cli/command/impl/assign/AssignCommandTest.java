package io.cli.command.impl.assign;

import io.cli.command.Command;
import io.cli.command.impl.external.ExternalCommand;
import io.cli.command.impl.external.ExternalCommandCreator;
import io.cli.context.Context;
import io.cli.parser.ParserOrchestrator;
import io.cli.parser.innerparser.PipeParser;
import io.cli.parser.innerparser.QuoteParser;
import io.cli.parser.innerparser.Substitutor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class AssignCommandTest {
    private ParserOrchestrator parserOrchestrator;
    private Context context;

    @BeforeEach
    void setUp() {
        context = new Context();
        parserOrchestrator = new ParserOrchestrator(
                List.of(
                        new AssignCommandCreator(context),
                        new ExternalCommandCreator(context)
                ),
                new PipeParser(),
                new QuoteParser(),
                new Substitutor()
        );
    }

    @Test
    void testValidSimple() {
        String key = "var";
        String value = "simple_value";

        String commandStr = String.format("%s=%s", key, value);
        List<Command> commands = parserOrchestrator.parse(commandStr, new Context());

        Assertions.assertEquals(1, commands.size());
        Assertions.assertInstanceOf(AssignCommand.class, commands.getFirst());

        Assertions.assertEquals(0, commands.getFirst().execute());

        Assertions.assertEquals("simple_value", context.getVar("var"));
    }

    @Test
    void testValidWithSpaces() {
        String key = "my_var";
        String value = "\"Hello world\"";

        String commandStr = String.format("%s=%s", key, value);
        List<Command> commands = parserOrchestrator.parse(commandStr, new Context());

        Assertions.assertEquals(1, commands.size());
        Assertions.assertInstanceOf(AssignCommand.class, commands.getFirst());

        Assertions.assertEquals(0, commands.getFirst().execute());

        Assertions.assertEquals("Hello world", context.getVar("my_var"));
    }

    @Test
    void testValidWithSpecialChars() {
        String key = "var_123";
        String value = "\"!@#$%^&*()_+{}[]\"";

        String commandStr = String.format("%s=%s", key, value);
        List<Command> commands = parserOrchestrator.parse(commandStr, new Context());

        Assertions.assertEquals(1, commands.size());
        Assertions.assertInstanceOf(AssignCommand.class, commands.getFirst());

        Assertions.assertEquals(0, commands.getFirst().execute());

        Assertions.assertEquals("!@#$%^&*()_+{}[]", context.getVar("var_123"));
    }

    @Test
    void testValidWithEmptyValue() {
        String key = "empty_var";
        String value = "";

        String commandStr = String.format("%s=%s", key, value);
        List<Command> commands = parserOrchestrator.parse(commandStr, new Context());

        Assertions.assertEquals(1, commands.size());
        Assertions.assertInstanceOf(AssignCommand.class, commands.getFirst());

        Assertions.assertEquals(0, commands.getFirst().execute());

        Assertions.assertEquals("", context.getVar("empty_var"));
    }

    @Test
    void testInvalidStartingWithNumber() {
        String key = "2invalid_var";
        String value = "value";

        String commandStr = String.format("%s=%s", key, value);
        List<Command> commands = parserOrchestrator.parse(commandStr, new Context());

        Assertions.assertEquals(1, commands.size());
        Assertions.assertInstanceOf(ExternalCommand.class, commands.getFirst());

        Assertions.assertNull(context.getVar(key));
    }

    @Test
    void testInvalidWithDash() {
        String key = "invalid-var";
        String value = "test";

        String commandStr = String.format("%s=%s", key, value);
        List<Command> commands = parserOrchestrator.parse(commandStr, new Context());

        Assertions.assertEquals(1, commands.size());
        Assertions.assertInstanceOf(ExternalCommand.class, commands.getFirst());

        Assertions.assertNull(context.getVar(key));
    }

    @Test
    void testInvalidWithOnlyEquals() {
        String commandStr = "=";
        List<Command> commands = parserOrchestrator.parse(commandStr, new Context());

        Assertions.assertEquals(1, commands.size());
        Assertions.assertInstanceOf(ExternalCommand.class, commands.getFirst());
    }

    @Test
    void testInvalidWithSpaceInKey() {
        String key = "var with space";
        String value = "some_value";

        String commandStr = String.format("%s=%s", key, value);
        List<Command> commands = parserOrchestrator.parse(commandStr, new Context());

        Assertions.assertEquals(1, commands.size());
        Assertions.assertInstanceOf(ExternalCommand.class, commands.getFirst());

        Assertions.assertNull(context.getVar(key));
    }
}