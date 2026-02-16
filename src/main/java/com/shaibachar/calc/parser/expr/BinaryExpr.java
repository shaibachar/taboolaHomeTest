package com.shaibachar.calc.parser.expr;

import com.shaibachar.calc.parser.BinaryOp;

import java.util.Objects;

/**
 * Represents a binary expression, such as addition, subtraction, multiplication, or division.
 * The left and right operands are also expressions, allowing for nested expressions.
 * The operator is represented by the BinaryOp enum, which defines the supported binary operations.
 * This class is immutable, meaning that once an instance is created, its state cannot be changed.
 * The equals and hashCode methods are overridden to allow for proper comparison and usage in collections.
 * The toString method provides a string representation of the binary expression for debugging purposes.
 * Example usage:
 * BinaryExpr expr = new BinaryExpr(new VarExpr("x"), BinaryOp.ADD, new VarExpr("y"));
 * This represents the expression "x + y".
 */
public final class BinaryExpr implements Expr {
    private final Expr left;
    private final BinaryOp op;
    private final Expr right;

    public BinaryExpr(Expr left, BinaryOp op, Expr right) {
        this.left = left;
        this.op = op;
        this.right = right;
    }

    public Expr left() {
        return left;
    }

    public BinaryOp op() {
        return op;
    }

    public Expr right() {
        return right;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BinaryExpr other)) {
            return false;
        }
        return Objects.equals(left, other.left)
                && Objects.equals(op, other.op)
                && Objects.equals(right, other.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, op, right);
    }

    @Override
    public String toString() {
        return "BinaryExpr[left=" + left + ", op=" + op + ", right=" + right + "]";
    }
}
