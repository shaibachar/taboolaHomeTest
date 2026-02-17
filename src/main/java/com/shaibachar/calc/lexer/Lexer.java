package com.shaibachar.calc.lexer;

import com.shaibachar.calc.exceptions.ErrorMessages;
import com.shaibachar.calc.exceptions.ParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * A simple lexer for a calculator language that supports:
 * - Identifiers: [a-zA-Z_][a-zA-Z0-9_]*
 * - Numbers: [0-9]+
 * - Operators: +, -, *, /, %, ^, ++, --, +=, -=, *=, /=, ^=
 * - Parentheses: (, )
 */
public class Lexer {
    private static final Logger LOGGER = Logger.getLogger(Lexer.class.getName());
    private final String input;
    private int pos;

    public Lexer(String input) {
        this.input = input == null ? "" : input;
    }

    public List<Token> tokenize() {
        LOGGER.fine("Tokenizing input");
        long startNs = System.nanoTime();
        List<Token> tokens = new ArrayList<>();
        while (!isAtEnd()) {
            char c = peek();
            if (Character.isWhitespace(c)) {
                advance();
                continue;
            }

            // Record the start position for error reporting and token lexeme extraction
            int start = pos;

            // Identifiers and keywords
            if (Character.isLetter(c) || c == '_') {
                tokens.add(readIdentifier(start));
                continue;
            }

            // Numbers
            if (Character.isDigit(c)) {
                tokens.add(readNumber(start));
                continue;
            }

            // Operators and punctuation
            switch (c) {
                case '+' -> {
                    advance();
                    if (match('+')) {
                        tokens.add(new Token(TokenType.PLUS_PLUS, "++", start));
                    } else if (match('=')) {
                        tokens.add(new Token(TokenType.PLUS_EQUAL, "+=", start));
                    } else {
                        tokens.add(new Token(TokenType.PLUS, "+", start));
                    }
                }
                case '-' -> {
                    advance();
                    if (match('-')) {
                        tokens.add(new Token(TokenType.MINUS_MINUS, "--", start));
                    } else if (match('=')) {
                        tokens.add(new Token(TokenType.MINUS_EQUAL, "-=", start));
                    } else {
                        tokens.add(new Token(TokenType.MINUS, "-", start));
                    }
                }
                case '*' -> {
                    advance();
                    if (match('=')) {
                        tokens.add(new Token(TokenType.STAR_EQUAL, "*=", start));
                    } else {
                        tokens.add(new Token(TokenType.STAR, "*", start));
                    }
                }
                case '/' -> {
                    advance();
                    if (match('=')) {
                        tokens.add(new Token(TokenType.SLASH_EQUAL, "/=", start));
                    } else {
                        tokens.add(new Token(TokenType.SLASH, "/", start));
                    }
                }
                case '%' -> {
                    advance();
                    if (match('=')) {
                        tokens.add(new Token(TokenType.PERCENT_EQUAL, "%=", start));
                    } else {
                        tokens.add(new Token(TokenType.PERCENT, "%", start));
                    }
                }
                case '^' -> {
                    advance();
                    if (match('=')) {
                        tokens.add(new Token(TokenType.CARET_EQUAL, "^=", start));
                    } else {
                        tokens.add(new Token(TokenType.CARET, "^", start));
                    }
                }
                case '=' -> {
                    advance();
                    tokens.add(new Token(TokenType.EQUAL, "=", start));
                }
                case '(' -> {
                    advance();
                    tokens.add(new Token(TokenType.LPAREN, "(", start));
                }
                case ')' -> {
                    advance();
                    tokens.add(new Token(TokenType.RPAREN, ")", start));
                }
                default -> throw new ParseException(ErrorMessages.lexerUnexpectedCharacter(c, pos));
            }
        }

        // Add an EOF token at the end of the input for the parser to know when to stop
        tokens.add(new Token(TokenType.EOF, "", pos));
        long elapsedMs = (System.nanoTime() - startNs) / 1_000_000;
        LOGGER.fine("perf.component=lexer_tokenize elapsed_ms=" + elapsedMs + " tokens=" + tokens.size());
        return tokens;
    }

    /**
     * Reads an identifier token starting at the given position.
     * An identifier consists of letters, digits, and underscores,
     * but must start with a letter or underscore.
     *
     * @param start the starting position of the identifier
     * @return a Token representing the identifier
     */
    private Token readIdentifier(int start) {
        LOGGER.fine("Reading identifier at position " + start);
        while (!isAtEnd()) {
            char c = peek();
            if (Character.isLetterOrDigit(c) || c == '_') {
                advance();
            } else {
                break;
            }
        }
        return new Token(TokenType.IDENT, input.substring(start, pos), start);
    }

    /**
     * Reads a number token starting at the given position.
     * A number consists of digits, optionally followed by a fractional part.
     *
     * @param start the starting position of the number
     * @return a Token representing the number
     * @throws ParseException if the number literal is invalid or too large
     */
    private Token readNumber(int start) {
        LOGGER.fine("Reading number at position " + start);
        while (!isAtEnd() && Character.isDigit(peek())) {
            advance();
        }
        boolean hasFraction = false;
        if (!isAtEnd() && peek() == '.') {
            if (peekNextIsDigit()) {
                hasFraction = true;
                advance();
                while (!isAtEnd() && Character.isDigit(peek())) {
                    advance();
                }
            } else {
                throw new ParseException(ErrorMessages.lexerInvalidNumberLiteral(pos));
            }
        }
        String literal = input.substring(start, pos);
        try {
            if (hasFraction) {
                double value = Double.parseDouble(literal);
                if (Double.isInfinite(value)) {
                    throw new NumberFormatException();
                }
            } else {
                Long.parseLong(literal);
            }
        } catch (NumberFormatException ex) {
            String kind = hasFraction ? "Floating-point" : "Long";
            throw new ParseException(ErrorMessages.lexerNumberOverflow(kind, literal));
        }
        return new Token(TokenType.NUMBER, literal, start);
    }

    /**
     * Checks if the next character matches the expected character. If it does, consumes it and returns true.
     * Otherwise, returns false without consuming anything.
     *
     * @param expected the character to match
     * @return true if the next character matches the expected character, false otherwise
     */
    private boolean match(char expected) {
        if (isAtEnd() || input.charAt(pos) != expected) {
            return false;
        }
        pos++;
        return true;
    }

    /**
     * Returns the current character without consuming it.
     *
     * @return the current character
     */
    private char peek() {
        return input.charAt(pos);
    }

    /**
     * Advances the current position by one character.
     */
    private void advance() {
        pos++;
    }

    /**
     * Checks if we have reached the end of the input string.
     *
     * @return true if we are at the end of the input, false otherwise
     */
    private boolean isAtEnd() {
        return pos >= input.length();
    }

    private boolean peekNextIsDigit() {
        int nextPos = pos + 1;
        return nextPos < input.length() && Character.isDigit(input.charAt(nextPos));
    }
}
