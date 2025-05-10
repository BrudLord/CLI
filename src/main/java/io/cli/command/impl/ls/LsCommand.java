package io.cli.command.impl.ls;

import io.cli.command.Command;
import io.cli.context.Context;
import io.cli.exception.CommandIllegalStateException;
import io.cli.exception.OutputException;
import io.cli.fs.PathFsApi;
import io.cli.parser.token.Token;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class LsCommand implements Command {
    private OutputStream outputStream = System.out;
    private final List<Token> args;
    private final PathFsApi fs;
    private final Context context;

    private enum EntryType {
        FILE, DIRECTORY
    }

    private record FilepathEntry(
        String filepath,
        EntryType type,
        List<String> dirFilepaths
    ) {}

    LsCommand(List<Token> args, PathFsApi fs, Context context) {
        this.args = args;
        this.fs = fs;
        this.context = context;
    }

    @Override
    public void execute() {
        List<FilepathEntry> filepaths = new ArrayList<>();
        
        if (args.size() <= 1) {
            // no filepath params -> print all files present in the current working dir
            String workingDir = context.pwd();
            Path dirPath = Paths.get(workingDir);
            
            if (!Files.exists(dirPath)) {
                throw new CommandIllegalStateException("ls: cannot access '" + workingDir + "': No such file or directory");
            }

            // add the current directory for the result
             filepaths.add(new FilepathEntry(
                 ".",
                 EntryType.DIRECTORY,
                 relativizeFilepaths(dirPath)
             ));
        }
        else {
            // relativize provided filepaths to working dir
            // and extract these files and directories
            // (for dirs it returns a list in inner files)
            for (int i = 1; i < args.size(); i++) {
                String filepath = args.get(i).getInput();
                Path adjustedFilepath = fs.withWorkingDir(context, filepath);

                if (Files.isRegularFile(adjustedFilepath)) {
                    filepaths.add(new FilepathEntry(
                        filepath,
                        EntryType.FILE,
                        Collections.emptyList()
                    ));
                }
                else if (Files.isDirectory(adjustedFilepath)) {
                    filepaths.add(new FilepathEntry(
                        filepath,
                        EntryType.DIRECTORY,
                        relativizeFilepaths(adjustedFilepath)
                    ));
                }
                else if (!Files.exists(adjustedFilepath)) {
                    throw new CommandIllegalStateException("ls: cannot access '" + filepath + "': No such file or directory");
                }
            }
        }

        writeToOutput(filepaths);
    }

    private List<String> relativizeFilepaths(Path dirPath) {
        // we search only for the direct children
        final int MAX_DEPTH = 1;

        try (var walk = Files.walk(dirPath, MAX_DEPTH)) {
            return walk
                .filter(path -> !path.equals(dirPath)) // discard self
                .map(path -> fs.relativeTo(dirPath, path).toString())
                .filter(path -> !path.isEmpty())
                .filter((path) -> !path.startsWith(".")) // hidden folders/files
                .sorted()
                .toList();
        } catch (IOException e) {
            throw new OutputException(e.getMessage());
        }
    }

    @Override
    public void setInputStream(InputStream newInputStream) {}

    @Override
    public void setOutputStream(OutputStream newOutputStream) {
        this.outputStream = newOutputStream;
    }


    private void writeToOutput(List<FilepathEntry> filepaths) {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
        try {
            if (filepaths.size() == 1) {
                boolean isWorkingDirectoryTraversed = Objects.equals(filepaths.getFirst().filepath, ".");

                // print the entries of the current working directory
                List<String> dirFilepaths = filepaths.getFirst().dirFilepaths;
                for (int i = 0, dirFilepathsSize = dirFilepaths.size(); i < dirFilepathsSize; i++) {
                    var filepath = dirFilepaths.get(i);
                    writer.write(filepath);

                    if (i + 1 < dirFilepathsSize) {
                        if (isWorkingDirectoryTraversed) writer.write("    ");
                        else writer.newLine();
                    }
                }
                writer.newLine();
                writer.flush();
            }
            else if (!filepaths.isEmpty()) {
                // output every entry
                for (var entry : filepaths) {
                    if (entry.type == EntryType.FILE) {
                        writer.write(entry.filepath);
                        writer.newLine();
                    }
                    else {
                        writer.write(entry.filepath + (!entry.dirFilepaths.isEmpty() ? ":" : ""));
                        writer.newLine();
                        for (var filepath : entry.dirFilepaths) {
                            writer.write(filepath);
                            writer.newLine();
                        }
                        if (!entry.dirFilepaths.isEmpty()) {
                            writer.newLine();
                        }
                    }
                }
                writer.flush();
            }
        }
        catch (IOException e) {
            throw new OutputException(e.getMessage());
        }
    }
}
