package io.cli.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents the environment and context for the CLI.
 */
public class Context {
    private final Map<String, String> envVars = new HashMap<>();

    /**
     * Initializes the context with default values.
     */
    public Context() {
        setVar("?", "0");
    }

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
