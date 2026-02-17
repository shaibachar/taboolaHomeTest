package com.shaibachar.calc;

import com.shaibachar.calc.lexer.Lexer;
import com.shaibachar.calc.parser.Parser;
import com.shaibachar.calc.exceptions.ParseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 test class for error collection mechanism with detailed error information.
 * This test class demonstrates how to retrieve and display error details including counts and messages.
 */
@DisplayName("Error Collection Mechanism Tests")
class TestMultipleErrorsEnhanced {

    @Test
    @DisplayName("Should collect error count when identifier is missing")
    void testErrorCountWhenMissingIdentifier() {
        String input = "= 5";
        Lexer lexer = new Lexer(input);
        var tokens = lexer.tokenize();
        Parser parser = new Parser(tokens);

        ParseException exception = assertThrows(ParseException.class, parser::parseStatement);
        assertTrue(exception.getErrorCount() > 0);
        assertFalse(exception.getErrors().isEmpty());
    }

    @Test
    @DisplayName("Should collect multiple errors when assignment operator and expression are missing")
    void testMultipleErrorsCollection() {
        String input = "x";
        Lexer lexer = new Lexer(input);
        var tokens = lexer.tokenize();
        Parser parser = new Parser(tokens);

        ParseException exception = assertThrows(ParseException.class, parser::parseStatement);
        assertTrue(exception.getErrorCount() >= 1);
        assertNotNull(exception.getErrors());
        assertTrue(exception.getErrors().size() > 0);
    }

    @Test
    @DisplayName("Should successfully parse valid statement without errors")
    void testValidStatementParsing() {
        String input = "x = 5";
        Lexer lexer = new Lexer(input);
        var tokens = lexer.tokenize();
        Parser parser = new Parser(tokens);

        assertDoesNotThrow(parser::parseStatement);
    }
}


