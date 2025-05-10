package io.cli.context;


public interface Variables {
    /**
     * Used as a key in {@code Context} for inserting a current working directory
     * tracked by the CLI. This working directory is returned by {@code PwdCommand}.
     */
    String CURRENT_WORKING_DIRECTORY_VARIABLE_NAME = "PWD";
    String PREVIOUS_WORKING_DIRECTORY_VARIABLE_NAME = "OLDPWD";

    String MOST_RECENT_RETURN_STATUS_CODE_VARIABLE_NAME = "?";
}
