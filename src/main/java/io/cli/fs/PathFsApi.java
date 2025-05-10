package io.cli.fs;

import io.cli.context.Context;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PathFsApi implements FsApi<Path> {
    @Override
    public Path withWorkingDir(Context context, Path path) {
        String pwd = context.pwd();

        if (path.isAbsolute() || (pwd == null) || pwd.isEmpty()) {
            return path;
        }
        else {
            return Paths.get(pwd, path.toString()).normalize().toAbsolutePath();
        }
    }

    @Override
    public Path withWorkingDir(Context context, String path) {
        return withWorkingDir(context, Paths.get(path));
    }

    @Override
    public Path relativeToWorkingDir(Context context, Path path) {
        Path pwd = Paths.get(context.pwd());
        return relativeTo(pwd, path);
        // return pwd.relativize(path);
    }

    @Override
    public Path relativeToWorkingDir(Context context, String path) {
        return relativeToWorkingDir(context, Paths.get(path));
    }

    @Override
    public Path relativeTo(Path base, Path target) {
        return base.relativize(target);
    }

    @Override
    public Path relativeTo(Path base, String target) {
        return relativeTo(base, Paths.get(target));
    }
}
