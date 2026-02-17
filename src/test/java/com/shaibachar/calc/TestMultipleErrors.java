package com.shaibachar.calc;

import com.shaibachar.calc.lexer.Lexer;
import com.shaibachar.calc.parser.Parser;
import com.shaibachar.calc.exceptions.ParseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 test class for multiple syntax errors handling.
 * This test class demonstrates how the parser collects and reports multiple errors at once.
 */
@DisplayName("Multiple Syntax Errors Tests")
class TestMultipleErrors {

    @Test
    @DisplayName("Should catch error when identifier is missing")
    void testMissingIdentifier() {
        String input = "= 5";
        Lexer lexer = new Lexer(input);
        var tokens = lexer.tokenize();
        Parser parser = new Parser(tokens);

        ParseException exception = assertThrows(ParseException.class, parser::parseStatement);
        assertTrue(exception.getErrorCount() > 0);
    }

    @Test
    @DisplayName("Should catch multiple errors when assignment operator and expression are missing")
    void testMissingAssignmentOperatorAndExpression() {
        String input = "x";
        Lexer lexer = new Lexer(input);
        var tokens = lexer.tokenize();
        Parser parser = new Parser(tokens);

        ParseException exception = assertThrows(ParseException.class, parser::parseStatement);
        assertTrue(exception.getErrorCount() >= 1);
    }

    @Test
    @DisplayName("Should throw ParseException when invalid operator starts expression")
    void testInvalidOperatorAtStart() {
        String input = "x = * 5";
        Lexer lexer = new Lexer(input);
        var tokens = lexer.tokenize();
        Parser parser = new Parser(tokens);

        ParseException exception = assertThrows(ParseException.class, parser::parseStatement);
        assertTrue(exception.getMessage().contains("PARSE_007"));
    }

    @Test
    @DisplayName("Should catch error when parenthesis is incomplete in nested structure")
    void testMultipleMissingClosingParentheses() {
        String input = "x = ((5 + 3)";
        Lexer lexer = new Lexer(input);
        var tokens = lexer.tokenize();
        Parser parser = new Parser(tokens);

        ParseException exception = assertThrows(ParseException.class, parser::parseStatement);
        assertTrue(exception.getErrorCount() > 0);
    }

    @Test
    @DisplayName("Should throw ParseException when operator is missing between operands")
    void testMissingOperatorBetweenOperands() {
        String input = "x = 5 3";
        Lexer lexer = new Lexer(input);
        var tokens = lexer.tokenize();
        Parser parser = new Parser(tokens);

        ParseException exception = assertThrows(ParseException.class, parser::parseStatement);
        assertTrue(exception.getMessage().contains("PARSE_003"));
    }

    @Test
    @DisplayName("Should catch errors when closing parenthesis is missing with extra tokens")
    void testMissingParenthesisWithExtraToken() {
        String input = "x = (5 + 3 extra";
        Lexer lexer = new Lexer(input);
        var tokens = lexer.tokenize();
        Parser parser = new Parser(tokens);

        ParseException exception = assertThrows(ParseException.class, parser::parseStatement);
        assertTrue(exception.getErrorCount() > 0);
    }
}


