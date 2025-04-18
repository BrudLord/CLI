package io.cli.parser.innerparser;

import io.cli.parser.token.Token;
import io.cli.parser.token.TokenType;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses a list of tokens into groups separated by pipe ('|') symbols.
 */
public class PipeParser {

    /**
     * Parses the input list of tokens and splits them into groups based on the pipe ('|') symbol.
     *
     * @param input The list of tokens to parse.
     * @return A list of token groups, where each group represents a command separated by pipes.
     */
    public List<List<Token>> parsePipe(List<Token> input) {
        List<List<Token>> tokens = new ArrayList<>(); // Stores groups of tokens.
        List<Token> current = new ArrayList<>(); // Stores tokens for the current group.

        for (Token token : input) {
            if (token.getType() == TokenType.COMMAND) {
                // Parse COMMAND tokens to handle pipes.
                parseToken(token.getInput(), current, tokens);
            } else {
                // Add non-COMMAND tokens (e.g., quoted strings) directly to the current group.
                current.add(token);
            }
        }

        // Add the final group if it's not empty.
        if (!current.isEmpty()) {
            tokens.add(List.copyOf(current));
        }

        return tokens;
    }

    /**
     * Parses a string token, splitting it into commands based on the pipe ('|') symbol.
     *
     * @param str     The string to parse.
     * @param current The current group of tokens being processed.
     * @param tokens  The list of token groups to which new groups are added.
     */
    private void parseToken(String str, List<Token> current, List<List<Token>> tokens) {
        StringBuilder currentToken = new StringBuilder(); // Builds the current token.

        for (char c : str.toCharArray()) {
            if (c == '|') {
                // When a pipe is encountered, finalize the current token and start a new group.
                if (!currentToken.isEmpty()) {
                    current.add(new Token(TokenType.COMMAND, currentToken.toString()));
                    currentToken.setLength(0); // Clear the token builder.
                }
                // Add the current group to the list and start a new group.
                tokens.add(List.copyOf(current));
                current.clear();
            } else {
                // Append characters to the current token.
                currentToken.append(c);
            }
        }

        // Add the last token in the string to the current group, if any.
        if (!currentToken.isEmpty()) {
            current.add(new Token(TokenType.COMMAND, currentToken.toString()));
        }
    }
}
