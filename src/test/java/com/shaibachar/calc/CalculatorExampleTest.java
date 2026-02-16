package com.shaibachar.calc;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculatorExampleTest {
    @Test
    void goldenExample() {
        String result = CalculatorApp.execute(List.of(
                "i = 0",
                "j = ++i",
                "x = i++ + 5",
                "y = (5 + 3) * 10",
                "i += y"
        ));
        assertEquals("(i=82,j=1,x=6,y=80)", result);
    }
}
