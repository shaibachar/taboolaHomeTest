package com.shaibachar.calc.parser.expr;

import com.shaibachar.calc.parser.PostfixOp;

import java.util.Objects;

/**
 * Represents a postfix expression, such as "x++" or "y--".
 */
public final class PostfixExpr implements Expr {
    private final Expr expr;
    private final PostfixOp op;

    public PostfixExpr(Expr expr, PostfixOp op) {
        this.expr = expr;
        this.op = op;
    }

    public Expr expr() {
        return expr;
    }

    public PostfixOp op() {
        return op;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PostfixExpr other)) {
            return false;
        }
        return Objects.equals(expr, other.expr)
                && Objects.equals(op, other.op);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expr, op);
    }

    @Override
    public String toString() {
        return "PostfixExpr[expr=" + expr + ", op=" + op + "]";
    }
}
