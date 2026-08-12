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

import java.util.Map;

/**
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
