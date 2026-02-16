package com.shaibachar.calc.parser.expr;

/**
 * Represents a literal integer expression in the abstract syntax tree (AST).
 * This class is immutable and provides methods for equality, hashing, and string representation.
 * Example usage:
 * <pre>
 *     LiteralExpr literal = new LiteralExpr(42);
 *     System.out.println(literal.value()); // Output: 42
 *     System.out.println(literal); // Output: LiteralExpr[value=42]
 * </pre>
 */
public final class LiteralExpr implements Expr {
    private final Number value;

    public LiteralExpr(Number value) {
        this.value = value;
    }

    public Number value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LiteralExpr other)) {
            return false;
        }
        return value != null && value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return value == null ? 0 : value.hashCode();
    }

    @Override
    public String toString() {
        return "LiteralExpr[value=" + value + "]";
    }
}
