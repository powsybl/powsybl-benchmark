/**
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.commons.serde.markdown.security;

import com.powsybl.benchmark.commons.Constants;
import com.powsybl.benchmark.commons.serde.BenchmarkTestUtils;
import com.powsybl.benchmark.commons.serde.markdown.AbstractMarkdownSerializerTest;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.results.RunResult;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author Dissoubray Nathan {@literal <nathan.dissoubray at rte-france.com>}
 */
class MultiThreadSecurityAnalysisBenchmarkReportMarkdownSerializerTest extends AbstractMarkdownSerializerTest {

    @Test
    void testReportToString() throws IOException {
        String benchClass = "MultiThreadSecurityAnalysisBenchmark";
        String benchName = benchClass + ".benchmark";

        List<RunResult> runResults = List.of(
            // IEEE 14
            //parallelization factor: 1
            BenchmarkTestUtils.mockRunResult(benchName, params(Constants.RTE_1888, "1"), 100.0),
            //parallelization factor: 100/(55*2) = 0.909 -> 0.91
            BenchmarkTestUtils.mockRunResult(benchName, params(Constants.RTE_1888, "2"), 55.0),
            //parallelization factor: 100/(30*4) = 0.8333 -> 0.83
            BenchmarkTestUtils.mockRunResult(benchName, params(Constants.RTE_1888, "4"), 30.0),
            //parallelization factor: 100/(20*8) = 0.625 -> 0.63
            BenchmarkTestUtils.mockRunResult(benchName, params(Constants.RTE_1888, "8"), 20.0),

            // IEEE 118
            //same factors since every number is double compared to the IEEE 14 network
            BenchmarkTestUtils.mockRunResult(benchName, params(Constants.RTE_6515, "1"), 200.0),
            BenchmarkTestUtils.mockRunResult(benchName, params(Constants.RTE_6515, "2"), 110.0),
            BenchmarkTestUtils.mockRunResult(benchName, params(Constants.RTE_6515, "4"), 60.0),
            BenchmarkTestUtils.mockRunResult(benchName, params(Constants.RTE_6515, "8"), 40.0),

            //another network
            BenchmarkTestUtils.mockRunResult(benchName, params("Unknown network", "1"), 150.0),
            BenchmarkTestUtils.mockRunResult(benchName, params("Unknown network", "2"), 77.32),
            BenchmarkTestUtils.mockRunResult(benchName, params("Unknown network", "4"), 56.82),
            BenchmarkTestUtils.mockRunResult(benchName, params("Unknown network", "8"), 89.29)
        );

        testReportToString(benchClass, runResults, "/multi-thread-security-analysis-report.md");
    }

    private Map<String, String> params(String networkName, String threadCount) {
        return Map.of("networkName", networkName, "threadCount", threadCount);
    }
}
