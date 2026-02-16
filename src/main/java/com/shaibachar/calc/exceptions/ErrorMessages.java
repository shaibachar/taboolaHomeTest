package com.shaibachar.calc.exceptions;

public final class ErrorMessages {
    public static final String PARSE_EXPECTED_IDENTIFIER = "PARSE_001: Expected identifier at statement start";
    public static final String PARSE_EXPECTED_ASSIGN_OP = "PARSE_002: Expected assignment operator";
    public static final String PARSE_UNEXPECTED_TOKEN_AFTER_EXPRESSION = "PARSE_003: Unexpected token after expression";
    public static final String PARSE_INVALID_MULTIPLICATIVE_OPERATOR = "PARSE_004: Invalid multiplicative operator";
    public static final String PARSE_INVALID_NUMBER_LITERAL = "PARSE_005: Invalid number literal";
    public static final String PARSE_EXPECTED_RPAREN = "PARSE_006: Expected ')' after expression";
    public static final String PARSE_EXPECTED_EXPRESSION = "PARSE_007: Expected expression";

    public static final String EVAL_UNSUPPORTED_STATEMENT = "EVAL_001: Unsupported statement type";
    public static final String EVAL_UNSUPPORTED_EXPRESSION = "EVAL_002: Unsupported expression type";
    public static final String EVAL_OPERAND_NOT_ASSIGNABLE = "EVAL_003: Operand is not assignable for ++/--";
    public static final String EVAL_DIVISION_BY_ZERO = "EVAL_004: Division by zero";
    public static final String EVAL_UNEXPECTED_ASSIGN_OP = "EVAL_005: Unexpected assignment operator: %s";

    public static final String ENV_UNDEFINED_VARIABLE = "ENV_001: Undefined variable: %s. Assign it before use.";

    public static final String LEXER_UNEXPECTED_CHARACTER = "LEXER_001: Unexpected character: %s at position %d";
    public static final String LEXER_INVALID_NUMBER_LITERAL = "LEXER_002: Invalid number literal at position %d";
    public static final String LEXER_NUMBER_OVERFLOW = "LEXER_003: %s overflow literal: %s";

    private ErrorMessages() {
    }

    public static String lexerUnexpectedCharacter(char c, int position) {
        return String.format(LEXER_UNEXPECTED_CHARACTER, c, position);
    }

    public static String lexerInvalidNumberLiteral(int position) {
        return String.format(LEXER_INVALID_NUMBER_LITERAL, position);
    }

    public static String lexerNumberOverflow(String kind, String literal) {
        return String.format(LEXER_NUMBER_OVERFLOW, kind, literal);
    }

    public static String evalUnexpectedAssignOp(Object op) {
        return String.format(EVAL_UNEXPECTED_ASSIGN_OP, op);
    }

    public static String envUndefinedVariable(String name) {
        return String.format(ENV_UNDEFINED_VARIABLE, name);
    }

    public static String withLineContext(String message, int lineNumber, String line) {
        return String.format("%s (line %d: %s)", message, lineNumber, line);
    }
}

