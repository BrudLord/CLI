package io.cli.context;

import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents the environment and context for the CLI.
 */
public class Context {
    private final Map<String, String> envVars = new HashMap<>();

    /**
     * Creates a default instance of {@code Context} with several environment variables,
     * such as `?` symbol and a current working directory (used by `pwd` CLI command).
     *
     * @return {@code Context}
     */
    public static Context initial() {
        var context = new Context();

        // install default expected values
        String pwd = Paths.get("").toAbsolutePath().toString();
        context.setVar(Variables.CURRENT_WORKING_DIRECTORY_VARIABLE_NAME, pwd);
        context.setVar(Variables.MOST_RECENT_RETURN_STATUS_CODE_VARIABLE_NAME, "0");

        return context;
    }

    /**
     * Initializes the context with default values.
     * See {@code Context.initial} static method.
     */
    private Context() {}

    /**
     * Retrieves an unmodifiable view of the environment variables.
     *
     * @return an unmodifiable map of environment variables.
     */
    public Map<String, String> getEnvironment() {
        return Collections.unmodifiableMap(envVars);
    }

    /**
     * Retrieves the value of a specific environment variable.
     *
     * @param key the name of the variable to retrieve.
     * @return the value of the variable, or null if it does not exist.
     */
    public String getVar(String key) {
        return envVars.get(key);
    }

    /**
     * Sets or updates the value of an environment variable.
     *
     * @param key   the name of the variable.
     * @param value the value of the variable.
     */
    public void setVar(String key, String value) {
        envVars.put(key, value);
    }
}
