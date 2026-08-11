/**
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.commons.serde.markdown.security;

import com.powsybl.benchmark.commons.Constants;
import com.powsybl.benchmark.commons.serde.BenchmarkReport;
import com.powsybl.benchmark.commons.serde.BenchmarkTestUtils;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.results.RunResult;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Dissoubray Nathan {@literal <nathan.dissoubray at rte-france.com>}
 */
class MultiThreadSecurityAnalysisBenchmarkReportMarkdownSerializerTest {

    @Test
    void testReportToString() throws IOException {
        String benchClass = "MultiThreadSecurityAnalysisBenchmark";
        String benchName = benchClass + ".benchmark";

        List<RunResult> runResults = List.of(
            // IEEE 14
            //parallelization factor: 1
            BenchmarkTestUtils.mockRunResult(benchName, params(Constants.IEEE_14, "1"), 100.0),
            //parallelization factor: 100/(55*2) = 0.909 -> 0.91
            BenchmarkTestUtils.mockRunResult(benchName, params(Constants.IEEE_14, "2"), 55.0),
            //parallelization factor: 100/(30*4) = 0.8333 -> 0.83
            BenchmarkTestUtils.mockRunResult(benchName, params(Constants.IEEE_14, "4"), 30.0),
            //parallelization factor: 100/(20*8) = 0.625 -> 0.63
            BenchmarkTestUtils.mockRunResult(benchName, params(Constants.IEEE_14, "8"), 20.0),

            // IEEE 118
            //same factors since every number is double compared to the IEEE 14 network
            BenchmarkTestUtils.mockRunResult(benchName, params(Constants.IEEE_118, "1"), 200.0),
            BenchmarkTestUtils.mockRunResult(benchName, params(Constants.IEEE_118, "2"), 110.0),
            BenchmarkTestUtils.mockRunResult(benchName, params(Constants.IEEE_118, "4"), 60.0),
            BenchmarkTestUtils.mockRunResult(benchName, params(Constants.IEEE_118, "8"), 40.0)
        );

        BenchmarkReport report = BenchmarkTestUtils.mockBenchmarkReport(benchClass, runResults);
        MultiThreadSecurityAnalysisBenchmarkReportMarkdownSerializer serializer = new MultiThreadSecurityAnalysisBenchmarkReportMarkdownSerializer();
        String actual = serializer.reportToString(report);

        String expected = new String(Objects.requireNonNull(getClass().getResourceAsStream("/multi-thread-security-analysis-report.md")).readAllBytes(), StandardCharsets.UTF_8);

        assertEquals(expected, actual);
    }

    private Map<String, String> params(String networkName, String threadCount) {
        return Map.of("networkName", networkName, "threadCount", threadCount);
    }
}
