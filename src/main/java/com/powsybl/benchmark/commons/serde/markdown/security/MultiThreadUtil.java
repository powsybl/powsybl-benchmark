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

import java.util.Comparator;
import java.util.List;

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

    //TODO need to generalize the sorting in AbstractBenchmarkReportMarkdownSerializer as an abstract method
    public static double sortColumnsAndGetOneThreadTime(List<BenchmarkResult> results) {
        results.sort(Comparator.comparingInt(r -> Integer.parseInt(r.parameters().get("threadCount"))));
        if (!results.getFirst().parameters().get("threadCount").equals("1")) {
            throw new IllegalStateException("No result found for 1 thread");
        }
        return results.getFirst().score();
    }

    public static String getFormatedScoreAndEffectiveness(BenchmarkResult result, double timeOneThread, int threadCount) {
        return AbstractBenchmarkReportMarkdownSerializer.getFormattedScoreAndUnit(result) + getFormattedParallelizationEfficiency(result, timeOneThread, threadCount);
    }

    public static String getFormattedParallelizationEfficiency(BenchmarkResult result, double timeOneThread, int threadCount) {
        return String.format(" (%.2f)", timeOneThread / (result.score() * threadCount));
    }
}
