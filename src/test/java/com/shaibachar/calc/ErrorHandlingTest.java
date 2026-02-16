package com.shaibachar.calc;

import com.shaibachar.calc.exceptions.EvalException;
import com.shaibachar.calc.exceptions.ParseException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ErrorHandlingTest {
    @Test
    void undefinedVariableThrows() {
        EvalException exception = assertThrows(EvalException.class,
                () -> CalculatorApp.execute(List.of("x = y + 1")));
        assertTrue(exception.getMessage().contains("line 1"));
        assertTrue(exception.getMessage().contains("x = y + 1"));
    }

    @Test
    void invalidPrefixIncrementOperandThrows() {
        EvalException exception = assertThrows(EvalException.class,
                () -> CalculatorApp.execute(List.of("i=1", "x = ++(1)")));
        assertTrue(exception.getMessage().contains("line 2"));
        assertTrue(exception.getMessage().contains("x = ++(1)"));
    }

    @Test
    void invalidPostfixIncrementOperandThrows() {
        EvalException exception = assertThrows(EvalException.class,
                () -> CalculatorApp.execute(List.of("i=1", "x = (i+1)++")));
        assertTrue(exception.getMessage().contains("line 2"));
        assertTrue(exception.getMessage().contains("x = (i+1)++"));
    }

    @Test
    void invalidSyntaxIncludesLineAndSourceContext() {
        ParseException exception = assertThrows(ParseException.class,
                () -> CalculatorApp.execute(List.of("x = (1 + 2")));
        assertTrue(exception.getMessage().contains("line 1"));
        assertTrue(exception.getMessage().contains("x = (1 + 2"));
    }
}
