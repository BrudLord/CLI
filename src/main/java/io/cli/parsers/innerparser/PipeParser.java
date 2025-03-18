package io.cli.parsers.innerparser;

import io.cli.parsers.token.Token;

import java.util.List;

public class PipeParser {
    public static List<List<Token>> parsePipe(List<Token> input) {
        return List.of(input);
    }
}