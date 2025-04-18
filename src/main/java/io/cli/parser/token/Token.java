package io.cli.parser.token;

import java.util.Objects;

/**
 * Represents a token identified during parsing.
 * A token consists of a type and the associated input string.
 */
public class Token {
    private final TokenType type; // The type of the token (e.g., COMMAND, DOUBLE_QUOTES).
    private final String input;  // The string value associated with the token.

    /**
     * Constructs a Token with the specified type and input string.
     *
     * @param type  The type of the token.
     * @param input The string value associated with the token.
     */
    public Token(TokenType type, String input) {
        this.type = type;
        this.input = input;
    }

    /**
     * Returns the type of the token.
     *
     * @return The TokenType of this token.
     */
    public TokenType getType() {
        return type;
    }

    /**
     * Returns the string value associated with the token.
     *
     * @return The input string of this token.
     */
    public String getInput() {
        return input;
    }

    /**
     * Compares this token to the specified object for equality.
     *
     * @param obj The object to compare with.
     * @return {@code true} if the specified object is equal to this token; {@code false} otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; // Check for reference equality.
        if (obj == null || getClass() != obj.getClass()) return false; // Ensure type compatibility.
        Token token = (Token) obj;
        return type == token.type && input.equals(token.input); // Compare fields.
    }

    /**
     * Returns the hash code for this token.
     *
     * @return The hash code of this token.
     */
    @Override
    public int hashCode() {
        return Objects.hash(type, input);
    }

    /**
     * Returns a string representation of this token.
     *
     * @return A string representing this token.
     */
    @Override
    public String toString() {
        return "Token{" +
                "type=" + type +
                ", input='" + input + '\'' +
                '}';
    }
}
