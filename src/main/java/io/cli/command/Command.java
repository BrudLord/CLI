package io.cli.command;

import java.io.InputStream;
import java.io.OutputStream;

public interface Command {
    int execute();

    void setInputStream(InputStream newInputStream);

    void setOutputStream(OutputStream newOutputStream);
}
