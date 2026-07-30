/**
 * Copyright (c) 2022-2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark;

import com.powsybl.benchmark.commons.FullBenchmark;
import com.powsybl.benchmark.commons.ReleaseBenchmark;
import com.powsybl.benchmark.commons.runcomparison.BenchmarkResultSerDe;
import com.powsybl.commons.PowsyblException;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.CommandLineOptionException;
import org.openjdk.jmh.runner.options.CommandLineOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.Supplier;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public final class BenchmarkRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(BenchmarkRunner.class);

    private BenchmarkRunner() {
    }

    public static void main(String[] args) throws CommandLineOptionException, RunnerException {
        String[] benchmarkArgs = buildBenchmarkArgs(args);
        if (List.of(benchmarkArgs).contains("--list")) {
            LOGGER.info("Selected benchmarks:");
            for (String arg : benchmarkArgs) {
                if ("--list".equals(arg)) {
                    continue;
                }
                for (String s : arg.split("\\|")) {
                    String value = s.replace("\\.", ".");
                    LOGGER.info(value);
                }
            }
        } else {
            CommandLineOptions opts = new CommandLineOptions(benchmarkArgs);
            try {
                Collection<RunResult> results = new Runner(opts).run();
                //TODO add option to not always write results
                BenchmarkResultSerDe.writeAll(results);
            } catch (RunnerException | IOException e) {
                LOGGER.error("Error writing benchmark results", e);
                System.exit(1);
            }
        }
    }

    private static String[] buildBenchmarkArgs(String[] args) {
        if (args.length < 1) {
            return args;
        }

        return switch (args[0]) {
            // If "--release" is the first argument, discover all @ReleaseBenchmark classes
            // and replace args with the generated regex
            case "--release" -> buildBenchmarkSuiteRegex(args, "release",
                () -> buildBenchmarkSuiteRegexFromAnnotation(ReleaseBenchmark.class));
            // If "--full" is the first argument, discover all @FullBenchmark classes
            // and replace args with the generated regex
            case "--full" -> buildBenchmarkSuiteRegex(args, "full",
                () -> buildBenchmarkSuiteRegexFromAnnotation(ReleaseBenchmark.class, FullBenchmark.class));
            default -> args;
        };
    }

    private static String[] buildBenchmarkSuiteRegex(String[] args, String benchmarkSuite, Supplier<String> regexSupplier) {
        String regex = regexSupplier.get();
        LOGGER.info("Running {} benchmarks matching: {}", benchmarkSuite, regex);
        // Pass remaining args after "--release", prepending the regex
        String[] remainingArgs = new String[args.length]; // same length
        remainingArgs[0] = regex;
        System.arraycopy(args, 1, remainingArgs, 1, args.length - 1);
        return remainingArgs;
    }

    /**
     * Scans all classes in the JAR for those annotated with @ReleaseBenchmark
     * (at class or method level) and builds a JMH-compatible regex.
     * - Class-level annotation: matches all benchmark methods in that class.
     * - Method-level annotation: matches only that specific benchmark method.
     */
    @SafeVarargs
    private static String buildBenchmarkSuiteRegexFromAnnotation(Class<? extends Annotation>... annotationClasses) {
        List<String> matchingPatterns = new ArrayList<>();

        try (InputStream is = Objects.requireNonNull(BenchmarkRunner.class.getResourceAsStream("/META-INF/BenchmarkList"));
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = reader.readLine()) != null) {
                processLine(line, matchingPatterns, annotationClasses);
            }
        } catch (IOException e) {
            throw new PowsyblException("Failed to read BenchmarkList", e);
        }

        if (matchingPatterns.isEmpty()) {
            String classes = String.join(", ", Arrays.stream(annotationClasses).map(Class::getSimpleName).toArray(String[]::new));
            throw new IllegalStateException(String.format("No benchmarks found with @%s annotations", classes));
        }

        return String.join("|", matchingPatterns);
    }

    /**
     * Processes a single line from the JMH BenchmarkList file and adds a matching
     * pattern to the list if the corresponding class or method is annotated with {@code annotationClass}.
     * <p>
     * The JMH BenchmarkList binary format starts with:
     * {@code JMH S <len> <userClass> S <len> <generatedClass> S <len> <methodName> ...}
     */
    @SafeVarargs
    private static void processLine(String line, List<String> matchingPatterns, Class<? extends Annotation>... annotationClasses) {
        // Tokens are space-separated; strings are preceded by "S <length>"
        String[] tokens = line.trim().split("\\s+");
        // Minimum: "JMH", "S", <userClass>, "S", <generatedClass>, "S", <methodName>
        if (tokens.length < 7 || !"JMH".equals(tokens[0])) {
            return;
        }
        // token[1]="S", token[2]=<len>, token[3]=<userClass>
        // token[4]="S", token[5]=<len>, token[6]=<generatedClass>
        // token[7]="S", token[8]=<len>, token[9]=<methodName>
        String className = tokens[3];
        String methodName = tokens[9];
        try {
            Class<?> clazz = Class.forName(className);
            if (isReleaseBenchmark(clazz, methodName, annotationClasses)) {
                matchingPatterns.add(clazz.getSimpleName() + "\\." + methodName);
            }
        } catch (ClassNotFoundException ignored) {
            // skip entries whose class cannot be loaded
        }
    }

    /**
     * Returns true if the given method should be included in the benchmark suite,
     * either because its class is annotated with {@code annotationClass}, or because the specific
     * method is.
     */
    @SafeVarargs
    private static boolean isReleaseBenchmark(Class<?> clazz, String methodName, Class<? extends Annotation>... annotationClasses) {
        for (Class<? extends Annotation> annotationClass : annotationClasses) {
            if (clazz.isAnnotationPresent(annotationClass) || Arrays.stream(clazz.getMethods())
                .anyMatch(method -> method.getName().equals(methodName)
                    && method.isAnnotationPresent(annotationClass))) {
                return true;
            }
        }
        return false;
    }
}
