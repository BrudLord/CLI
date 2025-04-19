package io.cli.parser.innerparser;

import io.cli.context.Context;
import io.cli.parser.token.Token;
import io.cli.parser.token.TokenType;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Character.isAlphabetic;

/**
 * Handles variable substitution in tokens using the provided context.
 * Supports substitution for variables prefixed with a `$` symbol.
 */
public class Substitutor {

    /**
     * Substitutes variables in a list of tokens based on the given context.
     * Tokens with type SINGLE_QUOTES are not modified.
     *
     * @param input   List of tokens to process.
     * @param context Context containing variable definitions for substitution.
     * @return A list of tokens with substituted variables where applicable.
     */
    public List<Token> substitute(List<Token> input, Context context) {
        List<Token> result = new ArrayList<>();

        for (Token token : input) {
            if (token.getType() == TokenType.SINGLE_QUOTES) {
                // Preserve tokens wrapped in single quotes as is.
                result.add(token);
            } else {
                // Substitute variables for other token types.
                result.add(new Token(token.getType(), substituteVariables(token.getInput(), context), token.getNeedToBeMerge()));
            }
        }

        int i = 0;
        while (i < result.size()) {
            if (result.get(i).getNeedToBeMerge()) {
                result.set(i, new Token(
                        TokenType.COMMAND,
                        result.get(i).getInput() + result.get(i + 1).getInput(),
                                result.get(i + 1).getNeedToBeMerge()
                        )
                );
                result.remove(i + 1);
            } else {
                i++;
            }
        }

        return result;
    }

    /**
     * Performs variable substitution in the input string.
     * Recognizes variables prefixed with a `$` and resolves their values from the context.
     *
     * @param str     The input string to process.
     * @param context Context containing variable definitions.
     * @return The string with substituted variable values.
     */
    private String substituteVariables(String str, Context context) {
        StringBuilder result = new StringBuilder(); // Accumulates the final substituted string.
        StringBuilder varBuilder = new StringBuilder(); // Accumulates the variable name.
        boolean startSubstitute = false; // Tracks whether a variable substitution is in progress.
        int i = 0;

        while (i < str.length()) {
            if (startSubstitute) {
                if (varBuilder.isEmpty()
                        && (isAlphabetic(str.charAt(i)) || str.charAt(i) == '_')
                ) {
                    // Build first letter in var
                    varBuilder.append(str.charAt(i));
                    i++;
                } else if (isAlphabetic(str.charAt(i))
                        || Character.isDigit(str.charAt(i))
                        || str.charAt(i) == '_'
                        || str.charAt(i) == '?'
                ) {
                    // Continue building the variable name if it's valid.
                    varBuilder.append(str.charAt(i));
                    i++;
                } else {
                    // Resolve and append the variable's value, or append the variable as is if undefined.
                    resolveVariable(result, varBuilder, context);
                    startSubstitute = false;
                }
            } else if (str.charAt(i) == '$' && (i == 0 || str.charAt(i - 1) != '\\')) {
                // Detect the start of a variable substitution, ignoring escaped `$`.
                startSubstitute = true;
                varBuilder.setLength(0); // Clear the variable builder for the new variable.
                i++;
            } else {
                // Append regular characters directly to the result.
                result.append(str.charAt(i));
                i++;
            }
        }

        // Handle any unfinished variable substitution at the end of the string.
        if (startSubstitute) {
            resolveVariable(result, varBuilder, context);
        }

        return result.toString();
    }

    /**
     * Resolves a variable name from the context and appends its value to the result.
     * If the variable is not defined, appends the variable name prefixed with `$`.
     *
     * @param result     The StringBuilder for the final result.
     * @param varBuilder The StringBuilder containing the variable name.
     * @param context    The context containing variable definitions.
     */
    private void resolveVariable(StringBuilder result, StringBuilder varBuilder, Context context) {
        String varName = varBuilder.toString();
        String varValue = context.getVar(varName);
        if (varValue != null) {
            result.append(varValue);
        } else {
            result.append('$').append(varName);
        }
    }
}
