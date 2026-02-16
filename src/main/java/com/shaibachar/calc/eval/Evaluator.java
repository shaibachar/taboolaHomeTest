package com.shaibachar.calc.eval;

import com.shaibachar.calc.exceptions.ErrorMessages;
import com.shaibachar.calc.exceptions.EvalException;
import com.shaibachar.calc.parser.AssignOp;
import com.shaibachar.calc.parser.stmt.AssignStmt;
import com.shaibachar.calc.parser.expr.BinaryExpr;
import com.shaibachar.calc.parser.expr.Expr;
import com.shaibachar.calc.parser.expr.LiteralExpr;
import com.shaibachar.calc.parser.expr.PostfixExpr;
import com.shaibachar.calc.parser.PostfixOp;
import com.shaibachar.calc.parser.stmt.Stmt;
import com.shaibachar.calc.parser.expr.UnaryExpr;
import com.shaibachar.calc.parser.UnaryOp;
import com.shaibachar.calc.parser.expr.VarExpr;

import java.util.logging.Logger;

/**
 * Evaluator class that evaluates statements and expressions based on the provided environment.
 * It supports variable assignments, arithmetic operations, and unary/postfix operators.
 * The evaluator processes statements and expressions recursively, updating the environment as needed.
 * It throws an EvalException for unsupported statement types, expression types, or invalid operations (e.g., division by zero).
 * The main method is `execute`, which takes a statement and evaluates it, while the `eval` method evaluates expressions.
 * The evaluator ensures that variables are defined before use and that only assignable variables are modified by unary operators.
 * The class is designed to be extensible, allowing for additional statement and expression types to be added in the future.
 * Overall, the Evaluator class serves as the core component for executing the logic of the calculator application,
 * interpreting the parsed abstract syntax tree (AST) and managing variable state through the environment.
 *
 */
public class Evaluator {
    private static final Logger LOGGER = Logger.getLogger(Evaluator.class.getName());
    private final Environment env;

    public Evaluator(Environment env) {
        this.env = env;
    }

    /**
     * Executes a statement by evaluating it and updating the environment accordingly.
     * Currently, it only supports assignment statements.
     * It checks the type of the statement and processes it based on the assignment operator.
     * For simple assignment, it evaluates the right-hand side expression and sets the variable in the environment.
     * For compound assignments (e.g., +=, -=), it retrieves the current value of the variable,
     * evaluates the right-hand side expression, performs the specified operation, and updates the variable
     * in the environment with the result.
     * If the statement type is unsupported, it throws an EvalException.
     *
     * @param stmt the statement to execute, which should be an instance of AssignStmt
     */
    public void execute(Stmt stmt) {
        LOGGER.fine("Executing statement");
        long startNs = System.nanoTime();
        // pre-condition: stmt is an instance of AssignStmt
        if (!(stmt instanceof AssignStmt assignStmt)) {
            throw new EvalException(ErrorMessages.EVAL_UNSUPPORTED_STATEMENT);
        }

        String name = assignStmt.name();
        AssignOp op = assignStmt.op();
        // validates that the variable is defined before use for compound assignments
        if (op == AssignOp.ASSIGN) {
            Number rightHandSide = eval(assignStmt.expr());
            env.set(name, rightHandSide);
            LOGGER.fine("Assigned " + name + " = " + rightHandSide);
            long elapsedMs = (System.nanoTime() - startNs) / 1_000_000;
            LOGGER.fine("perf.component=evaluator_execute elapsed_ms=" + elapsedMs);
            return;
        }

        Number leftHandSideSnapshot = env.get(name);
        Number rightHandSide = eval(assignStmt.expr());
        Number result = switch (op) {
            case PLUS_ASSIGN -> add(leftHandSideSnapshot, rightHandSide);
            case MINUS_ASSIGN -> subtract(leftHandSideSnapshot, rightHandSide);
            case MUL_ASSIGN -> multiply(leftHandSideSnapshot, rightHandSide);
            case DIV_ASSIGN -> divide(leftHandSideSnapshot, rightHandSide);
            case MOD_ASSIGN -> modulo(leftHandSideSnapshot, rightHandSide);
            default -> throw new IllegalStateException(ErrorMessages.evalUnexpectedAssignOp(op));
        };
        env.set(name, result);
        LOGGER.fine("Updated " + name + " = " + result);
        long elapsedMs = (System.nanoTime() - startNs) / 1_000_000;
        LOGGER.fine("perf.component=evaluator_execute elapsed_ms=" + elapsedMs);
    }

    /**
     * Evaluates an expression recursively. It checks the type of the expression and processes it accordingly:
     * - For literal expressions, it returns the literal value.
     * - For variable expressions, it retrieves the variable's value from the environment.
     * - For unary expressions, it evaluates the operand and applies the unary operator
     * (e.g., +, -, pre-increment, pre-decrement).
     * - For postfix expressions, it evaluates the operand and applies the postfix operator
     * (e.g., post-increment, post-decrement).
     * - For binary expressions, it evaluates the left and right operands and applies the binary operator
     * (e.g., +, -, *, /, %).
     * If the expression type is unsupported, it throws an EvalException.
     *
     * @param expr the expression to evaluate, which can be an instance of LiteralExpr, VarExpr, UnaryExpr, PostfixExpr,
     *            or BinaryExpr
     * @return the evaluated integer result of the expression
     */
    private Number eval(Expr expr) {
        LOGGER.fine("Evaluating expression: " + expr.getClass().getSimpleName());
        if (expr instanceof LiteralExpr literal) {
            return literal.value();
        }
        if (expr instanceof VarExpr varExpr) {
            return env.get(varExpr.name());
        }
        if (expr instanceof UnaryExpr unary) {
            return evalUnary(unary);
        }
        if (expr instanceof PostfixExpr postfix) {
            return evalPostfix(postfix);
        }
        if (expr instanceof BinaryExpr binary) {
            Number left = eval(binary.left());
            Number right = eval(binary.right());
            return switch (binary.op()) {
                case ADD -> add(left, right);
                case SUB -> subtract(left, right);
                case MUL -> multiply(left, right);
                case DIV -> divide(left, right);
                case MOD -> modulo(left, right);
            };
        }
        throw new EvalException(ErrorMessages.EVAL_UNSUPPORTED_EXPRESSION);
    }

    private Number evalUnary(UnaryExpr unary) {
        LOGGER.fine("Evaluating unary expression: " + unary.op());
        UnaryOp op = unary.op();
        Expr operand = unary.expr();
        return switch (op) {
            case PLUS -> eval(operand);
            case MINUS -> negate(eval(operand));
            case PRE_INC -> {
                String name = requireAssignableVariable(operand);
                Number updated = add(env.get(name), 1L);
                env.set(name, updated);
                yield updated;
            }
            case PRE_DEC -> {
                String name = requireAssignableVariable(operand);
                Number updated = subtract(env.get(name), 1L);
                env.set(name, updated);
                yield updated;
            }
        };
    }

    private Number evalPostfix(PostfixExpr postfix) {
        LOGGER.fine("Evaluating postfix expression: " + postfix.op());
        String name = requireAssignableVariable(postfix.expr());
        Number old = env.get(name);
        if (postfix.op() == PostfixOp.POST_INC) {
            env.set(name, add(old, 1L));
        } else {
            env.set(name, subtract(old, 1L));
        }
        return old;
    }

    private String requireAssignableVariable(Expr expr) {
        LOGGER.fine("Validating assignable operand");
        if (expr instanceof VarExpr varExpr) {
            return varExpr.name();
        }
        throw new EvalException(ErrorMessages.EVAL_OPERAND_NOT_ASSIGNABLE);
    }

    private Number divide(Number left, Number right) {
        LOGGER.fine("Dividing " + left + " by " + right);
        if (isZero(right)) {
            throw new EvalException(ErrorMessages.EVAL_DIVISION_BY_ZERO);
        }
        if (isFloating(left) || isFloating(right)) {
            return left.doubleValue() / right.doubleValue();
        }
        return left.longValue() / right.longValue();
    }

    private Number modulo(Number left, Number right) {
        LOGGER.fine("Modulo " + left + " by " + right);
        if (isZero(right)) {
            throw new EvalException(ErrorMessages.EVAL_DIVISION_BY_ZERO);
        }
        if (isFloating(left) || isFloating(right)) {
            return left.doubleValue() % right.doubleValue();
        }
        return left.longValue() % right.longValue();
    }

    private Number add(Number left, Number right) {
        LOGGER.fine("Adding " + left + " and " + right);
        if (isFloating(left) || isFloating(right)) {
            return left.doubleValue() + right.doubleValue();
        }
        return left.longValue() + right.longValue();
    }

    private Number subtract(Number left, Number right) {
        LOGGER.fine("Subtracting " + right + " from " + left);
        if (isFloating(left) || isFloating(right)) {
            return left.doubleValue() - right.doubleValue();
        }
        return left.longValue() - right.longValue();
    }

    private Number multiply(Number left, Number right) {
        LOGGER.fine("Multiplying " + left + " and " + right);
        if (isFloating(left) || isFloating(right)) {
            return left.doubleValue() * right.doubleValue();
        }
        return left.longValue() * right.longValue();
    }

    private Number negate(Number value) {
        LOGGER.fine("Negating " + value);
        if (isFloating(value)) {
            return -value.doubleValue();
        }
        return -value.longValue();
    }

    private boolean isFloating(Number value) {
        LOGGER.fine("Checking floating type for " + value);
        return value instanceof Float || value instanceof Double;
    }

    private boolean isZero(Number value) {
        LOGGER.fine("Checking zero for " + value);
        if (isFloating(value)) {
            return value.doubleValue() == 0.0;
        }
        return value.longValue() == 0L;
    }
}
