package io.cli.parser.token;

/**
 * Enum representing the types of tokens that can be identified during parsing.
 */
public enum TokenType {
    /**
     * Token type for double-quoted strings (e.g., "example").
     */
    DOUBLE_QUOTES,

    /**
     * Token type for single-quoted strings (e.g., 'example').
     */
    SINGLE_QUOTES,

    /**
     * Token type for command identifiers or keywords.
     */
    COMMAND
}
