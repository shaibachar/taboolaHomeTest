package com.shaibachar.calc;

import com.shaibachar.calc.eval.Environment;
import com.shaibachar.calc.eval.Evaluator;
import com.shaibachar.calc.exceptions.EvalException;
import com.shaibachar.calc.exceptions.ParseException;
import com.shaibachar.calc.lexer.Lexer;
import com.shaibachar.calc.parser.Parser;
import com.shaibachar.calc.parser.stmt.AssignStmt;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RobustnessTest {
    private static final String[] OPS = {"+", "-", "*", "/", "%"};

    @Test
    void generatedValidAssignmentsDoNotCrashParser() {
        Random random = new Random(42);
        for (int i = 0; i < 200; i++) {
            String input = "x = " + generateExpr(random, 0);
            assertDoesNotThrow(() -> new Parser(new Lexer(input).tokenize()).parseStatement(), input);
        }
    }

    @Test
    void generatedMalformedInputsFailGracefully() {
        String[] malformed = {
                "x = (1 + 2",
                "x = ++(1)",
                "x = 1 +",
                "x = * 7",
                "x = 7 / )",
                "= 5",
                "x ?= 1",
                "x = 1..2"
        };

        for (String input : malformed) {
            assertThrows(RuntimeException.class,
                    () -> CalculatorApp.execute(java.util.List.of(input)),
                    input);
        }
    }

    @Test
    void randomProgramExecutionProducesRuntimeOutcomeOnly() {
        Random random = new Random(7);
        for (int i = 0; i < 100; i++) {
            String line = "x = " + generateExpr(random, 0);
            Parser parser = new Parser(new Lexer(line).tokenize());
            AssignStmt stmt = (AssignStmt) parser.parseStatement();
            Evaluator evaluator = new Evaluator(new Environment());
            try {
                evaluator.execute(stmt);
            } catch (EvalException | ParseException ignored) {
                // acceptable outcome for generated expressions (e.g. division by zero)
            }
        }
    }

    private String generateExpr(Random random, int depth) {
        if (depth >= 3 || random.nextBoolean()) {
            return String.valueOf(random.nextInt(9) + 1);
        }
        String left = generateExpr(random, depth + 1);
        String right = generateExpr(random, depth + 1);
        String op = OPS[random.nextInt(OPS.length)];
        if (random.nextBoolean()) {
            return "(" + left + " " + op + " " + right + ")";
        }
        return left + " " + op + " " + right;
    }
}
