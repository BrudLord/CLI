package io.cli.parser.token;

import java.util.Objects;

/**
 * Represents a token identified during parsing.
 * A token consists of a type and the associated input string.
 */
public class Token {
    /**
     * The type of the token.
     */
    private final TokenType type;
    /**
     * The string value associated with the token.
     */
    private final String input;

    /**
     * Constructs a Token with the specified type and input string.
     *
     * @param tokenType The type of the token.
     * @param tokenInput The string value associated with the token.
     */
    public Token(final TokenType tokenType, final String tokenInput) {
        this.type = tokenType;
        this.input = tokenInput;
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
     * @return {@code true} if the specified object is equal to this token;
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Token token = (Token) obj;
        return type == token.type && input.equals(token.input);
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
        return "Token{"
                + "type=" + type
                + ", input='" + input + '\''
                + '}';
    }
}
