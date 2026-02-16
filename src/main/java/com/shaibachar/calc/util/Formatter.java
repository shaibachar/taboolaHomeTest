package com.shaibachar.calc.util;

import java.util.Map;
import java.util.StringJoiner;
import java.util.logging.Logger;

/**
 * Utility class for formatting a map of variable names and their corresponding values
 * into a string representation. The output format is (key1=value1,key2=value2,...).
 */
public final class Formatter {
    private static final Logger LOGGER = Logger.getLogger(Formatter.class.getName());

    private Formatter() {
    }

    public static String format(Map<String, Number> values) {
        LOGGER.info("Formatting output for " + values.size() + " variables");
        long startNs = System.nanoTime();
        StringJoiner joiner = new StringJoiner(",", "(", ")");
        for (Map.Entry<String, Number> entry : values.entrySet()) {
            joiner.add(entry.getKey() + "=" + entry.getValue());
        }
        String output = joiner.toString();
        long elapsedMs = (System.nanoTime() - startNs) / 1_000_000;
        LOGGER.info("perf.component=formatter_output elapsed_ms=" + elapsedMs + " vars=" + values.size());
        return output;
    }
}
