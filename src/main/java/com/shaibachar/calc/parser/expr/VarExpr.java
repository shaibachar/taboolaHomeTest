package com.shaibachar.calc.parser.expr;

import java.util.Objects;

/**
 * VarExpr represents a variable reference inside the calculator parser’s AST (e.g., x in x + 2).
 * It is an immutable, thread-safe data class that stores only the variable name and implements Expr.
 * The class mainly provides identity behavior — equality, hashing, and debug string — based on the variable name
 * , and serves as a basic building block for variable usage and assignments in expressions.
 */
public final class VarExpr implements Expr {
    private final String name;

    public VarExpr(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VarExpr other)) {
            return false;
        }
        return Objects.equals(name, other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "VarExpr[name=" + name + "]";
    }
}
