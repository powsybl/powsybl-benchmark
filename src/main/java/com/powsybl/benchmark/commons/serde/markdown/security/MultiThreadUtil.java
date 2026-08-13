/**
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.commons.serde.markdown.security;

import com.powsybl.benchmark.commons.serde.BenchmarkResult;
import com.powsybl.benchmark.commons.serde.markdown.AbstractBenchmarkReportMarkdownSerializer;
import com.powsybl.benchmark.commons.serde.markdown.AbstractByNetworkBenchmarkReportMarkdownSerializer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Make some code common between different multi-thread benchmark reports.
 * This is done instead of an abstract class because some multi-thread benchmarks are {@link AbstractByNetworkBenchmarkReportMarkdownSerializer}
 * whereas some are {@link AbstractBenchmarkReportMarkdownSerializer}
 * @author Dissoubray Nathan {@literal <nathan.dissoubray at rte-france.com>}
 */
public final class MultiThreadUtil {

    private MultiThreadUtil() {
        //no constructor for utility class
    }

    public static String[] columnNames(String firstColumnName) {
        return new String[] {
            firstColumnName,
            "1 thread",
            "2 threads",
            "4 threads",
            "8 threads"
        };
    }

    public static Map<Integer, BenchmarkResult> getTimePerThread(List<BenchmarkResult> results) {
        return results.stream()
           .collect(Collectors.toMap(MultiThreadUtil::getThreadCount, Function.identity()));
    }

    public static double getOneThreadTime(Map<Integer, BenchmarkResult> timePerThread) {
        BenchmarkResult oneThread = timePerThread.get(1);
        if (oneThread == null) {
            throw new NoSuchElementException("There is no result for 1 thread");
        }
        return oneThread.score();
    }

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

    public static String getPrettyColumnName(int threadCount) {
        return threadCount + " thread" + (threadCount > 1 ? "s" : "");
    }

    public static int getThreadCount(BenchmarkResult result) {
        return Integer.parseInt(result.parameters().get("threadCount"));
    }

    public static String getFormattedScoreAndEffectiveness(BenchmarkResult result, double timeOneThread, int threadCount) {
        return AbstractBenchmarkReportMarkdownSerializer.getFormattedScoreAndUnit(result) + getFormattedParallelizationEfficiency(result, timeOneThread, threadCount);
    }

    public static String getFormattedParallelizationEfficiency(BenchmarkResult result, double timeOneThread, int threadCount) {
        return String.format(" (%.2f)", timeOneThread / (result.score() * threadCount));
    }

    public static String getFormattedScoreAndEffectiveness(Map.Entry<Integer, BenchmarkResult> threadEntry, double timeOneThread) {
        return AbstractBenchmarkReportMarkdownSerializer.getFormattedScoreAndUnit(threadEntry.getValue())
            + getFormattedParallelizationEfficiency(threadEntry.getValue(), timeOneThread, threadEntry.getKey());
    }
}
