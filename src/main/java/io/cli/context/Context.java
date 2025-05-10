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
        // oldpwd and pwd are equal initially
        context.setVar(Variables.PREVIOUS_WORKING_DIRECTORY_VARIABLE_NAME, pwd);
        context.setVar(Variables.CURRENT_WORKING_DIRECTORY_VARIABLE_NAME, pwd);
        context.setSuccessfulStatusCode();

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

    // next goes helper methods for common ENV variables

    /**
     * Updates the most recent return status code in the context's environment variables.
     *
     * @param statusCode the status code to set, represented as a string.
     */
    public void setStatusCode(String statusCode) {
        setVar(Variables.MOST_RECENT_RETURN_STATUS_CODE_VARIABLE_NAME, statusCode);
    }

    /**
     * Sets the environment variable representing the most recent return status code
     * to indicate a successful operation (status code "0").
     *
     * This method updates the variable defined by
     * {@code Variables.MOST_RECENT_RETURN_STATUS_CODE_VARIABLE_NAME} in the context's
     * environment variable map to the value "0".
     */
    public void setSuccessfulStatusCode() {
        setStatusCode("0");
    }

    /**
     * Retrieves the most recent return status code from the context's environment variables.
     *
     * @return the most recent return status code as a string, or null if it does not exist.
     */
    public String getStatusCode() {
        return getVar(Variables.MOST_RECENT_RETURN_STATUS_CODE_VARIABLE_NAME);
    }

    /**
     * Returns the current working directory stored in the context's environment variables.
     *
     * @return the current working directory as a string, or null if it is not set.
     */
    public String pwd() {
        return getVar(Variables.CURRENT_WORKING_DIRECTORY_VARIABLE_NAME);
    }

    /**
     * Updates the current working directory stored in the context's environment variables.
     *
     * @param value the value to set as the current working directory (`PWD`).
     **/
    public void pwd(String value) {
        oldpwd(pwd());
        setVar(Variables.CURRENT_WORKING_DIRECTORY_VARIABLE_NAME, value);
    }

    /**
     * Retrieves the previous working directory stored in the context's environment variables.
     *
     * @return the previous working directory as a string, or null if it is not set.
     */
    public String oldpwd() {
        return getVar(Variables.PREVIOUS_WORKING_DIRECTORY_VARIABLE_NAME);
    }

    /**
     * Updates the previous working directory stored in the context's environment variables.
     *
     * @param value the value to set as the previous working directory (`OLDPWD`).
     */
    private void oldpwd(String value) {
        setVar(Variables.PREVIOUS_WORKING_DIRECTORY_VARIABLE_NAME, value);
    }
}
