package io.cli.parser.innerparser;

import io.cli.context.Context;
import io.cli.parser.token.Token;
import io.cli.parser.token.TokenType;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Character.isAlphabetic;

public class Substitutor {
    public List<Token> substitute(List<Token> input, Context context) {
        List<Token> result = new ArrayList<>();

        for (Token token : input) {
            if (token.getType() == TokenType.SINGLE_QUOTES) {
                result.add(token);
            } else {
                result.add(new Token(token.getType(), substituteVariables(token.getInput(), context)));
            }
        }

        return result;
    }

    private String substituteVariables(String str, Context context) {
        StringBuilder result = new StringBuilder();
        int i = 0;
        StringBuilder varBuilder = new StringBuilder();
        boolean startSubstitute = false;
        while (i < str.length()) {
            if (startSubstitute) {
                // TODO: Add check for the first symbol
                if (Character.isAlphabetic(str.charAt(i)) || Character.isDigit(str.charAt(i)) || str.charAt(i) == '_' || str.charAt(i) == '?') {
                    varBuilder.append(str.charAt(i));
                    i++;
                } else {
                    if (context.getVar(varBuilder.toString()) != null) {
                        result.append(context.getVar(varBuilder.toString()));
                    } else {
                        result.append('$').append(varBuilder);
                    }
                    startSubstitute = false;
                }
            } else if (str.charAt(i) == '$' && (i == 0 || str.charAt(i - 1) != '\\')) {
                startSubstitute = true;
                varBuilder.setLength(0);
                i++;
            } else {
                result.append(str.charAt(i));
                i++;
            }
        }
        if (startSubstitute) {
            if (context.getVar(varBuilder.toString()) != null) {
                result.append(context.getVar(varBuilder.toString()));
            } else {
                result.append('$').append(varBuilder);
            }
        }
        return result.toString();
    }
}