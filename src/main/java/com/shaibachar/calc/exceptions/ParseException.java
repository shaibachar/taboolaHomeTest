package com.shaibachar.calc.exceptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Enumeration of error severity levels.
 */
enum ErrorSeverity {
    ERROR("Error", "??"),      // Critical error that prevents compilation
    WARNING("Warning", "??");  // Non-critical issue that should be addressed

    private final String name;
    private final String icon;

    ErrorSeverity(String name, String icon) {
        this.name = name;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }
}

/**
 * Represents a single parse error with severity level, hint, and position.
 */
class ParseError {
    private final String code;
    private final String message;
    private final int position;
    private final ErrorSeverity severity;
    private final String hint;

    public ParseError(String code, String message, int position, ErrorSeverity severity, String hint) {
        this.code = code;
        this.message = message;
        this.position = position;
        this.severity = severity;
        this.hint = hint;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public int getPosition() {
        return position;
    }

    public ErrorSeverity getSeverity() {
        return severity;
    }

    public String getHint() {
        return hint;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(severity.getIcon()).append(" ");
        sb.append(code).append(": ").append(message);
        sb.append(" at position ").append(position);
        if (hint != null && !hint.isEmpty()) {
            sb.append("\n  Hint: ").append(hint);
        }
        return sb.toString();
    }
}

/**
 * Exception thrown during parsing. Supports collecting multiple errors
 * to provide comprehensive error reporting to the user.
 */
public class ParseException extends RuntimeException {
    private final List<ParseError> errors;

    /**
     * Creates a ParseException with a single error message.
     * @param message the error message
     */
    public ParseException(String message) {
        super(message);
        this.errors = new ArrayList<>();
        // Parse the error code and message to create a ParseError
        ParseError error = createErrorFromMessage(message);
        this.errors.add(error);
    }

    /**
     * Creates a ParseException with multiple error messages.
     * @param errors the list of error messages
     */
    public ParseException(List<String> errors) {
        super(formatErrors(errors));
        this.errors = new ArrayList<>();
        for (String error : errors) {
            this.errors.add(createErrorFromMessage(error));
        }
    }

    /**
     * Creates a ParseException with a list of ParseError objects.
     * @param parseErrors the list of ParseError objects
     */
    public ParseException(List<ParseError> parseErrors, boolean isParseErrors) {
        super(formatParseErrors(parseErrors));
        this.errors = new ArrayList<>(parseErrors);
    }

    /**
     * Adds an error to this exception.
     * @param error the ParseError to add
     */
    public void addParseError(ParseError error) {
        this.errors.add(error);
    }

    /**
     * Returns all collected errors as ParseError objects.
     * @return the list of ParseError objects
     */
    public List<ParseError> getParseErrors() {
        return new ArrayList<>(this.errors);
    }

    /**
     * Returns all collected error messages as strings.
     * @return the list of error messages
     */
    public List<String> getErrors() {
        List<String> messages = new ArrayList<>();
        for (ParseError error : this.errors) {
            messages.add(error.toString());
        }
        return messages;
    }

    /**
     * Returns the number of errors collected.
     * @return the error count
     */
    public int getErrorCount() {
        return this.errors.size();
    }

    /**
     * Returns the number of errors with a specific severity.
     * @param severity the severity level to count
     * @return the count of errors with that severity
     */
    public int getErrorCountBySeverity(ErrorSeverity severity) {
        return (int) this.errors.stream()
                .filter(e -> e.getSeverity() == severity)
                .count();
    }

    /**
     * Parses an error message string to extract code and message.
     * @param message the error message string
     * @return a ParseError object
     */
    private static ParseError createErrorFromMessage(String message) {
        // Extract error code from message (format: CODE_XXX: message at position Y)
        String code = "UNKNOWN";
        String msg = message;
        int position = -1;
        String hint = getHintForError(message);

        if (message.contains(": ")) {
            code = message.substring(0, message.indexOf(": "));
            msg = message.substring(message.indexOf(": ") + 2);
        }

        if (message.contains("at position ")) {
            String posStr = message.substring(message.lastIndexOf("at position ") + 12);
            try {
                position = Integer.parseInt(posStr);
            } catch (NumberFormatException e) {
                position = -1;
            }
        }

        // Determine severity based on code
        ErrorSeverity severity = ErrorSeverity.ERROR;
        if (code.startsWith("PARSE_")) {
            severity = ErrorSeverity.ERROR;
        } else if (code.startsWith("EVAL_")) {
            severity = ErrorSeverity.ERROR;
        }

        return new ParseError(code, msg, position, severity, hint);
    }

    /**
     * Provides helpful hints for common errors.
     * @param message the error message
     * @return a helpful hint, or empty string if no hint is available
     */
    private static String getHintForError(String message) {
        if (message.contains("PARSE_001")) {
            return "Start with a variable name (e.g., 'x', 'result')";
        } else if (message.contains("PARSE_002")) {
            return "Use an assignment operator: =, +=, -=, *=, /=, %=, or ^=";
        } else if (message.contains("PARSE_003")) {
            return "Check for extra tokens after your expression";
        } else if (message.contains("PARSE_004")) {
            return "Use valid operators: +, -, *, /, %, or ^";
        } else if (message.contains("PARSE_005")) {
            return "Check your number format - ensure it's a valid integer or decimal";
        } else if (message.contains("PARSE_006")) {
            return "Make sure every opening '(' has a matching closing ')'";
        } else if (message.contains("PARSE_007")) {
            return "Expected a value, variable, or expression (like '5', 'x', or '(2+3)')";
        } else if (message.contains("EVAL_001")) {
            return "Check your statement structure - expected an assignment statement";
        } else if (message.contains("EVAL_002")) {
            return "Ensure the expression type is supported by the evaluator";
        } else if (message.contains("division by zero")) {
            return "Cannot divide by zero - use a non-zero divisor";
        } else if (message.contains("undefined variable") || message.contains("EVAL_003")) {
            return "Make sure the variable is defined before using it";
        }
        return "";
    }

    /**
     * Formats error messages for display.
     * @param errors the list of error messages
     * @return formatted error message
     */
    private static String formatErrors(List<String> errors) {
        if (errors.isEmpty()) {
            return "Unknown error";
        }
        if (errors.size() == 1) {
            return errors.get(0);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Found ").append(errors.size()).append(" errors:\n");
        for (int i = 0; i < errors.size(); i++) {
            sb.append("  ").append(i + 1).append(". ").append(errors.get(i)).append("\n");
        }
        return sb.toString();
    }

    /**
     * Formats ParseError objects for display.
     * @param parseErrors the list of ParseError objects
     * @return formatted error message
     */
    private static String formatParseErrors(List<ParseError> parseErrors) {
        if (parseErrors.isEmpty()) {
            return "Unknown error";
        }
        if (parseErrors.size() == 1) {
            return parseErrors.get(0).toString();
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Found ").append(parseErrors.size()).append(" errors:\n");
        for (int i = 0; i < parseErrors.size(); i++) {
            sb.append("  ").append(i + 1).append(". ").append(parseErrors.get(i).toString()).append("\n");
        }
        return sb.toString();
    }
}
