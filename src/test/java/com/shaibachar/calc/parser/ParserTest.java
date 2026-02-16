package com.shaibachar.calc.parser;

import com.shaibachar.calc.exceptions.ParseException;
import com.shaibachar.calc.lexer.Lexer;
import com.shaibachar.calc.lexer.Token;
import com.shaibachar.calc.parser.expr.*;
import com.shaibachar.calc.parser.stmt.AssignStmt;
import com.shaibachar.calc.parser.stmt.Stmt;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    private Parser createParser(String input) {
        List<Token> tokens = new Lexer(input).tokenize();
        return new Parser(tokens);
    }

    @Nested
    class ParseStatementTests {
        @Test
        void simpleAssignment() {
            Parser parser = createParser("x = 5");
            Stmt stmt = parser.parseStatement();
            assertNotNull(stmt);
            assertInstanceOf(AssignStmt.class, stmt);
            AssignStmt assignStmt = (AssignStmt) stmt;
            assertEquals("x", assignStmt.name());
            assertEquals(AssignOp.ASSIGN, assignStmt.op());
            assertInstanceOf(LiteralExpr.class, assignStmt.expr());
        }

        @Test
        void compoundAssignmentPlus() {
            Parser parser = createParser("x += 10");
            AssignStmt stmt = (AssignStmt) parser.parseStatement();
            assertEquals("x", stmt.name());
            assertEquals(AssignOp.PLUS_ASSIGN, stmt.op());
        }

        @Test
        void compoundAssignmentMinus() {
            Parser parser = createParser("x -= 5");
            AssignStmt stmt = (AssignStmt) parser.parseStatement();
            assertEquals(AssignOp.MINUS_ASSIGN, stmt.op());
        }

        @Test
        void compoundAssignmentMul() {
            Parser parser = createParser("x *= 3");
            AssignStmt stmt = (AssignStmt) parser.parseStatement();
            assertEquals(AssignOp.MUL_ASSIGN, stmt.op());
        }

        @Test
        void compoundAssignmentDiv() {
            Parser parser = createParser("x /= 2");
            AssignStmt stmt = (AssignStmt) parser.parseStatement();
            assertEquals(AssignOp.DIV_ASSIGN, stmt.op());
        }

        @Test
        void compoundAssignmentMod() {
            Parser parser = createParser("x %= 4");
            AssignStmt stmt = (AssignStmt) parser.parseStatement();
            assertEquals(AssignOp.MOD_ASSIGN, stmt.op());
        }

        @Test
        void statementMissingIdentifier() {
            Parser parser = createParser("= 5");
            assertThrows(ParseException.class, parser::parseStatement);
        }

        @Test
        void statementMissingOperator() {
            Parser parser = createParser("x 5");
            assertThrows(ParseException.class, parser::parseStatement);
        }

        @Test
        void statementMissingExpression() {
            Parser parser = createParser("x =");
            assertThrows(ParseException.class, parser::parseStatement);
        }

        @Test
        void statementWithExtraTokens() {
            Parser parser = createParser("x = 5 + 3 y");
            assertThrows(ParseException.class, parser::parseStatement);
        }

        @Test
        void complexExpressionStatement() {
            Parser parser = createParser("i = ++i + i++ * (5 - 3)");
            AssignStmt stmt = (AssignStmt) parser.parseStatement();
            assertEquals("i", stmt.name());
            assertInstanceOf(BinaryExpr.class, stmt.expr());
        }
    }

    @Nested
    class AdditiveExpressionTests {
        @Test
        void literalOnly() {
            Parser parser = createParser("x = 42");
            AssignStmt stmt = (AssignStmt) parser.parseStatement();
            assertInstanceOf(LiteralExpr.class, stmt.expr());
            LiteralExpr lit = (LiteralExpr) stmt.expr();
            assertEquals(42L, lit.value());
        }

        @Test
        void singleAddition() {
            Parser parser = createParser("x = 5 + 3");
            AssignStmt stmt = (AssignStmt) parser.parseStatement();
            BinaryExpr expr = (BinaryExpr) stmt.expr();
            assertEquals(BinaryOp.ADD, expr.op());
        }

        @Test
        void singleSubtraction() {
            Parser parser = createParser("x = 5 - 3");
            AssignStmt stmt = (AssignStmt) parser.parseStatement();
            BinaryExpr expr = (BinaryExpr) stmt.expr();
            assertEquals(BinaryOp.SUB, expr.op());
        }

        @Test
        void multipleAdditions() {
            Parser parser = createParser("x = 1 + 2 + 3");
            AssignStmt stmt = (AssignStmt) parser.parseStatement();
            BinaryExpr expr = (BinaryExpr) stmt.expr();
            assertEquals(BinaryOp.ADD, expr.op());
            assertInstanceOf(BinaryExpr.class, expr.left());
            assertInstanceOf(LiteralExpr.class, expr.right());
        }

        @Test
        void multipleSubtractions() {
            Parser parser = createParser("x = 10 - 5 - 2");
            AssignStmt stmt = (AssignStmt) parser.parseStatement();
            BinaryExpr expr = (BinaryExpr) stmt.expr();
            assertEquals(BinaryOp.SUB, expr.op());
        }

        @Test
        void mixedAdditionSubtraction() {
            Parser parser = createParser("x = 5 + 3 - 2");
            AssignStmt stmt = (AssignStmt) parser.parseStatement();
            BinaryExpr expr = (BinaryExpr) stmt.expr();
            assertEquals(BinaryOp.SUB, expr.op());
            assertInstanceOf(BinaryExpr.class, expr.left());
        }

        @Test
        void additionWithMultiplication() {
            Parser parser = createParser("x = 2 + 3 * 4");
            AssignStmt stmt = (AssignStmt) parser.parseStatement();
            BinaryExpr expr = (BinaryExpr) stmt.expr();
            assertEquals(BinaryOp.ADD, expr.op());
            assertInstanceOf(LiteralExpr.class, expr.left());
            assertInstanceOf(BinaryExpr.class, expr.right());
        }
    }

    @Nested
    class MultiplicativeExpressionTests {
        @Test
        void singleMultiplication() {
            Parser parser = createParser("x = 5 * 3");
            AssignStmt stmt = (AssignStmt) parser.parseStatement();
            BinaryExpr expr = (BinaryExpr) stmt.expr();
            assertEquals(BinaryOp.MUL, expr.op());
        }

        @Test
        void singleDivision() {
            Parser parser = createParser("x = 10 / 2");
            AssignStmt stmt = (AssignStmt) parser.parseStatement();
            BinaryExpr expr = (BinaryExpr) stmt.expr();
            assertEquals(BinaryOp.DIV, expr.op());
        }

        @Test
        void singleModulo() {
            Parser parser = createParser("x = 10 % 3");
            AssignStmt stmt = (AssignStmt) parser.parseStatement();
            BinaryExpr expr = (BinaryExpr) stmt.expr();
            assertEquals(BinaryOp.MOD, expr.op());
        }

        @Test
        void multipleMultiplications() {
            Parser parser = createParser("x = 2 * 3 * 4");
            AssignStmt stmt = (AssignStmt) parser.parseStatement();
            BinaryExpr expr = (BinaryExpr) stmt.expr();
            assertEquals(BinaryOp.MUL, expr.op());
            assertInstanceOf(BinaryExpr.class, expr.left());
        }

        @Test
        void multiplyDivideDivide() {
            Parser parser = createParser("x = 20 / 2 / 5");
            AssignStmt stmt = (AssignStmt) parser.parseStatement();
            BinaryExpr expr = (BinaryExpr) stmt.expr();
            assertEquals(BinaryOp.DIV, expr.op());
        }

        @Test
        void multiplicationBeforeAddition() {
            Parser parser = createParser("x = 2 + 3 * 4");
            AssignStmt stmt = (AssignStmt) parser.parseStatement();
            BinaryExpr expr = (BinaryExpr) stmt.expr();
            assertEquals(BinaryOp.ADD, expr.op());
            assertInstanceOf(BinaryExpr.class, expr.right());
            BinaryExpr mulExpr = (BinaryExpr) expr.right();
            assertEquals(BinaryOp.MUL, mulExpr.op());
        }

        @Test
        void precedenceMultiplyAddMultiply() {
            Parser parser = createParser("x = 2 * 3 + 4 * 5");
            AssignStmt stmt = (AssignStmt) parser.parseStatement();
            BinaryExpr expr = (BinaryExpr) stmt.expr();
            assertEquals(BinaryOp.ADD, expr.op());
            assertInstanceOf(BinaryExpr.class, expr.left());
            assertInstanceOf(BinaryExpr.class, expr.right());
        }
    }

    @Nested
    class UnaryExpressionTests {
        @Test
        void unaryPlus() {
            Parser parser = createParser("x = +5");
            AssignStmt stmt = (AssignStmt) parser.parseStatement();
            UnaryExpr expr = (UnaryExpr) stmt.expr();
            assertEquals(UnaryOp.PLUS, expr.op());
            assertInstanceOf(LiteralExpr.class, expr.expr());
        }

        @Test
        void unaryMinus() {
            Parser parser = createParser("x = -5");
            AssignStmt stmt = (AssignStmt) parser.parseStatement();
            UnaryExpr expr = (UnaryExpr) stmt.expr();
            assertEquals(UnaryOp.MINUS, expr.op());
        }

        @Test
        void preIncrement() {
            Parser parser = createParser("x = ++i");
            AssignStmt stmt = (AssignStmt) parser.parseStatement();
            UnaryExpr expr = (UnaryExpr) stmt.expr();
            assertEquals(UnaryOp.PRE_INC, expr.op());
            assertInstanceOf(VarExpr.class, expr.expr());
        }

        @Test
        void preDecrement() {
            Parser parser = createParser("x = --i");
            AssignStmt stmt = (AssignStmt) parser.parseStatement();
            UnaryExpr expr = (UnaryExpr) stmt.expr();
            assertEquals(UnaryOp.PRE_DEC, expr.op());
        }

        @Test
        void doubleUnaryMinus() {
            Parser parser = createParser("x = --5");
            AssignStmt stmt = (AssignStmt) parser.parseStatement();
            UnaryExpr expr = (UnaryExpr) stmt.expr();
            // -- is tokenized as MINUS_MINUS (pre-decrement), not two MINUS operators
            assertEquals(UnaryOp.PRE_DEC, expr.op());
            assertInstanceOf(LiteralExpr.class, expr.expr());
        }

        @Test
        void unaryMinusWithExpression() {
            Parser parser = createParser("x = -(2 + 3)");
            AssignStmt stmt = (AssignStmt) parser.parseStatement();
            UnaryExpr expr = (UnaryExpr) stmt.expr();
            assertEquals(UnaryOp.MINUS, expr.op());
            assertInstanceOf(BinaryExpr.class, expr.expr());
        }

        @Test
        void preIncrementWithMultiplication() {
            Parser parser = createParser("x = ++i * 2");
            AssignStmt stmt = (AssignStmt) parser.parseStatement();
            BinaryExpr expr = (BinaryExpr) stmt.expr();
            assertEquals(BinaryOp.MUL, expr.op());
            assertInstanceOf(UnaryExpr.class, expr.left());
        }

        @Test
        void unaryPlusNegativeNumber() {
            Parser parser = createParser("x = +(-5)");
            AssignStmt stmt = (AssignStmt) parser.parseStatement();
            UnaryExpr expr = (UnaryExpr) stmt.expr();
            assertEquals(UnaryOp.PLUS, expr.op());
        }
    }

    @Nested
    class PostfixExpressionTests {
        @Test
        void postIncrement() {
            Parser parser = createParser("x = i++");
            AssignStmt stmt = (AssignStmt) parser.parseStatement();
            PostfixExpr expr = (PostfixExpr) stmt.expr();
            assertEquals(PostfixOp.POST_INC, expr.op());
            assertInstanceOf(VarExpr.class, expr.expr());
        }

        @Test
        void postDecrement() {
            Parser parser = createParser("x = i--");
            AssignStmt stmt = (AssignStmt) parser.parseStatement();
            PostfixExpr expr = (PostfixExpr) stmt.expr();
            assertEquals(PostfixOp.POST_DEC, expr.op());
        }

        @Test
        void postIncrementWithAddition() {
            Parser parser = createParser("x = i++ + 5");
            AssignStmt stmt = (AssignStmt) parser.parseStatement();
            BinaryExpr expr = (BinaryExpr) stmt.expr();
            assertEquals(BinaryOp.ADD, expr.op());
            assertInstanceOf(PostfixExpr.class, expr.left());
        }

        @Test
        void postIncrementWithMultiplication() {
            Parser parser = createParser("x = i++ * 2");
            AssignStmt stmt = (AssignStmt) parser.parseStatement();
            BinaryExpr expr = (BinaryExpr) stmt.expr();
            assertInstanceOf(PostfixExpr.class, expr.left());
        }

        @Test
        void literalWithoutPostfix() {
            Parser parser = createParser("x = 5");
            AssignStmt stmt = (AssignStmt) parser.parseStatement();
            assertInstanceOf(LiteralExpr.class, stmt.expr());
        }

        @Test
        void multiplePostIncrementsInExpression() {
            Parser parser = createParser("x = i++ + i++");
            AssignStmt stmt = (AssignStmt) parser.parseStatement();
            BinaryExpr expr = (BinaryExpr) stmt.expr();
            assertInstanceOf(PostfixExpr.class, expr.left());
            assertInstanceOf(PostfixExpr.class, expr.right());
        }
    }

    @Nested
    class PrimaryExpressionTests {
        @Test
        void numberLiteral() {
            Parser parser = createParser("x = 42");
            AssignStmt stmt = (AssignStmt) parser.parseStatement();
            LiteralExpr expr = (LiteralExpr) stmt.expr();
            assertEquals(42L, expr.value());
        }

        @Test
        void floatingPointLiteral() {
            Parser parser = createParser("x = 3.14");
            AssignStmt stmt = (AssignStmt) parser.parseStatement();
            LiteralExpr expr = (LiteralExpr) stmt.expr();
            assertEquals(3.14, expr.value().doubleValue(), 1e-9);
        }

        @Test
        void zeroLiteral() {
            Parser parser = createParser("x = 0");
            AssignStmt stmt = (AssignStmt) parser.parseStatement();
            LiteralExpr expr = (LiteralExpr) stmt.expr();
            assertEquals(0L, expr.value());
        }

        @Test
        void largeLiteral() {
            Parser parser = createParser("x = 2147483647");
            AssignStmt stmt = (AssignStmt) parser.parseStatement();
            LiteralExpr expr = (LiteralExpr) stmt.expr();
            assertEquals(2147483647L, expr.value());
        }

        @Test
        void parenthesizedExpression() {
            Parser parser = createParser("x = (5)");
            AssignStmt stmt = (AssignStmt) parser.parseStatement();
            LiteralExpr expr = (LiteralExpr) stmt.expr();
            assertEquals(5L, expr.value());
        }

        @Test
        void parenthesizedBinaryExpression() {
            Parser parser = createParser("x = (2 + 3)");
            AssignStmt stmt = (AssignStmt) parser.parseStatement();
            BinaryExpr expr = (BinaryExpr) stmt.expr();
            assertEquals(BinaryOp.ADD, expr.op());
        }

        @Test
        void nestedParentheses() {
            Parser parser = createParser("x = ((2 + 3))");
            AssignStmt stmt = (AssignStmt) parser.parseStatement();
            BinaryExpr expr = (BinaryExpr) stmt.expr();
            assertEquals(BinaryOp.ADD, expr.op());
        }

        @Test
        void parenthesesOverridePrecedence() {
            Parser parser = createParser("x = (2 + 3) * 4");
            AssignStmt stmt = (AssignStmt) parser.parseStatement();
            BinaryExpr expr = (BinaryExpr) stmt.expr();
            assertEquals(BinaryOp.MUL, expr.op());
            assertInstanceOf(BinaryExpr.class, expr.left());
            BinaryExpr inner = (BinaryExpr) expr.left();
            assertEquals(BinaryOp.ADD, inner.op());
        }


        @Test
        void emptyParentheses() {
            Parser parser = createParser("x = ()");
            assertThrows(ParseException.class, parser::parseStatement);
        }
    }

    @Nested
    class PrecedenceAndAssociativityTests {
        @Test
        void multiplicationBeforeAddition() {
            Parser parser = createParser("x = 2 + 3 * 4");
            AssignStmt stmt = (AssignStmt) parser.parseStatement();
            BinaryExpr expr = (BinaryExpr) stmt.expr();
            assertEquals(BinaryOp.ADD, expr.op());
            assertInstanceOf(BinaryExpr.class, expr.right());
        }

        @Test
        void divisionBeforeSubtraction() {
            Parser parser = createParser("x = 10 - 8 / 2");
            AssignStmt stmt = (AssignStmt) parser.parseStatement();
            BinaryExpr expr = (BinaryExpr) stmt.expr();
            assertEquals(BinaryOp.SUB, expr.op());
            assertInstanceOf(BinaryExpr.class, expr.right());
        }

        @Test
        void unaryBeforeMultiplication() {
            Parser parser = createParser("x = -2 * 3");
            AssignStmt stmt = (AssignStmt) parser.parseStatement();
            BinaryExpr expr = (BinaryExpr) stmt.expr();
            assertEquals(BinaryOp.MUL, expr.op());
            assertInstanceOf(UnaryExpr.class, expr.left());
        }

        @Test
        void postfixBeforeAddition() {
            Parser parser = createParser("x = i++ + 5");
            AssignStmt stmt = (AssignStmt) parser.parseStatement();
            BinaryExpr expr = (BinaryExpr) stmt.expr();
            assertEquals(BinaryOp.ADD, expr.op());
            assertInstanceOf(PostfixExpr.class, expr.left());
        }

        @Test
        void leftAssociativeAddition() {
            Parser parser = createParser("x = 1 + 2 + 3");
            AssignStmt stmt = (AssignStmt) parser.parseStatement();
            BinaryExpr expr = (BinaryExpr) stmt.expr();
            assertEquals(BinaryOp.ADD, expr.op());
            assertInstanceOf(BinaryExpr.class, expr.left());
            BinaryExpr left = (BinaryExpr) expr.left();
            assertEquals(BinaryOp.ADD, left.op());
        }

        @Test
        void leftAssociativeMultiplication() {
            Parser parser = createParser("x = 10 / 2 / 5");
            AssignStmt stmt = (AssignStmt) parser.parseStatement();
            BinaryExpr expr = (BinaryExpr) stmt.expr();
            assertEquals(BinaryOp.DIV, expr.op());
            assertInstanceOf(BinaryExpr.class, expr.left());
        }

        @Test
        void complexPrecedenceExpression() {
            Parser parser = createParser("x = 2 * 3 + 4 * 5");
            AssignStmt stmt = (AssignStmt) parser.parseStatement();
            BinaryExpr expr = (BinaryExpr) stmt.expr();
            assertEquals(BinaryOp.ADD, expr.op());
            assertInstanceOf(BinaryExpr.class, expr.left());
            assertInstanceOf(BinaryExpr.class, expr.right());
            assertEquals(BinaryOp.MUL, ((BinaryExpr) expr.left()).op());
            assertEquals(BinaryOp.MUL, ((BinaryExpr) expr.right()).op());
        }

        @Test
        void rightAssociativeUnary() {
            Parser parser = createParser("x = --++i");
            AssignStmt stmt = (AssignStmt) parser.parseStatement();
            UnaryExpr expr = (UnaryExpr) stmt.expr();
            assertEquals(UnaryOp.PRE_DEC, expr.op());
            assertInstanceOf(UnaryExpr.class, expr.expr());
            UnaryExpr inner = (UnaryExpr) expr.expr();
            assertEquals(UnaryOp.PRE_INC, inner.op());
        }
    }

    @Nested
    class ComplexExpressionTests {
        @Test
        void fullCalculatorExpression() {
            Parser parser = createParser("i = ++i + i++ * (5 - 3)");
            AssignStmt stmt = (AssignStmt) parser.parseStatement();
            assertEquals("i", stmt.name());
            assertInstanceOf(BinaryExpr.class, stmt.expr());
        }

        @Test
        void expressionWithAllOperators() {
            Parser parser = createParser("x = ++a + b-- * -c");
            AssignStmt stmt = (AssignStmt) parser.parseStatement();
            assertInstanceOf(BinaryExpr.class, stmt.expr());
        }

        @Test
        void compoundAssignmentWithComplexExpression() {
            Parser parser = createParser("x *= (a + b) / c");
            AssignStmt stmt = (AssignStmt) parser.parseStatement();
            assertEquals(AssignOp.MUL_ASSIGN, stmt.op());
            assertInstanceOf(BinaryExpr.class, stmt.expr());
        }

        @Test
        void deeplyNestedExpression() {
            Parser parser = createParser("x = ((((1 + 2))))");
            AssignStmt stmt = (AssignStmt) parser.parseStatement();
            assertInstanceOf(BinaryExpr.class, stmt.expr());
        }

        @Test
        void allCompoundOperatorsWithExpressions() {
            String[] expressions = {
                    "x = 5",      // ASSIGN
                    "x += 5",     // PLUS_ASSIGN
                    "x -= 5",     // MINUS_ASSIGN
                    "x *= 5",     // MUL_ASSIGN
                    "x /= 5",     // DIV_ASSIGN
                    "x %= 5"      // MOD_ASSIGN
            };
            AssignOp[] expectedOps = {
                    AssignOp.ASSIGN,
                    AssignOp.PLUS_ASSIGN,
                    AssignOp.MINUS_ASSIGN,
                    AssignOp.MUL_ASSIGN,
                    AssignOp.DIV_ASSIGN,
                    AssignOp.MOD_ASSIGN
            };
            for (int i = 0; i < expressions.length; i++) {
                Parser parser = createParser(expressions[i]);
                AssignStmt stmt = (AssignStmt) parser.parseStatement();
                assertEquals(expectedOps[i], stmt.op());
            }
        }
    }

    @Nested
    class ErrorHandlingTests {
        @Test
        void missingIdentifierAtStart() {
            Parser parser = createParser("= 5");
            assertThrows(ParseException.class, parser::parseStatement);
        }

        @Test
        void missingAssignmentOperator() {
            Parser parser = createParser("x 5");
            assertThrows(ParseException.class, parser::parseStatement);
        }

        @Test
        void missingExpression() {
            Parser parser = createParser("x =");
            assertThrows(ParseException.class, parser::parseStatement);
        }

        @Test
        void unexpectedTokenAfterExpression() {
            Parser parser = createParser("x = 5 + 3 extra");
            assertThrows(ParseException.class, parser::parseStatement);
        }

        @Test
        void invalidExpressionStart() {
            Parser parser = createParser("x = * 5");
            assertThrows(ParseException.class, parser::parseStatement);
        }

        @Test
        void unbalancedParentheses() {
            Parser parser = createParser("x = (5 + 3");
            assertThrows(ParseException.class, parser::parseStatement);
        }

        @Test
        void extraClosingParenthesis() {
            Parser parser = createParser("x = (5 + 3))");
            assertThrows(ParseException.class, parser::parseStatement);
        }


        @Test
        void errorMessageContainsPosition() {
            Parser parser = createParser("x =");
            ParseException exception = assertThrows(ParseException.class, parser::parseStatement);
            assertTrue(exception.getMessage().contains("position") ||
                       exception.getMessage().contains("Expected"),
                    "Error message should contain helpful information");
        }
    }

    @Nested
    class BinaryOperatorDirectionTests {
        @Test
        void additionIsLeftAssociative() {
            Parser parser = createParser("x = 10 - 5 - 2");
            AssignStmt stmt = (AssignStmt) parser.parseStatement();
            BinaryExpr expr = (BinaryExpr) stmt.expr();
            assertEquals(BinaryOp.SUB, expr.op());
            assertInstanceOf(BinaryExpr.class, expr.left());
            BinaryExpr left = (BinaryExpr) expr.left();
            assertEquals(BinaryOp.SUB, left.op());
        }

        @Test
        void multiplicationIsLeftAssociative() {
            Parser parser = createParser("x = 20 / 4 / 5");
            AssignStmt stmt = (AssignStmt) parser.parseStatement();
            BinaryExpr expr = (BinaryExpr) stmt.expr();
            assertEquals(BinaryOp.DIV, expr.op());
            assertInstanceOf(BinaryExpr.class, expr.left());
        }

        @Test
        void mixedAddSubIsLeftAssociative() {
            Parser parser = createParser("x = 10 + 5 - 3");
            AssignStmt stmt = (AssignStmt) parser.parseStatement();
            BinaryExpr expr = (BinaryExpr) stmt.expr();
            assertEquals(BinaryOp.SUB, expr.op());
            assertInstanceOf(BinaryExpr.class, expr.left());
            BinaryExpr left = (BinaryExpr) expr.left();
            assertEquals(BinaryOp.ADD, left.op());
        }
    }

    @Nested
    class VariableExpressionTests {
        @Test
        void simpleVariable() {
            Parser parser = createParser("x = y");
            AssignStmt stmt = (AssignStmt) parser.parseStatement();
            VarExpr expr = (VarExpr) stmt.expr();
            assertEquals("y", expr.name());
        }

        @Test
        void variableInBinaryExpression() {
            Parser parser = createParser("x = a + b");
            AssignStmt stmt = (AssignStmt) parser.parseStatement();
            BinaryExpr expr = (BinaryExpr) stmt.expr();
            assertInstanceOf(VarExpr.class, expr.left());
            assertInstanceOf(VarExpr.class, expr.right());
        }

        @Test
        void variableWithPreIncrement() {
            Parser parser = createParser("x = ++count");
            AssignStmt stmt = (AssignStmt) parser.parseStatement();
            UnaryExpr expr = (UnaryExpr) stmt.expr();
            assertInstanceOf(VarExpr.class, expr.expr());
            VarExpr var = (VarExpr) expr.expr();
            assertEquals("count", var.name());
        }

        @Test
        void variableWithPostIncrement() {
            Parser parser = createParser("x = count++");
            AssignStmt stmt = (AssignStmt) parser.parseStatement();
            PostfixExpr expr = (PostfixExpr) stmt.expr();
            assertInstanceOf(VarExpr.class, expr.expr());
            VarExpr var = (VarExpr) expr.expr();
            assertEquals("count", var.name());
        }
    }

    @Nested
    class AllOperatorsTests {
        @Test
        void allBinaryOperators() {
            BinaryOp[] ops = {BinaryOp.ADD, BinaryOp.SUB, BinaryOp.MUL, BinaryOp.DIV, BinaryOp.MOD};
            String[] expressions = {"x = 5 + 3", "x = 5 - 3", "x = 5 * 3", "x = 5 / 3", "x = 5 % 3"};

            for (int i = 0; i < ops.length; i++) {
                Parser parser = createParser(expressions[i]);
                AssignStmt stmt = (AssignStmt) parser.parseStatement();
                BinaryExpr expr = (BinaryExpr) stmt.expr();
                assertEquals(ops[i], expr.op());
            }
        }

        @Test
        void allUnaryOperators() {
            UnaryOp[] ops = {UnaryOp.PLUS, UnaryOp.MINUS, UnaryOp.PRE_INC, UnaryOp.PRE_DEC};
            String[] expressions = {"x = +5", "x = -5", "x = ++i", "x = --i"};

            for (int i = 0; i < ops.length; i++) {
                Parser parser = createParser(expressions[i]);
                AssignStmt stmt = (AssignStmt) parser.parseStatement();
                UnaryExpr expr = (UnaryExpr) stmt.expr();
                assertEquals(ops[i], expr.op());
            }
        }

        @Test
        void allPostfixOperators() {
            PostfixOp[] ops = {PostfixOp.POST_INC, PostfixOp.POST_DEC};
            String[] expressions = {"x = i++", "x = i--"};

            for (int i = 0; i < ops.length; i++) {
                Parser parser = createParser(expressions[i]);
                AssignStmt stmt = (AssignStmt) parser.parseStatement();
                PostfixExpr expr = (PostfixExpr) stmt.expr();
                assertEquals(ops[i], expr.op());
            }
        }
    }
}

