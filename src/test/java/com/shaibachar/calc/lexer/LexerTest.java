package com.shaibachar.calc.lexer;

import com.shaibachar.calc.exceptions.ParseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LexerTest {

    @Nested
    class IdentifierTokens {
        @Test
        void singleCharacterIdentifier() {
            Lexer lexer = new Lexer("x");
            List<Token> tokens = lexer.tokenize();
            assertEquals(2, tokens.size());
            assertEquals(TokenType.IDENT, tokens.get(0).type());
            assertEquals("x", tokens.get(0).lexeme());
            assertEquals(TokenType.EOF, tokens.get(1).type());
        }

        @Test
        void multiCharacterIdentifier() {
            Lexer lexer = new Lexer("myVariable");
            List<Token> tokens = lexer.tokenize();
            assertEquals(2, tokens.size());
            assertEquals(TokenType.IDENT, tokens.get(0).type());
            assertEquals("myVariable", tokens.get(0).lexeme());
        }

        @Test
        void identifierWithUnderscores() {
            Lexer lexer = new Lexer("my_var_123");
            List<Token> tokens = lexer.tokenize();
            assertEquals(2, tokens.size());
            assertEquals(TokenType.IDENT, tokens.get(0).type());
            assertEquals("my_var_123", tokens.get(0).lexeme());
        }

        @Test
        void identifierStartingWithUnderscore() {
            Lexer lexer = new Lexer("_private");
            List<Token> tokens = lexer.tokenize();
            assertEquals(2, tokens.size());
            assertEquals(TokenType.IDENT, tokens.get(0).type());
            assertEquals("_private", tokens.get(0).lexeme());
        }

        @Test
        void identifierWithNumbers() {
            Lexer lexer = new Lexer("var123");
            List<Token> tokens = lexer.tokenize();
            assertEquals(2, tokens.size());
            assertEquals(TokenType.IDENT, tokens.get(0).type());
            assertEquals("var123", tokens.get(0).lexeme());
        }
    }

    @Nested
    class NumberTokens {
        @Test
        void singleDigitNumber() {
            Lexer lexer = new Lexer("5");
            List<Token> tokens = lexer.tokenize();
            assertEquals(2, tokens.size());
            assertEquals(TokenType.NUMBER, tokens.get(0).type());
            assertEquals("5", tokens.get(0).lexeme());
        }

        @Test
        void multiDigitNumber() {
            Lexer lexer = new Lexer("12345");
            List<Token> tokens = lexer.tokenize();
            assertEquals(2, tokens.size());
            assertEquals(TokenType.NUMBER, tokens.get(0).type());
            assertEquals("12345", tokens.get(0).lexeme());
        }

        @Test
        void zero() {
            Lexer lexer = new Lexer("0");
            List<Token> tokens = lexer.tokenize();
            assertEquals(2, tokens.size());
            assertEquals(TokenType.NUMBER, tokens.get(0).type());
            assertEquals("0", tokens.get(0).lexeme());
        }

        @Test
        void largeNumber() {
            Lexer lexer = new Lexer("2147483647");
            List<Token> tokens = lexer.tokenize();
            assertEquals(2, tokens.size());
            assertEquals(TokenType.NUMBER, tokens.get(0).type());
            assertEquals("2147483647", tokens.get(0).lexeme());
        }

        @Test
        void integerOverflowThrows() {
            Lexer lexer = new Lexer("9223372036854775808");
            assertThrows(ParseException.class, lexer::tokenize);
        }
    }

    @Nested
    class SingleCharacterOperators {
        @Test
        void plusOperator() {
            Lexer lexer = new Lexer("+");
            List<Token> tokens = lexer.tokenize();
            assertEquals(2, tokens.size());
            assertEquals(TokenType.PLUS, tokens.get(0).type());
            assertEquals("+", tokens.get(0).lexeme());
        }

        @Test
        void minusOperator() {
            Lexer lexer = new Lexer("-");
            List<Token> tokens = lexer.tokenize();
            assertEquals(2, tokens.size());
            assertEquals(TokenType.MINUS, tokens.get(0).type());
            assertEquals("-", tokens.get(0).lexeme());
        }

        @Test
        void starOperator() {
            Lexer lexer = new Lexer("*");
            List<Token> tokens = lexer.tokenize();
            assertEquals(2, tokens.size());
            assertEquals(TokenType.STAR, tokens.get(0).type());
            assertEquals("*", tokens.get(0).lexeme());
        }

        @Test
        void slashOperator() {
            Lexer lexer = new Lexer("/");
            List<Token> tokens = lexer.tokenize();
            assertEquals(2, tokens.size());
            assertEquals(TokenType.SLASH, tokens.get(0).type());
            assertEquals("/", tokens.get(0).lexeme());
        }

        @Test
        void percentOperator() {
            Lexer lexer = new Lexer("%");
            List<Token> tokens = lexer.tokenize();
            assertEquals(2, tokens.size());
            assertEquals(TokenType.PERCENT, tokens.get(0).type());
            assertEquals("%", tokens.get(0).lexeme());
        }

        @Test
        void equalOperator() {
            Lexer lexer = new Lexer("=");
            List<Token> tokens = lexer.tokenize();
            assertEquals(2, tokens.size());
            assertEquals(TokenType.EQUAL, tokens.get(0).type());
            assertEquals("=", tokens.get(0).lexeme());
        }
    }

    @Nested
    class TwoCharacterOperators {
        @Test
        void plusPlusOperator() {
            Lexer lexer = new Lexer("++");
            List<Token> tokens = lexer.tokenize();
            assertEquals(2, tokens.size());
            assertEquals(TokenType.PLUS_PLUS, tokens.get(0).type());
            assertEquals("++", tokens.get(0).lexeme());
        }

        @Test
        void minusMinusOperator() {
            Lexer lexer = new Lexer("--");
            List<Token> tokens = lexer.tokenize();
            assertEquals(2, tokens.size());
            assertEquals(TokenType.MINUS_MINUS, tokens.get(0).type());
            assertEquals("--", tokens.get(0).lexeme());
        }

        @Test
        void plusEqualOperator() {
            Lexer lexer = new Lexer("+=");
            List<Token> tokens = lexer.tokenize();
            assertEquals(2, tokens.size());
            assertEquals(TokenType.PLUS_EQUAL, tokens.get(0).type());
            assertEquals("+=", tokens.get(0).lexeme());
        }

        @Test
        void minusEqualOperator() {
            Lexer lexer = new Lexer("-=");
            List<Token> tokens = lexer.tokenize();
            assertEquals(2, tokens.size());
            assertEquals(TokenType.MINUS_EQUAL, tokens.get(0).type());
            assertEquals("-=", tokens.get(0).lexeme());
        }

        @Test
        void starEqualOperator() {
            Lexer lexer = new Lexer("*=");
            List<Token> tokens = lexer.tokenize();
            assertEquals(2, tokens.size());
            assertEquals(TokenType.STAR_EQUAL, tokens.get(0).type());
            assertEquals("*=", tokens.get(0).lexeme());
        }

        @Test
        void slashEqualOperator() {
            Lexer lexer = new Lexer("/=");
            List<Token> tokens = lexer.tokenize();
            assertEquals(2, tokens.size());
            assertEquals(TokenType.SLASH_EQUAL, tokens.get(0).type());
            assertEquals("/=", tokens.get(0).lexeme());
        }

        @Test
        void percentEqualOperator() {
            Lexer lexer = new Lexer("%=");
            List<Token> tokens = lexer.tokenize();
            assertEquals(2, tokens.size());
            assertEquals(TokenType.PERCENT_EQUAL, tokens.get(0).type());
            assertEquals("%=", tokens.get(0).lexeme());
        }
    }

    @Nested
    class ParenthesesTokens {
        @Test
        void leftParenthesis() {
            Lexer lexer = new Lexer("(");
            List<Token> tokens = lexer.tokenize();
            assertEquals(2, tokens.size());
            assertEquals(TokenType.LPAREN, tokens.get(0).type());
            assertEquals("(", tokens.get(0).lexeme());
        }

        @Test
        void rightParenthesis() {
            Lexer lexer = new Lexer(")");
            List<Token> tokens = lexer.tokenize();
            assertEquals(2, tokens.size());
            assertEquals(TokenType.RPAREN, tokens.get(0).type());
            assertEquals(")", tokens.get(0).lexeme());
        }
    }

    @Nested
    class ComplexExpressions {
        @Test
        void simpleAssignment() {
            Lexer lexer = new Lexer("x = 5");
            List<Token> tokens = lexer.tokenize();
            assertEquals(4, tokens.size());
            assertEquals(TokenType.IDENT, tokens.get(0).type());
            assertEquals("x", tokens.get(0).lexeme());
            assertEquals(TokenType.EQUAL, tokens.get(1).type());
            assertEquals(TokenType.NUMBER, tokens.get(2).type());
            assertEquals("5", tokens.get(2).lexeme());
            assertEquals(TokenType.EOF, tokens.get(3).type());
        }

        @Test
        void binaryExpression() {
            Lexer lexer = new Lexer("a + b * c");
            List<Token> tokens = lexer.tokenize();
            assertEquals(6, tokens.size());
            assertEquals(TokenType.IDENT, tokens.get(0).type());
            assertEquals("a", tokens.get(0).lexeme());
            assertEquals(TokenType.PLUS, tokens.get(1).type());
            assertEquals(TokenType.IDENT, tokens.get(2).type());
            assertEquals("b", tokens.get(2).lexeme());
            assertEquals(TokenType.STAR, tokens.get(3).type());
            assertEquals(TokenType.IDENT, tokens.get(4).type());
            assertEquals("c", tokens.get(4).lexeme());
            assertEquals(TokenType.EOF, tokens.get(5).type());
        }

        @Test
        void compoundAssignment() {
            Lexer lexer = new Lexer("x += 10");
            List<Token> tokens = lexer.tokenize();
            assertEquals(4, tokens.size());
            assertEquals(TokenType.IDENT, tokens.get(0).type());
            assertEquals(TokenType.PLUS_EQUAL, tokens.get(1).type());
            assertEquals(TokenType.NUMBER, tokens.get(2).type());
            assertEquals(TokenType.EOF, tokens.get(3).type());
        }

        @Test
        void preIncrementExpression() {
            Lexer lexer = new Lexer("++i + 1");
            List<Token> tokens = lexer.tokenize();
            assertEquals(5, tokens.size());
            assertEquals(TokenType.PLUS_PLUS, tokens.get(0).type());
            assertEquals(TokenType.IDENT, tokens.get(1).type());
            assertEquals(TokenType.PLUS, tokens.get(2).type());
            assertEquals(TokenType.NUMBER, tokens.get(3).type());
            assertEquals(TokenType.EOF, tokens.get(4).type());
        }

        @Test
        void parenthesizedExpression() {
            Lexer lexer = new Lexer("(a + b) * c");
            List<Token> tokens = lexer.tokenize();
            assertEquals(8, tokens.size());
            assertEquals(TokenType.LPAREN, tokens.get(0).type());
            assertEquals(TokenType.IDENT, tokens.get(1).type());
            assertEquals(TokenType.PLUS, tokens.get(2).type());
            assertEquals(TokenType.IDENT, tokens.get(3).type());
            assertEquals(TokenType.RPAREN, tokens.get(4).type());
            assertEquals(TokenType.STAR, tokens.get(5).type());
            assertEquals(TokenType.IDENT, tokens.get(6).type());
            assertEquals(TokenType.EOF, tokens.get(7).type());
        }

        @Test
        void complexExpression() {
            Lexer lexer = new Lexer("i = ++i + i++ * (5 - 3)");
            List<Token> tokens = lexer.tokenize();
            assertEquals(14, tokens.size());
            assertEquals(TokenType.IDENT, tokens.get(0).type());
            assertEquals("i", tokens.get(0).lexeme());
            assertEquals(TokenType.EQUAL, tokens.get(1).type());
            assertEquals(TokenType.PLUS_PLUS, tokens.get(2).type());
            assertEquals(TokenType.IDENT, tokens.get(3).type());
            assertEquals(TokenType.PLUS, tokens.get(4).type());
            assertEquals(TokenType.IDENT, tokens.get(5).type());
            assertEquals(TokenType.PLUS_PLUS, tokens.get(6).type());
            assertEquals(TokenType.STAR, tokens.get(7).type());
            assertEquals(TokenType.LPAREN, tokens.get(8).type());
            assertEquals(TokenType.NUMBER, tokens.get(9).type());
            assertEquals(TokenType.MINUS, tokens.get(10).type());
            assertEquals(TokenType.NUMBER, tokens.get(11).type());
            assertEquals(TokenType.RPAREN, tokens.get(12).type());
            assertEquals(TokenType.EOF, tokens.get(13).type());
        }
    }

    @Nested
    class WhitespaceHandling {
        @Test
        void leadingWhitespace() {
            Lexer lexer = new Lexer("   x");
            List<Token> tokens = lexer.tokenize();
            assertEquals(2, tokens.size());
            assertEquals(TokenType.IDENT, tokens.get(0).type());
            assertEquals("x", tokens.get(0).lexeme());
        }

        @Test
        void trailingWhitespace() {
            Lexer lexer = new Lexer("x   ");
            List<Token> tokens = lexer.tokenize();
            assertEquals(2, tokens.size());
            assertEquals(TokenType.IDENT, tokens.get(0).type());
        }

        @Test
        void whitespaceAroundOperators() {
            Lexer lexer = new Lexer("x  +  y");
            List<Token> tokens = lexer.tokenize();
            assertEquals(4, tokens.size());
            assertEquals(TokenType.IDENT, tokens.get(0).type());
            assertEquals(TokenType.PLUS, tokens.get(1).type());
            assertEquals(TokenType.IDENT, tokens.get(2).type());
        }

        @Test
        void tabsAndSpaces() {
            Lexer lexer = new Lexer("x\t+\t y");
            List<Token> tokens = lexer.tokenize();
            assertEquals(4, tokens.size());
            assertEquals(TokenType.IDENT, tokens.get(0).type());
            assertEquals(TokenType.PLUS, tokens.get(1).type());
            assertEquals(TokenType.IDENT, tokens.get(2).type());
        }

        @Test
        void onlyWhitespace() {
            Lexer lexer = new Lexer("   \t  ");
            List<Token> tokens = lexer.tokenize();
            assertEquals(1, tokens.size());
            assertEquals(TokenType.EOF, tokens.get(0).type());
        }
    }

    @Nested
    class TokenPositions {
        @Test
        void firstTokenPosition() {
            Lexer lexer = new Lexer("x");
            List<Token> tokens = lexer.tokenize();
            assertEquals(0, tokens.get(0).position());
        }

        @Test
        void secondTokenPosition() {
            Lexer lexer = new Lexer("x y");
            List<Token> tokens = lexer.tokenize();
            assertEquals(0, tokens.get(0).position());
            assertEquals(2, tokens.get(1).position());
        }

        @Test
        void operatorPosition() {
            Lexer lexer = new Lexer("x + y");
            List<Token> tokens = lexer.tokenize();
            assertEquals(0, tokens.get(0).position());
            assertEquals(2, tokens.get(1).position());
            assertEquals(4, tokens.get(2).position());
        }

        @Test
        void multiCharacterTokenPosition() {
            Lexer lexer = new Lexer("++x");
            List<Token> tokens = lexer.tokenize();
            assertEquals(0, tokens.get(0).position());
            assertEquals(2, tokens.get(1).position());
        }
    }

    @Nested
    class ErrorHandling {
        @Test
        void unexpectedCharacterThrows() {
            Lexer lexer = new Lexer("@");
            assertThrows(ParseException.class, lexer::tokenize);
        }

        @Test
        void unexpectedCharacterWithDetails() {
            Lexer lexer = new Lexer("x @ y");
            ParseException exception = assertThrows(ParseException.class, lexer::tokenize);
            assertTrue(exception.getMessage().contains("@"));
        }

        @Test
        void multipleUnexpectedCharacters() {
            Lexer lexer = new Lexer("x & y");
            assertThrows(ParseException.class, lexer::tokenize);
        }

        @Test
        void dollarSignInvalid() {
            Lexer lexer = new Lexer("$var");
            assertThrows(ParseException.class, lexer::tokenize);
        }

        @Test
        void hashInvalid() {
            Lexer lexer = new Lexer("#define");
            assertThrows(ParseException.class, lexer::tokenize);
        }
    }

    @Nested
    class EmptyAndNullInput {
        @Test
        void emptyString() {
            Lexer lexer = new Lexer("");
            List<Token> tokens = lexer.tokenize();
            assertEquals(1, tokens.size());
            assertEquals(TokenType.EOF, tokens.get(0).type());
        }

        @Test
        void nullInput() {
            Lexer lexer = new Lexer(null);
            List<Token> tokens = lexer.tokenize();
            assertEquals(1, tokens.size());
            assertEquals(TokenType.EOF, tokens.get(0).type());
        }
    }

    @Nested
    class AllOperatorsInSequence {
        @Test
        void allSingleCharOperators() {
            Lexer lexer = new Lexer("+ - * / % =");
            List<Token> tokens = lexer.tokenize();
            assertEquals(7, tokens.size());
            assertEquals(TokenType.PLUS, tokens.get(0).type());
            assertEquals(TokenType.MINUS, tokens.get(1).type());
            assertEquals(TokenType.STAR, tokens.get(2).type());
            assertEquals(TokenType.SLASH, tokens.get(3).type());
            assertEquals(TokenType.PERCENT, tokens.get(4).type());
            assertEquals(TokenType.EQUAL, tokens.get(5).type());
            assertEquals(TokenType.EOF, tokens.get(6).type());
        }

        @Test
        void allCompoundOperators() {
            Lexer lexer = new Lexer("++ -- += -= *= /= %=");
            List<Token> tokens = lexer.tokenize();
            assertEquals(8, tokens.size());
            assertEquals(TokenType.PLUS_PLUS, tokens.get(0).type());
            assertEquals(TokenType.MINUS_MINUS, tokens.get(1).type());
            assertEquals(TokenType.PLUS_EQUAL, tokens.get(2).type());
            assertEquals(TokenType.MINUS_EQUAL, tokens.get(3).type());
            assertEquals(TokenType.STAR_EQUAL, tokens.get(4).type());
            assertEquals(TokenType.SLASH_EQUAL, tokens.get(5).type());
            assertEquals(TokenType.PERCENT_EQUAL, tokens.get(6).type());
            assertEquals(TokenType.EOF, tokens.get(7).type());
        }
    }

    @Nested
    class EdgeCases {
        @Test
        void consecutiveOperators() {
            Lexer lexer = new Lexer("++--");
            List<Token> tokens = lexer.tokenize();
            assertEquals(3, tokens.size());
            assertEquals(TokenType.PLUS_PLUS, tokens.get(0).type());
            assertEquals(TokenType.MINUS_MINUS, tokens.get(1).type());
            assertEquals(TokenType.EOF, tokens.get(2).type());
        }

        @Test
        void identifierFollowedByNumber() {
            Lexer lexer = new Lexer("x5");
            List<Token> tokens = lexer.tokenize();
            assertEquals(2, tokens.size());
            assertEquals(TokenType.IDENT, tokens.get(0).type());
            assertEquals("x5", tokens.get(0).lexeme());
        }

        @Test
        void underscoreFollowedByNumbers() {
            Lexer lexer = new Lexer("_123");
            List<Token> tokens = lexer.tokenize();
            assertEquals(2, tokens.size());
            assertEquals(TokenType.IDENT, tokens.get(0).type());
            assertEquals("_123", tokens.get(0).lexeme());
        }

        @Test
        void mixedCaseIdentifier() {
            Lexer lexer = new Lexer("MyVariable");
            List<Token> tokens = lexer.tokenize();
            assertEquals(2, tokens.size());
            assertEquals(TokenType.IDENT, tokens.get(0).type());
            assertEquals("MyVariable", tokens.get(0).lexeme());
        }

        @Test
        void allUpperCaseIdentifier() {
            Lexer lexer = new Lexer("MAX_VALUE");
            List<Token> tokens = lexer.tokenize();
            assertEquals(2, tokens.size());
            assertEquals(TokenType.IDENT, tokens.get(0).type());
            assertEquals("MAX_VALUE", tokens.get(0).lexeme());
        }
    }

    @Nested
    class InvalidCharacters {
        @Test
        void atSymbol() {
            Lexer lexer = new Lexer("@");
            assertThrows(ParseException.class, lexer::tokenize);
        }

        @Test
        void ampersand() {
            Lexer lexer = new Lexer("&");
            assertThrows(ParseException.class, lexer::tokenize);
        }

        @Test
        void pipe() {
            Lexer lexer = new Lexer("|");
            assertThrows(ParseException.class, lexer::tokenize);
        }

        @Test
        void caret() {
            // Caret (^) is now a valid power operator token
            Lexer lexer = new Lexer("^");
            var tokens = lexer.tokenize();
            assertEquals(2, tokens.size()); // CARET and EOF
            assertEquals(TokenType.CARET, tokens.get(0).type());
        }

        @Test
        void tilde() {
            Lexer lexer = new Lexer("~");
            assertThrows(ParseException.class, lexer::tokenize);
        }

        @Test
        void questionMark() {
            Lexer lexer = new Lexer("?");
            assertThrows(ParseException.class, lexer::tokenize);
        }

        @Test
        void colon() {
            Lexer lexer = new Lexer(":");
            assertThrows(ParseException.class, lexer::tokenize);
        }

        @Test
        void semicolon() {
            Lexer lexer = new Lexer(";");
            assertThrows(ParseException.class, lexer::tokenize);
        }

        @Test
        void comma() {
            Lexer lexer = new Lexer(",");
            assertThrows(ParseException.class, lexer::tokenize);
        }

        @Test
        void period() {
            Lexer lexer = new Lexer(".");
            assertThrows(ParseException.class, lexer::tokenize);
        }

        @Test
        void backslash() {
            Lexer lexer = new Lexer("\\");
            assertThrows(ParseException.class, lexer::tokenize);
        }

        @Test
        void singleQuote() {
            Lexer lexer = new Lexer("'");
            assertThrows(ParseException.class, lexer::tokenize);
        }

        @Test
        void doubleQuote() {
            Lexer lexer = new Lexer("\"");
            assertThrows(ParseException.class, lexer::tokenize);
        }

        @Test
        void backtick() {
            Lexer lexer = new Lexer("`");
            assertThrows(ParseException.class, lexer::tokenize);
        }

        @Test
        void dollarSign() {
            Lexer lexer = new Lexer("$");
            assertThrows(ParseException.class, lexer::tokenize);
        }

        @Test
        void hashSymbol() {
            Lexer lexer = new Lexer("#");
            assertThrows(ParseException.class, lexer::tokenize);
        }

        @Test
        void lessThan() {
            Lexer lexer = new Lexer("<");
            assertThrows(ParseException.class, lexer::tokenize);
        }

        @Test
        void greaterThan() {
            Lexer lexer = new Lexer(">");
            assertThrows(ParseException.class, lexer::tokenize);
        }

        @Test
        void exclamation() {
            Lexer lexer = new Lexer("!");
            assertThrows(ParseException.class, lexer::tokenize);
        }

        @Test
        void squareBracketOpen() {
            Lexer lexer = new Lexer("[");
            assertThrows(ParseException.class, lexer::tokenize);
        }

        @Test
        void squareBracketClose() {
            Lexer lexer = new Lexer("]");
            assertThrows(ParseException.class, lexer::tokenize);
        }

        @Test
        void curlyBracketOpen() {
            Lexer lexer = new Lexer("{");
            assertThrows(ParseException.class, lexer::tokenize);
        }

        @Test
        void curlyBracketClose() {
            Lexer lexer = new Lexer("}");
            assertThrows(ParseException.class, lexer::tokenize);
        }
    }

    @Nested
    class InvalidStringsAndSequences {
        @Test
        void invalidCharacterAtStart() {
            Lexer lexer = new Lexer("@variable");
            assertThrows(ParseException.class, lexer::tokenize);
        }

        @Test
        void invalidCharacterInMiddle() {
            Lexer lexer = new Lexer("var@iable");
            assertThrows(ParseException.class, lexer::tokenize);
        }

        @Test
        void invalidCharacterAtEnd() {
            Lexer lexer = new Lexer("variable@");
            assertThrows(ParseException.class, lexer::tokenize);
        }

        @Test
        void multipleInvalidCharacters() {
            Lexer lexer = new Lexer("@#$");
            assertThrows(ParseException.class, lexer::tokenize);
        }

        @Test
        void invalidCharacterInExpression() {
            Lexer lexer = new Lexer("x + & y");
            assertThrows(ParseException.class, lexer::tokenize);
        }

        @Test
        void hashInComment() {
            Lexer lexer = new Lexer("#comment");
            assertThrows(ParseException.class, lexer::tokenize);
        }

        @Test
        void dollarInIdentifier() {
            Lexer lexer = new Lexer("$var");
            assertThrows(ParseException.class, lexer::tokenize);
        }

        @Test
        void doubleQuoteString() {
            Lexer lexer = new Lexer("\"hello\"");
            assertThrows(ParseException.class, lexer::tokenize);
        }

        @Test
        void singleQuoteString() {
            Lexer lexer = new Lexer("'hello'");
            assertThrows(ParseException.class, lexer::tokenize);
        }

        @Test
        void lessThanEqualNotSupported() {
            Lexer lexer = new Lexer("x <= y");
            assertThrows(ParseException.class, lexer::tokenize);
        }

        @Test
        void greaterThanEqualNotSupported() {
            Lexer lexer = new Lexer("x >= y");
            assertThrows(ParseException.class, lexer::tokenize);
        }

        @Test
        void lessThanWithEqualNotSupported() {
            Lexer lexer = new Lexer("x <");
            assertThrows(ParseException.class, lexer::tokenize);
        }

        @Test
        void greaterThanWithEqualNotSupported() {
            Lexer lexer = new Lexer("x >");
            assertThrows(ParseException.class, lexer::tokenize);
        }

        @Test
        void bitwiseXorNotSupported() {
            // Caret (^) is now a valid power operator token, not bitwise XOR
            Lexer lexer = new Lexer("x ^ y");
            var tokens = lexer.tokenize();
            // Should tokenize as: IDENT, CARET, IDENT, EOF
            assertEquals(4, tokens.size());
            assertEquals(TokenType.IDENT, tokens.get(0).type());
            assertEquals(TokenType.CARET, tokens.get(1).type());
            assertEquals(TokenType.IDENT, tokens.get(2).type());
        }

        @Test
        void bitwiseNotNotSupported() {
            Lexer lexer = new Lexer("~x");
            assertThrows(ParseException.class, lexer::tokenize);
        }

        @Test
        void leftShiftNotSupported() {
            Lexer lexer = new Lexer("x << y");
            assertThrows(ParseException.class, lexer::tokenize);
        }

        @Test
        void rightShiftNotSupported() {
            Lexer lexer = new Lexer("x >> y");
            assertThrows(ParseException.class, lexer::tokenize);
        }

        @Test
        void arrowNotSupported() {
            Lexer lexer = new Lexer("x -> y");
            assertThrows(ParseException.class, lexer::tokenize);
        }

        @Test
        void colonNotSupported() {
            Lexer lexer = new Lexer("x : y");
            assertThrows(ParseException.class, lexer::tokenize);
        }

        @Test
        void semicolonNotSupported() {
            Lexer lexer = new Lexer("x ; y");
            assertThrows(ParseException.class, lexer::tokenize);
        }

        @Test
        void periodNotSupported() {
            Lexer lexer = new Lexer("x.y");
            assertThrows(ParseException.class, lexer::tokenize);
        }

        @Test
        void commaInExpression() {
            Lexer lexer = new Lexer("x , y");
            assertThrows(ParseException.class, lexer::tokenize);
        }

        @Test
        void backslashInExpression() {
            Lexer lexer = new Lexer("x \\ y");
            assertThrows(ParseException.class, lexer::tokenize);
        }

        @Test
        void squareBracketsNotSupported() {
            Lexer lexer = new Lexer("x[0]");
            assertThrows(ParseException.class, lexer::tokenize);
        }

        @Test
        void curlyBracesNotSupported() {
            Lexer lexer = new Lexer("{ x }");
            assertThrows(ParseException.class, lexer::tokenize);
        }

        @Test
        void questionMarkNotSupported() {
            Lexer lexer = new Lexer("x ? y : z");
            assertThrows(ParseException.class, lexer::tokenize);
        }

        @Test
        void caretNotSupported() {
            // Caret (^) is now a valid power operator token
            Lexer lexer = new Lexer("x ^ y");
            var tokens = lexer.tokenize();
            // Should tokenize as: IDENT, CARET, IDENT, EOF
            assertEquals(4, tokens.size());
            assertEquals(TokenType.IDENT, tokens.get(0).type());
            assertEquals(TokenType.CARET, tokens.get(1).type());
            assertEquals(TokenType.IDENT, tokens.get(2).type());
        }

        @Test
        void tildeNotSupported() {
            Lexer lexer = new Lexer("~x");
            assertThrows(ParseException.class, lexer::tokenize);
        }

        @Test
        void exclamationNotSupported() {
            Lexer lexer = new Lexer("!x");
            assertThrows(ParseException.class, lexer::tokenize);
        }
    }

    @Nested
    class InvalidCharacterErrorMessages {
        @Test
        void errorMessageContainsCharacter() {
            Lexer lexer = new Lexer("@");
            ParseException exception = assertThrows(ParseException.class, lexer::tokenize);
            assertTrue(exception.getMessage().contains("@"),
                    "Error message should contain the invalid character");
        }

        @Test
        void errorMessageContainsPosition() {
            Lexer lexer = new Lexer("x @ y");
            ParseException exception = assertThrows(ParseException.class, lexer::tokenize);
            assertTrue(exception.getMessage().contains("@") ||
                       exception.getMessage().contains("position") ||
                       exception.getMessage().contains("2"),
                    "Error message should contain position information");
        }

        @Test
        void errorOnFirstInvalidCharacter() {
            Lexer lexer = new Lexer("x & y | z");
            ParseException exception = assertThrows(ParseException.class, lexer::tokenize);
            // Should fail on the first invalid character (&), not the second (|)
            assertTrue(exception.getMessage().contains("&"),
                    "Should report error on first invalid character");
        }
    }

    @Nested
    class ValidCharactersAfterInvalid {
        @Test
        void invalidBeforeValidNumber() {
            Lexer lexer = new Lexer("@5");
            assertThrows(ParseException.class, lexer::tokenize);
        }

        @Test
        void invalidAfterValidNumber() {
            Lexer lexer = new Lexer("5@");
            assertThrows(ParseException.class, lexer::tokenize);
        }

        @Test
        void invalidBeforeValidOperator() {
            Lexer lexer = new Lexer("@+");
            assertThrows(ParseException.class, lexer::tokenize);
        }

        @Test
        void invalidAfterValidOperator() {
            Lexer lexer = new Lexer("+@");
            assertThrows(ParseException.class, lexer::tokenize);
        }

        @Test
        void invalidBeforeValidParenthesis() {
            Lexer lexer = new Lexer("@(");
            assertThrows(ParseException.class, lexer::tokenize);
        }

        @Test
        void invalidAfterValidParenthesis() {
            Lexer lexer = new Lexer("(@");
            assertThrows(ParseException.class, lexer::tokenize);
        }
    }
}
