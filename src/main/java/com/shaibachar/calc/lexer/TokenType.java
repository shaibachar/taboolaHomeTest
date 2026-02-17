package com.shaibachar.calc.lexer;

public enum TokenType {
    IDENT("identifier"),
    NUMBER("number"),
    PLUS("+"),
    MINUS("-"),
    STAR("*"),
    SLASH("/"),
    PERCENT("%"),
    CARET("^"),
    PLUS_PLUS("++"),
    MINUS_MINUS("--"),
    EQUAL("="),
    PLUS_EQUAL("+="),
    MINUS_EQUAL("-="),
    STAR_EQUAL("*="),
    SLASH_EQUAL("/="),
    PERCENT_EQUAL("%="),
    CARET_EQUAL("^="),
    LPAREN("("),
    RPAREN(")"),
    EOF("end of input");

    private final String description;

    TokenType(String description) {
        this.description = description;
    }

    @SuppressWarnings("unused")
    public String getDescription() {
        return description;
    }
}
