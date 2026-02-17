package com.shaibachar.calc;

import com.shaibachar.calc.lexer.Lexer;
import com.shaibachar.calc.parser.Parser;
import com.shaibachar.calc.exceptions.ParseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 test class for missing/extra parenthesis error handling.
 * This test class demonstrates how the parser handles various parenthesis-related errors.
 */
@DisplayName("Missing/Extra Parenthesis Tests")
class TestMissingParenthesis {

    @Test
    @DisplayName("Should throw ParseException when closing parenthesis is missing")
    void testMissingClosingParenthesis() {
        String input = "x = (5 + 3";
        Lexer lexer = new Lexer(input);
        var tokens = lexer.tokenize();
        Parser parser = new Parser(tokens);

        ParseException exception = assertThrows(ParseException.class, parser::parseStatement);
        assertTrue(exception.getMessage().contains("PARSE_006"));
    }

    @Test
    @DisplayName("Should throw ParseException when extra closing parenthesis is present")
    void testExtraClosingParenthesis() {
        String input = "x = (5 + 3))";
        Lexer lexer = new Lexer(input);
        var tokens = lexer.tokenize();
        Parser parser = new Parser(tokens);

        ParseException exception = assertThrows(ParseException.class, parser::parseStatement);
        assertTrue(exception.getMessage().contains("PARSE_003"));
    }

    @Test
    @DisplayName("Should throw ParseException when nested parenthesis is incomplete")
    void testNestedMissingClosingParenthesis() {
        String input = "x = ((5 + 3)";
        Lexer lexer = new Lexer(input);
        var tokens = lexer.tokenize();
        Parser parser = new Parser(tokens);

        ParseException exception = assertThrows(ParseException.class, parser::parseStatement);
        assertTrue(exception.getMessage().contains("PARSE_006"));
    }

    @Test
    @DisplayName("Should successfully parse valid parenthesized expression")
    void testValidParenthesizedExpression() {
        String input = "x = (5 + 3)";
        Lexer lexer = new Lexer(input);
        var tokens = lexer.tokenize();
        Parser parser = new Parser(tokens);

        assertDoesNotThrow(parser::parseStatement);
    }

    @Test
    @DisplayName("Should throw ParseException when closing parenthesis is missing opening")
    void testExtraClosingParenthesisNoOpening() {
        String input = "x = 5 + 3)";
        Lexer lexer = new Lexer(input);
        var tokens = lexer.tokenize();
        Parser parser = new Parser(tokens);

        ParseException exception = assertThrows(ParseException.class, parser::parseStatement);
        assertTrue(exception.getMessage().contains("PARSE_003"));
    }
}


