package io.cli.parser.innerparser;

import io.cli.parser.token.Token;
import io.cli.parser.token.TokenType;

import java.util.ArrayList;
import java.util.List;

public class QuoteParser {
    public List<Token> parseQuote(String input) {
        List<Token> tokens = new ArrayList<>();
        StringBuilder token = new StringBuilder();
        TokenType state = TokenType.COMMAND;
        char prev = ' ';
        for (char c : input.toCharArray()) {
            if (prev == '\\') {
                token.append(c);
            } else if (c == '"' || c == '\'') {
                boolean isDouble = (c == '"');
                TokenType quoteType = isDouble ? TokenType.DOUBLE_QUOTES : TokenType.SINGLE_QUOTES;
                if (state == quoteType) {
                    tokens.add(new Token(state, token.toString()));
                    token.setLength(0);
                    state = TokenType.COMMAND;
                } else if (state != TokenType.COMMAND) {
                    token.append(c);
                } else {
                    if (!token.isEmpty()) {
                        tokens.add(new Token(state, token.toString()));
                        token.setLength(0);
                    }
                    state = quoteType;
                }
            } else if (c == ' ') {
                if (state == TokenType.COMMAND) {
                    if (!token.isEmpty()) {
                        tokens.add(new Token(state, token.toString()));
                        token.setLength(0);
                    }
                } else {
                    token.append(c);
                }
            } else {
                token.append(c);
            }
            prev = c;
        }
        if (!token.isEmpty()) {
            if (state != TokenType.COMMAND)
                throw new IllegalStateException("Invalid: you must end quotes");
            tokens.add(new Token(state, token.toString()));
        }
        return tokens;
    }
}