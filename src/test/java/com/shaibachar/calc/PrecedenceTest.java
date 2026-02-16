package com.shaibachar.calc;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PrecedenceTest {
    @Test
    void multiplicationBeforeAddition() {
        String result = CalculatorApp.execute(List.of("x = 2 + 3 * 4"));
        assertEquals("(x=14)", result);
    }

    @Test
    void parenthesesOverridePrecedence() {
        String result = CalculatorApp.execute(List.of("x = (2 + 3) * 4"));
        assertEquals("(x=20)", result);
    }

    @Test
    void unaryMinusPrecedence() {
        String result = CalculatorApp.execute(List.of("x = -2 * 3"));
        assertEquals("(x=-6)", result);
    }

    @Test
    void unaryMinusWithParentheses() {
        String result = CalculatorApp.execute(List.of("x = -(2 * 3)"));
        assertEquals("(x=-6)", result);
    }
}
