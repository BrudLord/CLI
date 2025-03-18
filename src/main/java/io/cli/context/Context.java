package io.cli.context;

import java.util.HashMap;
import java.util.Map;

public class Context {
    private final Map<String, String> envVars = new HashMap<>();

    public String getVar(String key) {
        return envVars.get(key);
    }

    public void setVar(String key, String value) {
        envVars.put(key, value);
    }
}
