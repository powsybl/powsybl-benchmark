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
import com.powsybl.benchmark.commons.serde.*;
import com.powsybl.commons.PowsyblException;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.CommandLineOptionException;
import org.openjdk.jmh.runner.options.CommandLineOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.io.*;
import java.lang.annotation.Annotation;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.function.Supplier;

import static picocli.CommandLine.Option.NULL_VALUE;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
@Command(name = "benchmark", mixinStandardHelpOptions = true, versionProvider = BenchmarkVersionProvider.class)
public final class BenchmarkRunner implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(BenchmarkRunner.class);

    private static final Map<String, String> BENCHMARK_METHOD_TO_CLASS = loadBenchmarkMethodToClassCache();

    @CommandLine.Parameters(paramLabel = "<benchmarks>", description = "List of benchmarks to run, separated by spaces")
    private String[] benchmarks;

    @CommandLine.Option(names = {"--compare", "-C"}, arity = "0..1", description = "Compare with a baselineDirectory containing baseline benchmark results to compare with." +
        "Must contain only one of each serialized benchmark result", defaultValue = NULL_VALUE, fallbackValue = BenchmarkReportJsonSerDe.BENCHMARK_PATH_STRING)
    private Path baselineDirectory;

    @CommandLine.Option(names = "--baseline-timestamp", description = "Only use serialized benchmark results from the baseline directory that match exactly the provided timestamp." +
        "Format is YYYYMMDD_hhmmss. If not provided, do not consider timestamp to match filenames", defaultValue = "")
    private String baselineTimestamp = "";

    @CommandLine.Option(names = {"--debug", "-D"}, description = "Disable forks for debug purposes", defaultValue = "false")
    private boolean debug = false;

    @CommandLine.Option(names = {"--export-markdown-only", "-E"}, description = "Only export existing JSON results to Markdown without running benchmarks", defaultValue = "false")
    private boolean exportMarkdownOnly = false;

    @CommandLine.Option(names = {"--list", "-l"}, description = "List benchmarks that would be run by the command, but do not run them", defaultValue = "false")
    private boolean listBenchmarks = false;

    @CommandLine.Option(names = "--no-serde", description = "Do not exportAll benchmark results", defaultValue = "false")
    private boolean noSerde = false;

    @CommandLine.Option(names = "--serde-path", description = "Where to exportAll benchmark results", defaultValue = BenchmarkReportJsonSerDe.BENCHMARK_PATH_STRING)
    private String serdePath = BenchmarkReportJsonSerDe.BENCHMARK_PATH_STRING;

    @CommandLine.ArgGroup(exclusive = true, multiplicity = "0..1", heading = "Benchmark suite selection\n")
    private BenchmarkSuite benchmarkSuite;

    private static final class BenchmarkSuite {

        @CommandLine.Option(names = {"--release"}, description = "Run benchmarks tagged as release")
        private boolean release;

        @CommandLine.Option(names = {"--full"}, description = "Run all benchmarks tagged as full (that includes release benchmarks)")
        private boolean full;
    }

    BenchmarkRunner() {
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new BenchmarkRunner()).execute(args);
        System.exit(exitCode);
    }

    private static Map<String, String> loadBenchmarkMethodToClassCache() {
        Map<String, String> benchmarkMethodToClass = new HashMap<>();
        // Initialize the cache from BenchmarkList
        try (InputStream is = Objects.requireNonNull(BenchmarkRunner.class.getResourceAsStream("/META-INF/BenchmarkList"));
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = reader.readLine()) != null) {
                cacheBenchmarkMethodToClass(benchmarkMethodToClass, line);
            }
        } catch (IOException e) {
            LOGGER.warn("Failed to populate benchmark method-to-class cache", e);
        }
        return benchmarkMethodToClass;
    }

    @Override
    public void run() {
        String[] benchmarkArgs = buildBenchmarkArgs();
        if (benchmarkArgs == null) {
            LOGGER.warn("No benchmark provided");
            return;
        }
        if (listBenchmarks) {
            // Only list selected benchmarks, do not run them
            LOGGER.info("Selected benchmarks:");
            for (String bench : benchmarkArgs) {
                for (String s : bench.split("\\|")) {
                    String value = s.replace("\\.", ".");
                    LOGGER.info(value);
                }
            }
        } else {

            // Path to the directory where benchmark results are and/or will be exported
            Path benchmarkOutputPath = Path.of(serdePath);

            // Check/create directory and parents, does not throw if directory already exists
            if (exportMarkdownOnly || !noSerde) {
                checkOrCreateDirectory(benchmarkOutputPath);
            }

            if (exportMarkdownOnly) {
                exportExistingJsonReportsToMarkdown(benchmarkOutputPath, benchmarkArgs);
            } else {
                runAndExportBenchmarks(benchmarkOutputPath, benchmarkArgs);
            }
        }
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
     * Build JMH arguments, adding the "-f 0" option when debug mode is enabled.
     * @param debug whether debug mode is enabled
     * @param benchmarkArgs the benchmark arguments to run
     * @return an array with benchmark arguments and debug options if needed
     */
    private static String[] buildJmhArgs(boolean debug, String[] benchmarkArgs) {
        if (debug) {
            LOGGER.info("Debug mode activated: forks are disabled");
            String[] jmhArgs = new String[benchmarkArgs.length + 2];
            System.arraycopy(benchmarkArgs, 0, jmhArgs, 0, benchmarkArgs.length);
            jmhArgs[benchmarkArgs.length] = "-f";
            jmhArgs[benchmarkArgs.length + 1] = "0";
            return jmhArgs;
        }
        return benchmarkArgs;
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

    /**
     * Build benchmarks to be passed to JMH. This is a mix of regex and class names.
     * The returned regex depends on the value of the {@link BenchmarkSuite}.
     * @return an array, where the first element is a potential JMH regex, and the remaining elements are the names in {@link #benchmarks} (if any).
     */
    String[] buildBenchmarkArgs() {
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

    private void checkOrCreateDirectory(Path directory) {
        try {
            Files.createDirectories(directory);
        } catch (IOException e) {
            throw new PowsyblException("Failed to create directory: " + directory, e);
        }
    }

    private String[] getBenchmarkClassesFromBenchmarkArgs(String[] benchmarkArgs) {
        Set<String> cleanedBenchmarkArgs = new HashSet<>();
        for (String bench : benchmarkArgs) {
            for (String s : bench.split("\\|")) {
                // Split at the first backslash to get the class or method name
                String classOrMethod = s.split("\\\\")[0]; // Remove escaped characters

                // If it's a method name, look it up in the cache
                // Otherwise, treat it as a class name or full qualified name
                cleanedBenchmarkArgs.add(BENCHMARK_METHOD_TO_CLASS.getOrDefault(classOrMethod, classOrMethod));
            }
        }
        return cleanedBenchmarkArgs.toArray(new String[0]);
    }

    private void exportExistingJsonReportsToMarkdown(Path benchmarkOutputPath, String[] benchmarkArgs) {
        // Clean benchmark classes from regex characters to match the JSON files
        String[] cleanedBenchmarkArgs = getBenchmarkClassesFromBenchmarkArgs(benchmarkArgs);

        List<BenchmarkReport> reports;
        try {
            reports = BenchmarkReportJsonSerDe.readReports(
                SerDeUtil.getMatchingFilesFromPath(benchmarkOutputPath,
                    new SerDeFilePredicate(cleanedBenchmarkArgs, ".json", "")).toArray(new Path[0]));

        } catch (IOException e) {
            throw new PowsyblException("Error reading existing JSON reports", e);
        }

        // Markdown export
        ResultsExporter.exportAll(reports, benchmarkOutputPath, baselineDirectory, baselineTimestamp);
    }

    private void runAndExportBenchmarks(Path benchmarkOutputPath, String[] benchmarkArgs) {
        List<BenchmarkReport> reports;
        try {
            String[] jmhArgs = buildJmhArgs(debug, benchmarkArgs);
            CommandLineOptions opts = new CommandLineOptions(jmhArgs);
            // Run the benchmarks and collect the results
            Collection<RunResult> results = new Runner(opts).run();

            String datetimeFormatted = SerDeUtil.formatISODatetimeToSimplified(LocalDateTime.now(ZoneOffset.UTC).toString());
            reports = BenchmarkReport.buildAllReports(results, datetimeFormatted);

        } catch (RunnerException | CommandLineOptionException e) {
            throw new PowsyblException("Error running benchmarks", e);
        }

        // Export the results if serde is enabled
        if (!noSerde) {
            // JSON export
            BenchmarkReportJsonSerDe.writeReports(reports, benchmarkOutputPath);

            // Markdown export
            ResultsExporter.exportAll(reports, benchmarkOutputPath, baselineDirectory, baselineTimestamp);
        }
    }

    /**
     * Caches the method-to-class mapping from a BenchmarkList line.
     */
    private static void cacheBenchmarkMethodToClass(Map<String, String> benchmarkMethodToClass, String line) {
        String[] tokens = line.trim().split("\\s+");
        if (tokens.length < 10 || !"JMH".equals(tokens[0])) {
            return;
        }
        String fullClassName = tokens[3];
        String methodName = tokens[9];
        try {
            Class<?> clazz = Class.forName(fullClassName);
            benchmarkMethodToClass.put(methodName, clazz.getSimpleName());
        } catch (ClassNotFoundException ignored) {
            // skip entries whose class cannot be loaded
        }
    }
}
