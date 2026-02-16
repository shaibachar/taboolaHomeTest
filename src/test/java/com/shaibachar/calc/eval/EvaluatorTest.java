package com.shaibachar.calc.eval;

import com.shaibachar.calc.exceptions.EvalException;
import com.shaibachar.calc.parser.expr.BinaryExpr;
import com.shaibachar.calc.parser.BinaryOp;
import com.shaibachar.calc.parser.expr.Expr;
import com.shaibachar.calc.parser.expr.LiteralExpr;
import com.shaibachar.calc.parser.expr.PostfixExpr;
import com.shaibachar.calc.parser.PostfixOp;
import com.shaibachar.calc.parser.expr.UnaryExpr;
import com.shaibachar.calc.parser.UnaryOp;
import com.shaibachar.calc.parser.expr.VarExpr;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EvaluatorTest {
    private static Method evalMethod;

    @BeforeAll
    static void lookupEval() throws Exception {
        evalMethod = Evaluator.class.getDeclaredMethod("eval", Expr.class);
        evalMethod.setAccessible(true);
    }

    private static Number eval(Evaluator evaluator, Expr expr) {
        try {
            return (Number) evalMethod.invoke(evaluator, expr);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            throw new RuntimeException(cause);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void evalLiteral() {
        Evaluator evaluator = new Evaluator(new Environment());
        assertEquals(42L, eval(evaluator, new LiteralExpr(42L)));
        assertEquals(3.5, eval(evaluator, new LiteralExpr(3.5)).doubleValue(), 1e-9);
    }

    @Test
    void evalVariable() {
        Environment env = new Environment();
        env.set("x", 7L);
        Evaluator evaluator = new Evaluator(env);
        assertEquals(7L, eval(evaluator, new VarExpr("x")));
    }

    @Test
    void evalUndefinedVariableThrows() {
        Evaluator evaluator = new Evaluator(new Environment());
        assertThrows(EvalException.class, () -> eval(evaluator, new VarExpr("x")));
    }

    @Test
    void evalUnaryPlusAndMinus() {
        Evaluator evaluator = new Evaluator(new Environment());
        assertEquals(5L, eval(evaluator, new UnaryExpr(UnaryOp.PLUS, new LiteralExpr(5L))));
        assertEquals(-5L, eval(evaluator, new UnaryExpr(UnaryOp.MINUS, new LiteralExpr(5L))));
        assertEquals(-2.5, eval(evaluator, new UnaryExpr(UnaryOp.MINUS, new LiteralExpr(2.5))).doubleValue(), 1e-9);
    }

    @Test
    void evalPreIncrementAndDecrement() {
        Environment env = new Environment();
        env.set("i", 1L);
        Evaluator evaluator = new Evaluator(env);
        assertEquals(2L, eval(evaluator, new UnaryExpr(UnaryOp.PRE_INC, new VarExpr("i"))));
        assertEquals(2L, env.get("i"));
        assertEquals(1L, eval(evaluator, new UnaryExpr(UnaryOp.PRE_DEC, new VarExpr("i"))));
        assertEquals(1L, env.get("i"));
    }

    @Test
    void evalPostIncrementAndDecrement() {
        Environment env = new Environment();
        env.set("i", 1L);
        Evaluator evaluator = new Evaluator(env);
        assertEquals(1L, eval(evaluator, new PostfixExpr(new VarExpr("i"), PostfixOp.POST_INC)));
        assertEquals(2L, env.get("i"));
        assertEquals(2L, eval(evaluator, new PostfixExpr(new VarExpr("i"), PostfixOp.POST_DEC)));
        assertEquals(1L, env.get("i"));
    }

    @Test
    void evalBinaryOperators() {
        Evaluator evaluator = new Evaluator(new Environment());
        assertEquals(11L, eval(evaluator, new BinaryExpr(new LiteralExpr(8L), BinaryOp.ADD, new LiteralExpr(3L))));
        assertEquals(5L, eval(evaluator, new BinaryExpr(new LiteralExpr(8L), BinaryOp.SUB, new LiteralExpr(3L))));
        assertEquals(24L, eval(evaluator, new BinaryExpr(new LiteralExpr(8L), BinaryOp.MUL, new LiteralExpr(3L))));
        assertEquals(2L, eval(evaluator, new BinaryExpr(new LiteralExpr(8L), BinaryOp.DIV, new LiteralExpr(3L))));
        assertEquals(2L, eval(evaluator, new BinaryExpr(new LiteralExpr(8L), BinaryOp.MOD, new LiteralExpr(3L))));
        assertEquals(2L, eval(evaluator, new BinaryExpr(new LiteralExpr(5L), BinaryOp.DIV, new LiteralExpr(2L))));
        assertEquals(2.0, eval(evaluator, new BinaryExpr(new LiteralExpr(5.0), BinaryOp.MOD, new LiteralExpr(3L))).doubleValue(), 1e-9);
    }

    @Test
    void evalBinaryDivisionByZeroThrows() {
        Evaluator evaluator = new Evaluator(new Environment());
        assertThrows(EvalException.class, () -> eval(evaluator,
                new BinaryExpr(new LiteralExpr(1L), BinaryOp.DIV, new LiteralExpr(0L))));
        assertThrows(EvalException.class, () -> eval(evaluator,
                new BinaryExpr(new LiteralExpr(1L), BinaryOp.MOD, new LiteralExpr(0L))));
        assertThrows(EvalException.class, () -> eval(evaluator,
                new BinaryExpr(new LiteralExpr(1.0), BinaryOp.DIV, new LiteralExpr(0.0))));
    }

    @Test
    void evalLeftToRightSideEffects() {
        Environment env = new Environment();
        env.set("i", 0L);
        Evaluator evaluator = new Evaluator(env);
        Expr expr = new BinaryExpr(
                new PostfixExpr(new VarExpr("i"), PostfixOp.POST_INC),
                BinaryOp.ADD,
                new UnaryExpr(UnaryOp.PRE_INC, new VarExpr("i"))
        );
        assertEquals(2L, eval(evaluator, expr));
        assertEquals(2L, env.get("i"));
    }

    @Test
    void evalFloatingPointIncDec() {
        Environment env = new Environment();
        env.set("x", 1.5);
        Evaluator evaluator = new Evaluator(env);
        assertEquals(2.5, eval(evaluator, new UnaryExpr(UnaryOp.PRE_INC, new VarExpr("x"))).doubleValue(), 1e-9);
        assertEquals(2.5, env.get("x").doubleValue(), 1e-9);
        assertEquals(2.5, eval(evaluator, new PostfixExpr(new VarExpr("x"), PostfixOp.POST_DEC)).doubleValue(), 1e-9);
        assertEquals(1.5, env.get("x").doubleValue(), 1e-9);
    }
}