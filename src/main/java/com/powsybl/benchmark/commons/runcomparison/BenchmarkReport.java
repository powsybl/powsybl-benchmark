/**
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.commons.runcomparison;

import com.powsybl.openloadflow.util.PowsyblOpenLoadFlowVersion;
import com.powsybl.tools.PowsyblCoreVersion;
import org.openjdk.jmh.results.RunResult;

import java.time.ZoneOffset;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Results of all benchmarks for a given class. This aggregates all the BenchmarkResult of each benchmarked function
 * in that class.
 * @author Dissoubray Nathan {@literal <nathan.dissoubray at rte-france.com>}
 */
public record BenchmarkReport(
    String benchmarkClass,
    String powsyblCoreVersion,
    String openLoadFlowVersion,
    String datetime,
    List<BenchmarkResult> results
) {
    public BenchmarkReport(String benchmarkClass, Collection<RunResult> runResultsForThatClass) {
        this(
            benchmarkClass,
            new PowsyblCoreVersion().getMavenProjectVersion(),
            new PowsyblOpenLoadFlowVersion().getMavenProjectVersion(),
            java.time.LocalDateTime.now(ZoneOffset.UTC).toString(),
            runResultsForThatClass.stream().map(BenchmarkResult::new).toList()
        );
    }

    private static String getClassOfBenchmark(RunResult runResult) {
        String bench = runResult.getParams().getBenchmark();
        String nameSplitter = "\\.";
        String[] parts = bench.split(nameSplitter);
        //bench is com.powsybl.something.ClassName.BenchName
        //to get the class name, it's the second-to-last part
        return parts[parts.length - 2];
    }

    public static List<BenchmarkReport> buildAllReports(Collection<RunResult> runResults) {
        return runResults.stream()
            .collect(Collectors.groupingBy(BenchmarkReport::getClassOfBenchmark))
            .entrySet().stream()
            .map(e -> new BenchmarkReport(e.getKey(), e.getValue()))
            .toList();
    }
}
