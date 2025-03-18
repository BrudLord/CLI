package io.cli.command;

import java.io.InputStream;
import java.io.OutputStream;

public interface Command {
    public void execute();

    public void setInputStream(InputStream input);

    public void setOutputStream(OutputStream output);
}
