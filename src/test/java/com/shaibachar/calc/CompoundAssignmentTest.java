package com.shaibachar.calc;

import com.shaibachar.calc.exceptions.EvalException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CompoundAssignmentTest {
    @Test
    void plusAssign() {
        String result = CalculatorApp.execute(List.of("i=2", "i+=3"));
        assertEquals("(i=5)", result);
    }

    @Test
    void multiplyAssignWithExpression() {
        String result = CalculatorApp.execute(List.of("i=2", "i*=3+4"));
        assertEquals("(i=14)", result);
    }

    @Test
    void divisionByZeroInRhsThrows() {
        assertThrows(EvalException.class, () -> CalculatorApp.execute(List.of("i=2", "i+=1/0")));
    }
}
