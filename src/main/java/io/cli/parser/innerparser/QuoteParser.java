package io.cli.parser.innerparser;

import io.cli.exception.InputException;
import io.cli.parser.token.Token;
import io.cli.parser.token.TokenType;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses a string into tokens, handling quoted substrings and escaping.
 */
public class QuoteParser {

    /**
     * Parses the input string into a list of tokens based on quotes and whitespace.
     *
     * @param input The input string to parse.
     * @return A list of tokens parsed from the input string.
     * @throws IllegalStateException If an unclosed quote is detected.
     */
    public List<Token> parseQuote(String input) {
        List<Token> tokens = new ArrayList<>(); // Accumulates the parsed tokens.
        StringBuilder token = new StringBuilder(); // Builds the current token.
        TokenType state = TokenType.COMMAND; // Tracks the current parsing state.
        char prev = ' '; // Tracks the previous character to handle escaping.

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            boolean needToMerge = i < input.length() - 1 && !Character.isSpaceChar(input.charAt(i + 1));
            if (prev == '\\') {
                // Append the escaped character to the current token.
                token.append(c);
            } else if (c == '"' || c == '\'') {
                // Handle opening or closing quotes.
                TokenType quoteType = (c == '"') ? TokenType.DOUBLE_QUOTES : TokenType.SINGLE_QUOTES;

                if (state == quoteType) {
                    // If in a quoted state, closing the quote completes the token.
                    tokens.add(new Token(state, token.toString(), needToMerge));
                    token.setLength(0);
                    state = TokenType.COMMAND; // Return to COMMAND state after closing the quote.
                } else if (state != TokenType.COMMAND) {
                    // If already in a different quoted state, treat the character as part of the token.
                    token.append(c);
                } else {
                    // If not in a quoted state, start a new quoted token.
                    if (!token.isEmpty()) {
                        tokens.add(new Token(state, token.toString(), needToMerge));
                        token.setLength(0);
                    }
                    state = quoteType; // Set the state to the current quote type.
                }
            } else if (c == ' ') {
                // Handle spaces, which can delimit tokens.
                if (state == TokenType.COMMAND) {
                    if (!token.isEmpty()) {
                        tokens.add(new Token(state, token.toString()));
                        token.setLength(0);
                    }
                } else {
                    // Treat spaces inside quotes as part of the token.
                    token.append(c);
                }
            } else {
                // Append regular characters to the current token.
                token.append(c);
            }
            prev = c; // Update the previous character.
        }

        if (state != TokenType.COMMAND) {
            // If the state is not COMMAND, it means there is an unclosed quote.
            throw new InputException("Invalid: you must end quotes");
        }

        // Handle the final token, if any.
        if (!token.isEmpty()) {
            tokens.add(new Token(state, token.toString()));
        }


        return tokens;
    }
}
