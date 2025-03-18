package io.cli.parsers.token;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Token {
    TokenType type;
    String command;
}
