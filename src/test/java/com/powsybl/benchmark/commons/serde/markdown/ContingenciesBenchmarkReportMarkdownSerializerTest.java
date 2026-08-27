/**
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.commons.serde.markdown;

import com.powsybl.benchmark.commons.Constants;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.results.Result;
import org.openjdk.jmh.results.RunResult;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static com.powsybl.benchmark.commons.serde.BenchmarkTestUtils.mockResult;
import static com.powsybl.benchmark.commons.serde.BenchmarkTestUtils.mockRunResult;

/**
 * @author Dissoubray Nathan {@literal <nathan.dissoubray at rte-france.com>}
 */
class ContingenciesBenchmarkReportMarkdownSerializerTest extends AbstractMarkdownSerializerTest {
    private static final String BENCH_CLASS = "MonoThreadSecurityAnalysisBenchmark";
    private static final String BENCH_NAME = BENCH_CLASS + ".benchmark";

    @Test
    void testReportToString() throws IOException {
        List<RunResult> runResults = buildResults();

        testReportToString(BENCH_CLASS, runResults, "/contingencies-report.md");
    }

    @Test
    void testReportToStringWithoutBaseline() throws IOException {
        List<RunResult> runResults = buildResults();

        testReportToString(BENCH_CLASS, runResults, "/contingencies-report.md", Path.of("non-existent-path"));
    }

    @Test
    void testReportToStringWithBaseline() throws IOException, URISyntaxException {
        List<RunResult> runResults = buildResults();

        testReportToString(BENCH_CLASS, runResults, "/contingencies-report-with-baseline.md",
            Paths.get(getClass().getResource("/baseline").toURI()));
    }

    private Map<String, String> params(String networkName, String type) {
        return Map.of("networkName", networkName, "type", type);
    }

    private Map<String, Result<?>> paramSecondary(double numberOfContingencies) {
        return Map.of("numberOfContingencies", mockResult(numberOfContingencies));
    }

    private List<RunResult> buildResults() {
        return List.of(
            // IEEE 14
            mockRunResult(BENCH_NAME, params(Constants.IEEE_14, "BASIC"), Mode.AverageTime, 10.0, 0.1, "ms/op", paramSecondary(10.0)),
            mockRunResult(BENCH_NAME, params(Constants.IEEE_14, "STANDARD"), Mode.AverageTime, 15.0, 0.1, "ms/op", paramSecondary(10.0)),
            mockRunResult(BENCH_NAME, params(Constants.IEEE_14, "STANDARD_REACTIVE_LIMITS_NOT_USED"), Mode.AverageTime, 12.0, 0.1, "ms/op", paramSecondary(10.0)),

            // IEEE 118
            mockRunResult(BENCH_NAME, params(Constants.IEEE_118, "BASIC"), Mode.AverageTime, 103.0, 0.1, "ms/op", paramSecondary(100.0)),
            mockRunResult(BENCH_NAME, params(Constants.IEEE_118, "STANDARD"), Mode.AverageTime, 177.7, 0.1, "ms/op", paramSecondary(100.0)),
            mockRunResult(BENCH_NAME, params(Constants.IEEE_118, "STANDARD_REACTIVE_LIMITS_NOT_USED"), Mode.AverageTime, 124.28, 0.1, "ms/op", paramSecondary(100.0))
        );
    }
}
