package io.cli.parser.innerparser;

import io.cli.parser.token.Token;
import io.cli.parser.token.TokenType;

import java.util.ArrayList;
import java.util.List;

public class PipeParser {
    public static List<List<Token>> parsePipe(List<Token> input) {
        List<List<Token>> tokens = new ArrayList<>();
        List<Token> current = new ArrayList<>();
        for (Token token : input) {
            if (token.getType() == TokenType.COMMAND) {
                parseToken(token.getInput(), current, tokens);
            } else {
                current.add(token);
            }
        }
        if (!current.isEmpty()) {
            tokens.add(List.copyOf(current));
        }
        return tokens;
    }

    private static void parseToken(String str, List<Token> current, List<List<Token>> tokens) {
        StringBuilder currentToken = new StringBuilder();
        for (char c : str.toCharArray()) {
            if (c == '|') {
                if (!currentToken.isEmpty()) {
                    current.add(new Token(TokenType.COMMAND, currentToken.toString()));
                    currentToken.setLength(0);
                }
                tokens.add(List.copyOf(current));
                current.clear();
                currentToken.setLength(0);
            } else {
                currentToken.append(c);
            }
        }
        if (!currentToken.isEmpty()) {
            current.add(new Token(TokenType.COMMAND, currentToken.toString()));
        }
    }
}