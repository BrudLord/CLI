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
     *
     */
    private boolean needToBeMerge = false;

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
     * Constructs a Token with the specified type and input string.
     *
     * @param tokenType The type of the token.
     * @param tokenInput The string value associated with the token.
     * @param needToBeMerge The bool value which means should we merge this token with the next one
     */
    public Token(final TokenType tokenType, final String tokenInput, boolean needToBeMerge) {
            this.type = tokenType;
            this.input = tokenInput;
            this.needToBeMerge = needToBeMerge;
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
     * Returns the bool value which tell need we merge this token with next or not.
     *
     * @return The input string of this token.
     */
    public boolean getNeedToBeMerge() {
        return needToBeMerge;
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
        return type == token.type && input.equals(token.input) && needToBeMerge == token.needToBeMerge;
    }

    /**
     * Returns the hash code for this token.
     *
     * @return The hash code of this token.
     */
    @Override
    public int hashCode() {
        return Objects.hash(type, input, needToBeMerge);
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
                + ", needToBeMerge= '" + needToBeMerge + '\''
                + '}';
    }
}
