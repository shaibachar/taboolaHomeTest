package com.shaibachar.calc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Performance tests for the calculator's most common flows.
 * Measures execution time for typical operations to detect performance regressions.
 */
@DisplayName("Performance Tests")
class PerformanceTest {

    private static final int ITERATIONS = 1000;
    private static final int COMPLEX_ITERATIONS = 100;
    private static final long PERFORMANCE_THRESHOLD_MS = 5000; // 5 seconds for 1000 operations

    /**
     * Tests performance of simple arithmetic operations (most common flow).
     * Common: x = 5 + 3
     */
    @Test
    @DisplayName("Simple arithmetic operations performance (1000 iterations)")
    void testSimpleArithmeticPerformance() {
        long startTime = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            CalculatorApp.execute(List.of("x = 5 + 3"));
        }
        long endTime = System.nanoTime();

        long elapsedMs = (endTime - startTime) / 1_000_000;
        System.out.println("Simple arithmetic (1000x): " + elapsedMs + "ms");
        assertTrue(elapsedMs < PERFORMANCE_THRESHOLD_MS,
                   "Simple arithmetic took too long: " + elapsedMs + "ms > " + PERFORMANCE_THRESHOLD_MS + "ms");
    }

    /**
     * Tests performance of variable assignments.
     * Common: x = 42
     */
    @Test
    @DisplayName("Variable assignment performance (1000 iterations)")
    void testVariableAssignmentPerformance() {
        long startTime = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            CalculatorApp.execute(List.of("result = 42"));
        }
        long endTime = System.nanoTime();

        long elapsedMs = (endTime - startTime) / 1_000_000;
        System.out.println("Variable assignment (1000x): " + elapsedMs + "ms");
        assertTrue(elapsedMs < PERFORMANCE_THRESHOLD_MS,
                   "Variable assignment took too long: " + elapsedMs + "ms > " + PERFORMANCE_THRESHOLD_MS + "ms");
    }

    /**
     * Tests performance of compound assignments.
     * Common: x += 5
     */
    @Test
    @DisplayName("Compound assignment performance (500 iterations)")
    void testCompoundAssignmentPerformance() {
        long startTime = System.nanoTime();
        for (int i = 0; i < 500; i++) {
            CalculatorApp.execute(List.of("x = 10", "x += 5"));
        }
        long endTime = System.nanoTime();

        long elapsedMs = (endTime - startTime) / 1_000_000;
        System.out.println("Compound assignment (500x): " + elapsedMs + "ms");
        assertTrue(elapsedMs < PERFORMANCE_THRESHOLD_MS,
                   "Compound assignment took too long: " + elapsedMs + "ms > " + PERFORMANCE_THRESHOLD_MS + "ms");
    }

    /**
     * Tests performance of increment/decrement operations.
     * Common: i = i++
     */
    @Test
    @DisplayName("Increment/decrement operations performance (500 iterations)")
    void testIncrementDecrementPerformance() {
        long startTime = System.nanoTime();
        for (int i = 0; i < 500; i++) {
            CalculatorApp.execute(List.of("i = 0", "i = i++", "i = ++i"));
        }
        long endTime = System.nanoTime();

        long elapsedMs = (endTime - startTime) / 1_000_000;
        System.out.println("Increment/decrement (500x): " + elapsedMs + "ms");
        assertTrue(elapsedMs < PERFORMANCE_THRESHOLD_MS,
                   "Increment/decrement took too long: " + elapsedMs + "ms > " + PERFORMANCE_THRESHOLD_MS + "ms");
    }

    /**
     * Tests performance of complex expressions with multiple operators.
     * Common: x = (a + b) * c - d / e
     */
    @Test
    @DisplayName("Complex expression performance (100 iterations)")
    void testComplexExpressionPerformance() {
        long startTime = System.nanoTime();
        for (int i = 0; i < COMPLEX_ITERATIONS; i++) {
            CalculatorApp.execute(List.of(
                "a = 10", "b = 20", "c = 5", "d = 100", "e = 2",
                "result = (a + b) * c - d / e"
            ));
        }
        long endTime = System.nanoTime();

        long elapsedMs = (endTime - startTime) / 1_000_000;
        System.out.println("Complex expression (100x): " + elapsedMs + "ms");
        // Allow more time for complex expressions
        long complexThreshold = 1000; // 1 second for 100 iterations
        assertTrue(elapsedMs < complexThreshold,
                   "Complex expression took too long: " + elapsedMs + "ms > " + complexThreshold + "ms");
    }

    /**
     * Tests performance of power operations.
     * Common: x = 2 ^ 10
     */
    @Test
    @DisplayName("Power operator performance (500 iterations)")
    void testPowerOperatorPerformance() {
        long startTime = System.nanoTime();
        for (int i = 0; i < 500; i++) {
            CalculatorApp.execute(List.of("result = 2 ^ 10"));
        }
        long endTime = System.nanoTime();

        long elapsedMs = (endTime - startTime) / 1_000_000;
        System.out.println("Power operator (500x): " + elapsedMs + "ms");
        assertTrue(elapsedMs < 2500, // 2.5 seconds for 500 iterations
                   "Power operator took too long: " + elapsedMs + "ms");
    }

    /**
     * Tests performance of nested parentheses.
     * Common: x = ((a + b) * (c - d)) / e
     */
    @Test
    @DisplayName("Nested parentheses performance (100 iterations)")
    void testNestedParenthesesPerformance() {
        long startTime = System.nanoTime();
        for (int i = 0; i < COMPLEX_ITERATIONS; i++) {
            CalculatorApp.execute(List.of(
                "a = 5", "b = 3", "c = 10", "d = 2", "e = 2",
                "result = ((a + b) * (c - d)) / e"
            ));
        }
        long endTime = System.nanoTime();

        long elapsedMs = (endTime - startTime) / 1_000_000;
        System.out.println("Nested parentheses (100x): " + elapsedMs + "ms");
        assertTrue(elapsedMs < 1000, // 1 second for 100 iterations
                   "Nested parentheses took too long: " + elapsedMs + "ms");
    }

    /**
     * Tests performance of end-to-end calculator flow.
     * Simulates typical user session with multiple operations.
     */
    @Test
    @DisplayName("End-to-end calculator flow performance (50 iterations)")
    void testEndToEndPerformance() {
        long startTime = System.nanoTime();
        for (int iter = 0; iter < 50; iter++) {
            // Simulate typical user session
            CalculatorApp.execute(List.of(
                "x = 10",
                "y = 20",
                "z = x + y",
                "result = z * 2",
                "counter = 0",
                "counter = counter + 1",
                "counter = counter + 1",
                "sum = x + y + z + result",
                "average = sum / 4",
                "power_result = 2 ^ 3"
            ));
        }
        long endTime = System.nanoTime();

        long elapsedMs = (endTime - startTime) / 1_000_000;
        System.out.println("End-to-end flow (50x): " + elapsedMs + "ms");
        assertTrue(elapsedMs < 2000, // 2 seconds for 50 full sessions
                   "End-to-end flow took too long: " + elapsedMs + "ms");
    }

    /**
     * Tests performance of mixed operations with all operator types.
     */
    @Test
    @DisplayName("Mixed operators performance (100 iterations)")
    void testMixedOperatorsPerformance() {
        long startTime = System.nanoTime();
        for (int i = 0; i < COMPLEX_ITERATIONS; i++) {
            CalculatorApp.execute(List.of(
                "a = 5",
                "b = 3",
                "add_result = a + b",
                "sub_result = a - b",
                "mul_result = a * b",
                "div_result = a / b",
                "mod_result = a % b",
                "pow_result = a ^ b",
                "inc_a = ++a",
                "dec_a = --a"
            ));
        }
        long endTime = System.nanoTime();

        long elapsedMs = (endTime - startTime) / 1_000_000;
        System.out.println("Mixed operators (100x): " + elapsedMs + "ms");
        assertTrue(elapsedMs < 1500, // 1.5 seconds for 100 iterations
                   "Mixed operators took too long: " + elapsedMs + "ms");
    }

    /**
     * Tests performance with deeply nested expressions.
     */
    @Test
    @DisplayName("Deeply nested expressions performance (100 iterations)")
    void testDeeplyNestedPerformance() {
        long startTime = System.nanoTime();
        for (int i = 0; i < COMPLEX_ITERATIONS; i++) {
            CalculatorApp.execute(List.of(
                "x = 2",
                "result = (((((x + 1) * 2) + 3) * 4) + 5)"
            ));
        }
        long endTime = System.nanoTime();

        long elapsedMs = (endTime - startTime) / 1_000_000;
        System.out.println("Deeply nested (100x): " + elapsedMs + "ms");
        assertTrue(elapsedMs < 1000, // 1 second for 100 iterations
                   "Deeply nested took too long: " + elapsedMs + "ms");
    }

    /**
     * Tests performance with large numbers.
     */
    @Test
    @DisplayName("Large number operations performance (100 iterations)")
    void testLargeNumberPerformance() {
        long startTime = System.nanoTime();
        for (int i = 0; i < COMPLEX_ITERATIONS; i++) {
            CalculatorApp.execute(List.of(
                "big1 = 999999999",
                "big2 = 888888888",
                "sum = big1 + big2",
                "product = big1 * big2"
            ));
        }
        long endTime = System.nanoTime();

        long elapsedMs = (endTime - startTime) / 1_000_000;
        System.out.println("Large numbers (100x): " + elapsedMs + "ms");
        assertTrue(elapsedMs < 1000, // 1 second for 100 iterations
                   "Large numbers took too long: " + elapsedMs + "ms");
    }

    /**
     * Tests performance of decimal number operations.
     */
    @Test
    @DisplayName("Decimal number operations performance (100 iterations)")
    void testDecimalPerformance() {
        long startTime = System.nanoTime();
        for (int i = 0; i < COMPLEX_ITERATIONS; i++) {
            CalculatorApp.execute(List.of(
                "f1 = 3.14159",
                "f2 = 2.71828",
                "sum = f1 + f2",
                "product = f1 * f2",
                "division = f1 / f2"
            ));
        }
        long endTime = System.nanoTime();

        long elapsedMs = (endTime - startTime) / 1_000_000;
        System.out.println("Decimal numbers (100x): " + elapsedMs + "ms");
        assertTrue(elapsedMs < 1000, // 1 second for 100 iterations
                   "Decimal numbers took too long: " + elapsedMs + "ms");
    }
}



