package com.shaibachar.calc.lexer;

import java.util.Objects;

public final class Token {
    private final TokenType type;
    private final String lexeme;
    private final int position;

    public Token(TokenType type, String lexeme, int position) {
        this.type = type;
        this.lexeme = lexeme;
        this.position = position;
    }

    public TokenType type() {
        return type;
    }

    public String lexeme() {
        return lexeme;
    }

    public int position() {
        return position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Token other)) {
            return false;
        }
        return position == other.position
                && Objects.equals(type, other.type)
                && Objects.equals(lexeme, other.lexeme);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, lexeme, position);
    }

    @Override
    public String toString() {
        return "Token[type=" + type + ", lexeme=" + lexeme + ", position=" + position + "]";
    }
}
