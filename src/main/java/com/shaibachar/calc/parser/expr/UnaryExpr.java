package com.shaibachar.calc.parser.expr;

import com.shaibachar.calc.parser.UnaryOp;

import java.util.Objects;

/**
 * Represents a unary expression, such as -x or +y.
 */
public final class UnaryExpr implements Expr {
    private final UnaryOp op;
    private final Expr expr;

    public UnaryExpr(UnaryOp op, Expr expr) {
        this.op = op;
        this.expr = expr;
    }

    public UnaryOp op() {
        return op;
    }

    public Expr expr() {
        return expr;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UnaryExpr other)) {
            return false;
        }
        return Objects.equals(op, other.op)
                && Objects.equals(expr, other.expr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(op, expr);
    }

    @Override
    public String toString() {
        return "UnaryExpr[op=" + op + ", expr=" + expr + "]";
    }
}
