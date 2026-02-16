package com.shaibachar.calc.parser;

import com.shaibachar.calc.exceptions.ErrorMessages;
import com.shaibachar.calc.exceptions.ParseException;
import com.shaibachar.calc.lexer.Token;
import com.shaibachar.calc.lexer.TokenType;
import com.shaibachar.calc.parser.expr.*;
import com.shaibachar.calc.parser.stmt.AssignStmt;
import com.shaibachar.calc.parser.stmt.Stmt;

import java.util.List;
import java.util.logging.Logger;

/**
 * A simple recursive descent parser for a subset of Java-like expressions and statements.
 * It supports variable assignments, arithmetic expressions, and increment/decrement operators.
 * The parser assumes that the input tokens are well-formed and does not handle all edge cases or syntax errors comprehensively.
 * Example usage:
 * <pre>
 * List<Token> tokens = new Lexer("i += 2 * (3 + 4)").tokenize();
 * Parser parser = new Parser(tokens);
 * Stmt stmt = parser.parseStatement();
 * </pre>
 */
public class Parser {
    private static final Logger LOGGER = Logger.getLogger(Parser.class.getName());
    private final List<Token> tokens;
    private int current;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public Stmt parseStatement() {
        LOGGER.fine("Parsing statement");
        long startNs = System.nanoTime();

        // Expect an assignment statement of the form: IDENT ASSIGN_OP EXPR EOF
        Token ident = consume(TokenType.IDENT, ErrorMessages.PARSE_EXPECTED_IDENTIFIER);

        // Parse the assignment operator, which can be =, +=, -=, *=, /=, or %=
        AssignOp assignOp = parseAssignOp();

        // Parse the expression on the right-hand side of the assignment
        Expr expr = expression();

        // After parsing the expression, we expect to reach the end of the token list (EOF).
        // If there are any extra tokens, it's an error.
        consume(TokenType.EOF, ErrorMessages.PARSE_UNEXPECTED_TOKEN_AFTER_EXPRESSION);

        long elapsedMs = (System.nanoTime() - startNs) / 1_000_000;
        LOGGER.fine("perf.component=parser_parse_statement elapsed_ms=" + elapsedMs);
        return new AssignStmt(ident.lexeme(), assignOp, expr);
    }

    /**
     * Parses an assignment operator, which can be one of the following:
     * - = (simple assignment)
     * - += (addition assignment)
     * - -= (subtraction assignment)
     * - *= (multiplication assignment)
     * - /= (division assignment)
     * - %= (modulus assignment)
     * @return the corresponding AssignOp enum value for the parsed operator
     */
    private AssignOp parseAssignOp() {
        LOGGER.fine("Parsing assignment operator");
        if (match(TokenType.EQUAL)) {
            return AssignOp.ASSIGN;
        }
        if (match(TokenType.PLUS_EQUAL)) {
            return AssignOp.PLUS_ASSIGN;
        }
        if (match(TokenType.MINUS_EQUAL)) {
            return AssignOp.MINUS_ASSIGN;
        }
        if (match(TokenType.STAR_EQUAL)) {
            return AssignOp.MUL_ASSIGN;
        }
        if (match(TokenType.SLASH_EQUAL)) {
            return AssignOp.DIV_ASSIGN;
        }
        if (match(TokenType.PERCENT_EQUAL)) {
            return AssignOp.MOD_ASSIGN;
        }
        throw error(peek(), ErrorMessages.PARSE_EXPECTED_ASSIGN_OP);
    }

    private Expr expression() {
        LOGGER.fine("Parsing expression");
        return additive();
    }

    /**
     * Parses an additive expression, which consists of multiplicative expressions combined with addition or subtraction operators.
     * @return the parsed expression
     */
    private Expr additive() {
        LOGGER.fine("Parsing additive expression");

        // Start by parsing the leftmost multiplicative expression
        Expr expr = multiplicative();

        // Then, as long as we see a + or - operator, we consume it and parse the next multiplicative expression on the right
        while (match(TokenType.PLUS, TokenType.MINUS)) {
            // The operator token determines whether this is an addition or subtraction operation
            Token operator = previous();

            // Parse the right-hand side multiplicative expression
            Expr right = multiplicative();

            // Create a new BinaryExpr node that combines the left and right expressions with the appropriate operator
            expr = new BinaryExpr(expr, operator.type() == TokenType.PLUS ? BinaryOp.ADD : BinaryOp.SUB, right);
        }
        return expr;
    }

    /**
     * Parses a multiplicative expression, which consists of unary expressions combined with multiplication,
     * division, or modulus operators.
     * @return the parsed expression
     */
    private Expr multiplicative() {
        LOGGER.fine("Parsing multiplicative expression");

        // Start by parsing the leftmost unary expression
        Expr expr = unary();

        // Then, as long as we see a *, /, or % operator, we consume it and parse the next unary expression on the right
        while (match(TokenType.STAR, TokenType.SLASH, TokenType.PERCENT)) {

            // The operator token determines whether this is a multiplication, division, or modulus operation
            Token operator = previous();

            // Parse the right-hand side unary expression
            Expr right = unary();

            // Create a new BinaryExpr node that combines the left and right expressions with the appropriate operator
            BinaryOp op = switch (operator.type()) {
                case STAR -> BinaryOp.MUL;
                case SLASH -> BinaryOp.DIV;
                case PERCENT -> BinaryOp.MOD;
                default -> throw error(operator, ErrorMessages.PARSE_INVALID_MULTIPLICATIVE_OPERATOR);
            };
            expr = new BinaryExpr(expr, op, right);
        }
        return expr;
    }

    /**
     * Parses a unary expression, which can be a primary expression preceded by an
     * optional pre-increment, pre-decrement, unary plus, or unary minus operator.
     * @return the parsed expression
     */
    private Expr unary() {
        LOGGER.fine("Parsing unary expression");
        if (match(TokenType.PLUS_PLUS)) {
            return new UnaryExpr(UnaryOp.PRE_INC, unary());
        }
        if (match(TokenType.MINUS_MINUS)) {
            return new UnaryExpr(UnaryOp.PRE_DEC, unary());
        }
        if (match(TokenType.PLUS)) {
            return new UnaryExpr(UnaryOp.PLUS, unary());
        }
        if (match(TokenType.MINUS)) {
            return new UnaryExpr(UnaryOp.MINUS, unary());
        }
        return postfix();
    }

    /**
     * Parses a postfix expression, which can be a primary expression followed by
     * an optional post-increment or post-decrement operator.
     * @return the parsed expression
     */
    private Expr postfix() {
        LOGGER.fine("Parsing postfix expression");
        Expr expr = primary();
        if (match(TokenType.PLUS_PLUS)) {
            return new PostfixExpr(expr, PostfixOp.POST_INC);
        }
        if (match(TokenType.MINUS_MINUS)) {
            return new PostfixExpr(expr, PostfixOp.POST_DEC);
        }
        return expr;
    }

    /**
     * Parses a primary expression, which can be a number literal, an identifier, or a parenthesized expression.
     * @return the parsed expression
     * @throws ParseException if the current token does not match any of the expected primary expression types
     */
    private Expr primary() {
        LOGGER.fine("Parsing primary expression");
        if (match(TokenType.NUMBER)) {
            String literal = previous().lexeme();
            try {
                if (literal.contains(".")) {
                    return new LiteralExpr(Double.parseDouble(literal));
                }
                return new LiteralExpr(Long.parseLong(literal));
            } catch (NumberFormatException ex) {
                throw error(previous(), ErrorMessages.PARSE_INVALID_NUMBER_LITERAL);
            }
        }
        if (match(TokenType.IDENT)) {
            return new VarExpr(previous().lexeme());
        }
        if (match(TokenType.LPAREN)) {
            Expr expr = expression();
            consume(TokenType.RPAREN, ErrorMessages.PARSE_EXPECTED_RPAREN);
            return expr;
        }
        throw error(peek(), ErrorMessages.PARSE_EXPECTED_EXPRESSION);
    }

    /**
     * Checks if the current token matches any of the given types.
     * If it does, consumes the token and returns true.
     * Otherwise, returns false without consuming anything.
     * @param types the token types to check against
     * @return true if the current token matches any of the types, false otherwise
     */
    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    /**
     * Consumes the current token if it matches the expected type, otherwise throws a ParseException with the given message.
     * @param type the expected token type
     * @param message the error message to include in the exception if the token does not match
     * @return the consumed token if it matches the expected type
     * @throws ParseException if the current token does not match the expected type
     */
    private Token consume(TokenType type, String message) {
        if (check(type)) {
            return advance();
        }
        throw error(peek(), message);
    }

    /**
     * Checks if the current token matches the given type without consuming it.
     * @param type the token type to check against
     * @return true if the current token matches the type, false otherwise
     */
    private boolean check(TokenType type) {
        if (isAtEnd()) {
            return type == TokenType.EOF;
        }
        return peek().type() == type;
    }

    /**
     * Advances the current token index and returns the previous token.
     * @return the token that was just consumed
     */
    private Token advance() {
        if (!isAtEnd()) {
            current++;
        }
        return previous();
    }

    /**
     * Checks if the parser has reached the end of the token list.
     * @return true if the current token is EOF, false otherwise
     */
    private boolean isAtEnd() {
        return peek().type() == TokenType.EOF;
    }

    /**
     * Returns the current token without consuming it.
     * @return the current token
     */
    private Token peek() {
        return tokens.get(current);
    }

    /**
     * Returns the most recently consumed token.
     * @return the previous token
     */
    private Token previous() {
        return tokens.get(current - 1);
    }

    private ParseException error(Token token, String message) {
        return new ParseException(message + " at position " + token.position());
    }
}
