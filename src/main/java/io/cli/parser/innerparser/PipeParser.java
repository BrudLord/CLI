package io.cli.parser.innerparser;

import io.cli.parser.token.Token;

import java.util.List;

public class PipeParser {
    public static List<List<Token>> parsePipe(List<Token> input) {
        return List.of(input);
    }
}