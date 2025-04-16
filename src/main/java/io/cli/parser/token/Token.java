package io.cli.parser.token;


import java.util.Objects;

public class Token {
    private final TokenType type;
    private final String input;

    public Token(TokenType type, String input) {
        this.type = type;
        this.input = input;
    }

    public TokenType getType() {
        return type;
    }

    public String getInput() {
        return input;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Token token = (Token) obj;
        return type == token.type && input.equals(token.input);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, input);
    }

    @Override
    public String toString() {
        return "Token{" +
                "type=" + type +
                ", command='" + input + '\'' +
                '}';
    }
}
