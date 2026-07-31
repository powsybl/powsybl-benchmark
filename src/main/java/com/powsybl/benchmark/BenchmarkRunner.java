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
import com.powsybl.commons.PowsyblException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.io.*;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
@Command(name = "benchmark", version = "2026.1.0-SNAPSHOT", mixinStandardHelpOptions = true)
public final class BenchmarkRunner implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(BenchmarkRunner.class);

    @CommandLine.Option(names = {"--list", "-l"}, description = "List benchmarks that would be run by the command, but do not run them", defaultValue = "false")
    private boolean listBenchmarks = false;

    @CommandLine.ArgGroup(exclusive = true, multiplicity = "0..1", heading = "Benchmark suite selection\n")
    private BenchmarkSuite benchmarkSuite;

    private static final class BenchmarkSuite {

        @CommandLine.Option(names = {"--release"}, description = "Run benchmarks tagged as release")
        private boolean release;

        @CommandLine.Option(names = {"--full"}, description = "Run all benchmarks tagged as full (that includes release benchmarks)")
        private boolean full;
    }

    @CommandLine.Parameters(paramLabel = "<benchmarks>", description = "List of benchmarks to run, separated by spaces")
    private String[] benchmarks;

    private BenchmarkRunner() {
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new BenchmarkRunner()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        String[] benchmarkArgs = buildBenchmarkArgs();
        if (listBenchmarks) {
            LOGGER.info("Selected benchmarks:");
            for (String bench : benchmarkArgs) {
                for (String s : bench.split("\\|")) {
                    String value = s.replace("\\.", ".");
                    LOGGER.info(value);
                }
            }
        } else {
            try {
                org.openjdk.jmh.Main.main(benchmarkArgs);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    /**
     * Build benchmarks to be passed to JMH. This is a mix of regex and class names.
     * The returned regex depends on the value of the {@link BenchmarkSuite}.
     * @return an array, where the first element is a potential JMH regex, and the remaining elements are the names in {@link #benchmarks} (if any).
     */
    private String[] buildBenchmarkArgs() {
        if (benchmarkSuite != null) {
            if (benchmarkSuite.release) {
                //discover all @ReleaseBenchmark classes and prepend the regex to the list of benchmarks
                return buildBenchmarkSuiteRegex(benchmarks, "release",
                    () -> buildBenchmarkSuiteRegexFromAnnotation(ReleaseBenchmark.class));
            } else if (benchmarkSuite.full) {
                //discover all @FullBenchmark and @ReleaseBenchmark classes and prepend the regex to the list of benchmarks
                return buildBenchmarkSuiteRegex(benchmarks, "full",
                    () -> buildBenchmarkSuiteRegexFromAnnotation(ReleaseBenchmark.class, FullBenchmark.class));
            }
        }
        return benchmarks;
    }

    /**
     * Build the list of benchmarks to be run by JMH using a regex and additionally provided benchmark names.
     * @param benchmarks the named benchmarks to run (might be empty or null)
     * @param benchmarkSuite the name of the benchmark suite
     * @param regexSupplier a supplier of the regex corresponding to the benchmark suite (related to annotation classes)
     * @return an array starting with the string corresponding to the JMH regex (classes separated by <code>|</code>), and
     * the remaining elements are the names of the benchmarks to run named directly inside <code>benchmarks</code> (if any).
     */
    private static String[] buildBenchmarkSuiteRegex(String[] benchmarks, String benchmarkSuite, Supplier<String> regexSupplier) {
        String regex = regexSupplier.get();
        LOGGER.info("Running {} benchmarks matching: {}", benchmarkSuite, regex);
        // The complete list of benchmarks to run is the benchmarks from the regex + benchmarks that are named individually
        int benchmarksArgNumber = benchmarks != null ? benchmarks.length + 1 : 1;
        String[] allBenchmarks = new String[benchmarksArgNumber]; // same length
        allBenchmarks[0] = regex;
        if (benchmarks != null) {
            System.arraycopy(benchmarks, 0, allBenchmarks, 1, benchmarks.length);
        }
        return allBenchmarks;
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
