package com.shaibachar.calc.eval;

import com.shaibachar.calc.exceptions.ErrorMessages;
import com.shaibachar.calc.exceptions.EvalException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Environment class that holds variable bindings for the expression evaluation.
 * It provides methods to get and set variable values, and to retrieve all variable bindings.
 * This class is used during the evaluation of expressions to keep track of variable values.
 * The environment is implemented as a LinkedHashMap to maintain the order of variable definitions.
 * It throws an EvalException if an attempt is made to access an undefined variable.
 *
 */
public class Environment {
    private static final Logger LOGGER = Logger.getLogger(Environment.class.getName());
    // LinkedHashMap to store variable names and their corresponding numeric values
    private final LinkedHashMap<String, Number> values = new LinkedHashMap<>();

    /**
     * Retrieves the value of a variable from the environment.
     * @param name the name of the variable to retrieve
     * @return the numeric value associated with the variable name
     * @throws EvalException if the variable is not defined in the environment
     */
    public Number get(String name) {
        LOGGER.fine("Reading variable: " + name);
        Number value = values.get(name);
        if (value == null) {
            throw new EvalException(ErrorMessages.envUndefinedVariable(name));
        }
        return value;
    }

    /**
     * Sets the value of a variable in the environment. If the variable already exists, its value is updated.
     * If the variable does not exist, it is added to the environment with the specified value.
     * This method allows for both defining new variables and updating existing ones in the environment.
     *
     * @param name the name of the variable to set
     * @param value the numeric value to associate with the variable name
     */
    public void set(String name, Number value) {
        LOGGER.fine("Setting variable: " + name + " = " + value);
        values.put(name, value);
    }

    public Map<String, Number> values() {
        LOGGER.fine("Reading environment values");
        return values;
    }
}
