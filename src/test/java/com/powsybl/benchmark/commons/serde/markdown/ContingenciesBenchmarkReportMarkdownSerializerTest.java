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
import java.util.List;
import java.util.Map;

import static com.powsybl.benchmark.commons.serde.BenchmarkTestUtils.mockResult;
import static com.powsybl.benchmark.commons.serde.BenchmarkTestUtils.mockRunResult;

/**
 * @author Dissoubray Nathan {@literal <nathan.dissoubray at rte-france.com>}
 */
class ContingenciesBenchmarkReportMarkdownSerializerTest extends AbstractMarkdownSerializerTest {

    @Test
    void testReportToString() throws IOException {
        String benchClass = "MonoThreadSecurityAnalysisBenchmark";
        String benchName = benchClass + ".benchmark";

        List<RunResult> runResults = List.of(
            // IEEE 14
            mockRunResult(benchName, paramNetwork(Constants.IEEE_14), Mode.AverageTime, 10.0, 0.1, "ms/op", paramSecondary(10.0)),
            mockRunResult(benchName, paramNetwork(Constants.IEEE_14), Mode.AverageTime, 15.0, 0.1, "ms/op", paramSecondary(10.0)),
            mockRunResult(benchName, paramNetwork(Constants.IEEE_14), Mode.AverageTime, 12.0, 0.1, "ms/op", paramSecondary(10.0)),

            // IEEE 118
            mockRunResult(benchName, paramNetwork(Constants.IEEE_118), Mode.AverageTime, 103.0, 0.1, "ms/op", paramSecondary(100.0)),
            mockRunResult(benchName, paramNetwork(Constants.IEEE_118), Mode.AverageTime, 177.7, 0.1, "ms/op", paramSecondary(100.0)),
            mockRunResult(benchName, paramNetwork(Constants.IEEE_118), Mode.AverageTime, 124.28, 0.1, "ms/op", paramSecondary(100.0))
        );

        testReportToString(benchClass, runResults, "/contingencies-report.md");
    }

    private Map<String, String> paramNetwork(String networkName) {
        return Map.of("networkName", networkName);
    }

    private Map<String, Result<?>> paramSecondary(double numberOfContingencies) {
        return Map.of("numberOfContingencies", mockResult(numberOfContingencies));
    }
}
