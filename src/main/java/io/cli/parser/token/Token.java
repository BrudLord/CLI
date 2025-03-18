package io.cli.parser.token;


import java.util.Objects;

public class Token {
    private final TokenType type;
    private final String command;

    public Token(TokenType type, String command) {
        this.type = type;
        this.command = command;
    }

    public TokenType getType() {
        return type;
    }

    public String getCommand() {
        return command;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Token token = (Token) obj;
        return type == token.type && command.equals(token.command);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, command);
    }

    @Override
    public String toString() {
        return "Token{" +
                "type=" + type +
                ", command='" + command + '\'' +
                '}';
    }
}
