package com.shaibachar.calc.parser.stmt;

import com.shaibachar.calc.parser.AssignOp;
import com.shaibachar.calc.parser.expr.Expr;

import java.util.Objects;

public final class AssignStmt implements Stmt {
    private final String name;
    private final AssignOp op;
    private final Expr expr;

    public AssignStmt(String name, AssignOp op, Expr expr) {
        this.name = name;
        this.op = op;
        this.expr = expr;
    }

    public String name() {
        return name;
    }

    public AssignOp op() {
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
        if (!(o instanceof AssignStmt other)) {
            return false;
        }
        return Objects.equals(name, other.name)
                && Objects.equals(op, other.op)
                && Objects.equals(expr, other.expr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, op, expr);
    }

    @Override
    public String toString() {
        return "AssignStmt[name=" + name + ", op=" + op + ", expr=" + expr + "]";
    }
}
