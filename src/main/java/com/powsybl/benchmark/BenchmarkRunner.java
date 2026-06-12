/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.benchmark;

import com.powsybl.benchmark.commons.ReleaseBenchmark;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public final class BenchmarkRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(BenchmarkRunner.class);

    private BenchmarkRunner() {
    }

    public static void main(String[] args) throws Exception {
        // If "--release" is the first argument, discover all @ReleaseBenchmark classes
        // and replace args with the generated regex
        if (args.length > 0 && "--release".equals(args[0])) {
            String regex = buildReleaseBenchmarkRegex();
            LOGGER.info("Running release benchmarks matching: {}", regex);
            // Pass remaining args after "--release", prepending the regex
            String[] remainingArgs = new String[args.length]; // same length
            remainingArgs[0] = regex;
            System.arraycopy(args, 1, remainingArgs, 1, args.length - 1);
            org.openjdk.jmh.Main.main(remainingArgs);
        } else {
            org.openjdk.jmh.Main.main(args);
        }
    }

    /**
     * Scans all classes in the JAR for those annotated with @ReleaseBenchmark
     * (at class or method level) and builds a JMH-compatible regex.
     * - Class-level annotation: matches all benchmark methods in that class.
     * - Method-level annotation: matches only that specific benchmark method.
     */
    private static String buildReleaseBenchmarkRegex() throws IOException {
        List<String> matchingPatterns = new ArrayList<>();

        try (InputStream is = Objects.requireNonNull(BenchmarkRunner.class.getResourceAsStream("/META-INF/BenchmarkList"));
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = reader.readLine()) != null) {
                processLine(line, matchingPatterns);
            }
        }

        if (matchingPatterns.isEmpty()) {
            throw new IllegalStateException("No benchmarks found with @ReleaseBenchmark annotation");
        }

        return String.join("|", matchingPatterns);
    }

    /**
     * Processes a single line from the JMH BenchmarkList file and adds a matching
     * pattern to the list if the corresponding class or method is annotated with @ReleaseBenchmark.
     * The JMH BenchmarkList format is: "fully.qualified.ClassName.methodName ..."
     */
    private static void processLine(String line, List<String> matchingPatterns) {
        String[] parts = line.trim().split("\\s+");
        if (parts.length == 0) {
            return;
        }
        // parts[0] is "fully.qualified.ClassName.methodName"
        int lastDot = parts[0].lastIndexOf('.');
        if (lastDot < 0) {
            return;
        }
        String className = parts[0].substring(0, lastDot);
        String methodName = parts[0].substring(lastDot + 1);
        try {
            Class<?> clazz = Class.forName(className);
            if (isReleaseBenchmark(clazz, methodName)) {
                matchingPatterns.add(clazz.getSimpleName() + "\\." + methodName);
            }
        } catch (ClassNotFoundException ignored) {
            // skip entries whose class cannot be loaded
        }
    }

    /**
     * Returns true if the given method should be included in the release benchmark suite,
     * either because its class is annotated with @ReleaseBenchmark, or because the specific
     * method is.
     */
    private static boolean isReleaseBenchmark(Class<?> clazz, String methodName) {
        if (clazz.isAnnotationPresent(ReleaseBenchmark.class)) {
            return true;
        }
        return Arrays.stream(clazz.getMethods())
            .anyMatch(method -> method.getName().equals(methodName)
                && method.isAnnotationPresent(ReleaseBenchmark.class));
    }
}
