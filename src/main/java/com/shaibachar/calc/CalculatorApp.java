package com.shaibachar.calc;

import com.shaibachar.calc.eval.Environment;
import com.shaibachar.calc.eval.Evaluator;
import com.shaibachar.calc.exceptions.ErrorMessages;
import com.shaibachar.calc.exceptions.EvalException;
import com.shaibachar.calc.exceptions.ParseException;
import com.shaibachar.calc.lexer.Lexer;
import com.shaibachar.calc.lexer.Token;
import com.shaibachar.calc.parser.Parser;
import com.shaibachar.calc.parser.stmt.Stmt;
import com.shaibachar.calc.util.Formatter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class CalculatorApp {
    static {
        try (InputStream input = CalculatorApp.class.getClassLoader().getResourceAsStream("logging.properties")) {
            if (input != null) {
                LogManager.getLogManager().readConfiguration(input);
            }
        } catch (IOException e) {
            System.out.println("Could not load logging configuration: " + e.getMessage());
        }
    }

    private static final Logger LOGGER = Logger.getLogger(CalculatorApp.class.getName());

    public static void main(String[] args) throws IOException {
        configureLogging(args);

        LOGGER.info("Starting calculator app");
        System.out.println("Text Calculator");
        System.out.println("Enter one assignment per line (e.g., x = 5, y += 2, z = (x + y) * 3).");
        System.out.println("When finished, end input:");
        System.out.println("  Windows (PowerShell): Ctrl+Z then Enter");
        System.out.println("  macOS/Linux: Ctrl+D");
        System.out.println("Run command: mvn exec:java -Dexec.mainClass=\"com.shaibachar.calc.CalculatorApp\"");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        List<String> lines = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }
        System.out.println(execute(lines));
    }

    public static String execute(List<String> lines) {
        LOGGER.info("Executing calculator with " + lines.size() + " lines");
        long startNs = System.nanoTime();
        Environment env = new Environment();
        Evaluator evaluator = new Evaluator(env);

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line == null || line.trim().isEmpty()) {
                continue;
            }

            int lineNumber = i + 1;
            try {
                List<Token> tokens = new Lexer(line).tokenize();
                Stmt stmt = new Parser(tokens).parseStatement();
                evaluator.execute(stmt);
            } catch (ParseException e) {
                throw new ParseException(ErrorMessages.withLineContext(e.getMessage(), lineNumber, line));
            } catch (EvalException e) {
                throw new EvalException(ErrorMessages.withLineContext(e.getMessage(), lineNumber, line));
            }
        }

        String output = Formatter.format(env.values());
        long elapsedMs = (System.nanoTime() - startNs) / 1_000_000;
        LOGGER.info("perf.component=calculator_execute elapsed_ms=" + elapsedMs + " lines=" + lines.size());
        return output;
    }

    private static void configureLogging(String[] args) {
        boolean verbose = false;
        for (String arg : args) {
            if ("--verbose".equals(arg) || "-v".equals(arg)) {
                verbose = true;
                break;
            }
        }
        if (verbose) {
            Logger root = Logger.getLogger("");
            root.setLevel(Level.FINE);
            LOGGER.info("Verbose logging enabled");
        }
    }
}
