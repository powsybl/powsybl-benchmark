/**
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.commons.serde.markdown;

import com.powsybl.benchmark.commons.serde.BenchmarkResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Make some code common between different multi-thread benchmark reports.
 * This is done instead of an abstract class because some multi-thread benchmarks are {@link AbstractByNetworkMarkdownResultsExporter}
 * whereas some are {@link AbstractMarkdownResultsExporter}
 *
 * @author Dissoubray Nathan {@literal <nathan.dissoubray at rte-france.com>}
 */
public final class MultiThreadUtil {

    private MultiThreadUtil() {
        //no constructor for utility class
    }

    /**
     * Generates a list of column names for a benchmarking table, using the provided
     * first column name followed by predefined thread-based column names.
     *
     * @param firstColumnName The name of the first column in the table.
     * @return An array of strings representing column names, starting with the provided
     *         first column name, followed by "1 thread", "2 threads", "4 threads",
     *         and "8 threads".
     */
    public static String[] columnNames(String firstColumnName) {
        return new String[] {
            firstColumnName,
            "1 thread",
            "2 threads",
            "4 threads",
            "8 threads"
        };
    }

    /**
     * Processes a list of {@code BenchmarkResult} objects and maps the number of threads
     * used (as extracted from each result) to the corresponding {@code BenchmarkResult}.
     *
     * @param results A list of {@code BenchmarkResult} objects, where each result contains
     *                performance metrics and associated parameters such as the number of threads.
     * @return A map where the keys are the number of threads (as an {@code Integer}) and
     *         the values are the corresponding {@code BenchmarkResult} objects.
     */
    public static Map<Integer, BenchmarkResult> getTimePerThread(List<BenchmarkResult> results) {
        return results.stream()
            .collect(Collectors.toMap(MultiThreadUtil::getThreadCount, Function.identity()));
    }

    /**
     * Retrieves the benchmark score for a single-threaded execution from the provided map.
     * If no benchmark result exists for 1 thread, an exception is thrown.
     *
     * @param timePerThread A map where the keys represent the number of threads (as {@code Integer})
     *                      and the values are {@code BenchmarkResult} objects containing performance metrics.
     * @return The benchmark score for a single-threaded execution as a {@code double}.
     * @throws NoSuchElementException If there is no benchmark result for 1 thread in the provided map.
     */
    public static double getOneThreadTime(Map<Integer, BenchmarkResult> timePerThread) {
        BenchmarkResult oneThread = timePerThread.get(1);
        if (oneThread == null) {
            throw new NoSuchElementException("There is no result for 1 thread");
        }
        return oneThread.score();
    }

    /**
     * Builds a table line representing benchmark results for various thread counts.
     * The first column is populated using a custom value derived from the provided list
     * of benchmark results, and subsequent columns correspond to formatted performance metrics
     * for each thread count.
     *
     * @param results A list of {@code BenchmarkResult} objects containing benchmarking data
     *                used to populate the table line.
     * @param firstColumnName The name of the first column in the table.
     * @param firstValueGetter A function that computes the value to be placed in the first column
     *                         based on the provided list of benchmark results.
     * @return A {@code Map} containing column names as keys and their corresponding values as values,
     *         where the first column corresponds to the custom value and subsequent columns
     *         correspond to thread-based performance metrics.
     */
    public static Map<String, String> buildTableLine(List<BenchmarkResult> results, String firstColumnName, Function<List<BenchmarkResult>, String> firstValueGetter) {
        Map<Integer, BenchmarkResult> timePerThread = MultiThreadUtil.getTimePerThread(results);
        double timeOneThread = MultiThreadUtil.getOneThreadTime(timePerThread);
        Map<String, String> line = new HashMap<>();
        line.put(firstColumnName, firstValueGetter.apply(results));
        for (Map.Entry<Integer, BenchmarkResult> threadEntry : timePerThread.entrySet()) {
            line.put(
                MultiThreadUtil.getPrettyColumnName(threadEntry.getKey()),
                MultiThreadUtil.getFormattedScoreAndEffectiveness(threadEntry, timeOneThread)
            );
        }
        return line;
    }

    /**
     * Generates a descriptive column name for a given thread count, appending "thread" or "threads" based on the value.
     *
     * @param threadCount The number of threads for which the column name is being generated.
     * @return A string representing the column name, formatted as "<threadCount> thread" if singular or "<threadCount> threads" if plural.
     */
    public static String getPrettyColumnName(int threadCount) {
        return threadCount + " thread" + (threadCount > 1 ? "s" : "");
    }

    /**
     * Retrieves the thread count from the provided {@code BenchmarkResult} object.
     *
     * This method extracts the "threadCount" parameter from the result's parameters map,
     * converts it to an integer, and returns the value.
     *
     * @param result The {@code BenchmarkResult} object containing benchmark data,
     *               including parameters such as "threadCount".
     * @return The number of threads as an integer, extracted from the "threadCount" parameter.
     * @throws NumberFormatException If the "threadCount" parameter is not a valid integer.
     * @throws NullPointerException  If the "threadCount" parameter is not present in the result's parameters map.
     */
    public static int getThreadCount(BenchmarkResult result) {
        return Integer.parseInt(result.parameters().get("threadCount"));
    }

    /**
     * Computes and formats the parallelization efficiency for a benchmark run.
     * The efficiency is calculated as the ratio of the single-threaded performance
     * to the multi-threaded performance, scaled by the number of threads.
     *
     * @param result The {@code BenchmarkResult} containing the performance metrics
     *               for the benchmark. The score from this result is used as the
     *               multi-threaded performance score.
     * @param timeOneThread The performance score from a single-threaded execution.
     *                      This value is used as the baseline for calculating efficiency.
     * @param threadCount The number of threads used for the multi-threaded execution
     *                    whose efficiency is being computed.
     * @return A formatted string representing the parallelization efficiency as a
     *         ratio, displayed as a decimal value in parentheses (e.g., "(0.75)").
     */
    public static String getFormattedParallelizationEfficiency(BenchmarkResult result, double timeOneThread, int threadCount) {
        return String.format(" (%.2f)", timeOneThread / (result.score() * threadCount));
    }

    /**
     * Combines the formatted score and a calculated parallelization efficiency
     * for a benchmark run into a single string.
     *
     * @param threadEntry A map entry where the key represents the number of threads
     *                    and the value is the {@code BenchmarkResult} containing
     *                    the performance metrics for the corresponding thread count.
     * @param timeOneThread The benchmark score for single-threaded execution, used
     *                      as a baseline for calculating the parallelization efficiency.
     * @return A string with the formatted score followed by the parallelization
     *         efficiency, represented as a ratio in parentheses.
     * @see #getFormattedParallelizationEfficiency(BenchmarkResult, double, int)
     */
    public static String getFormattedScoreAndEffectiveness(Map.Entry<Integer, BenchmarkResult> threadEntry, double timeOneThread) {
        return AbstractMarkdownResultsExporter.getFormattedScoreAndUnit(threadEntry.getValue())
            + getFormattedParallelizationEfficiency(threadEntry.getValue(), timeOneThread, threadEntry.getKey());
    }
}
