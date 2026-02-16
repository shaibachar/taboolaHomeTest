package com.shaibachar.calc;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IncDecSemanticsTest {
    @Test
    void preIncrement() {
        String result = CalculatorApp.execute(List.of("i=0", "x=++i"));
        assertEquals("(i=1,x=1)", result);
    }

    @Test
    void postIncrement() {
        String result = CalculatorApp.execute(List.of("i=0", "x=i++"));
        assertEquals("(i=1,x=0)", result);
    }

    @Test
    void preDecrement() {
        String result = CalculatorApp.execute(List.of("i=2", "x=--i"));
        assertEquals("(i=1,x=1)", result);
    }

    @Test
    void postDecrement() {
        String result = CalculatorApp.execute(List.of("i=2", "x=i--"));
        assertEquals("(i=1,x=2)", result);
    }

    @Test
    void leftToRightSideEffects() {
        String result = CalculatorApp.execute(List.of("i=0", "x=i++ + ++i"));
        assertEquals("(i=2,x=2)", result);
    }
}
