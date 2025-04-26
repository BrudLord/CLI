package io.cli.command.impl.assign;

import io.cli.command.Command;
import io.cli.context.Context;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class AssignCommandTest {

    @Test
    void executeShouldSetVariableInContext() {
        var context = new Context();

        String key = "key", value = "value";

        Command cmd = new AssignCommand(context, key, value);
        cmd.execute();

        assertEquals(value, context.getVar(key));
    }

    @Test
    void executeShouldUpdateExistingVariable() {
        var context = new Context();

        String key = "key", oldValue = "oldValue", newValue = "newValue";

        context.setVar(key, oldValue);
        assertEquals(oldValue, context.getVar(key));

        Command cmd = new AssignCommand(context, key, newValue);
        cmd.execute();

        assertEquals(newValue, context.getVar(key));
    }

    @Test
    void setStreamsShouldDoNothing() {
        var context = new Context();

        String key = "key", value = "value";

        Command cmd = new AssignCommand(context, key, value);

        byte[] input = "hello, world!\n".getBytes();
        InputStream inputStream = new ByteArrayInputStream(input);
        cmd.setInputStream(inputStream);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        cmd.setOutputStream(outputStream);

        cmd.execute();

        assertDoesNotThrow(
                () -> assertArrayEquals(input, inputStream.readAllBytes())
        );

        assertEquals(0, outputStream.toByteArray().length);
    }
}