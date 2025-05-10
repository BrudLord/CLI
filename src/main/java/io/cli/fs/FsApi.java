package io.cli.fs;

import io.cli.context.Context;

/**
 * Defines API for adjusting filepaths in accordance to the {@code Context}'s
 * current working directory represented as `PWD`.
 *
 * @param <Path> The type representing a path in the file system.
 */
public interface FsApi<Path> {
    /**
     * Adjusts the given {@code path} to be either absolute or relative
     * to the `Context`'s current working directory extracted from `PWD`.
     *
     * @param context Context with the environment data
     * @param path a path to adjust, according to the `Context`'s current working directory.
     * @return adjusted path (e.g., {@code context.pwd / path}
     */
    Path withWorkingDir(Context context, Path path);

    Path withWorkingDir(Context context, String path);
}
