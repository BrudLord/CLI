package io.cli.parser.token;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Token {
    private TokenType type;
    private String command;
}
