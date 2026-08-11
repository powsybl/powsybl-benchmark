/**
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.commons.serde.markdown.loadflow;

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
class LoadFlowBenchmarkReportMarkdownSerializerTest {

    @Test
    void testReportToString() throws IOException {
        String benchClass = "LoadFlowBenchmark";
        String benchName = benchClass + ".benchmarkLoadFlow";

        List<RunResult> runResults = List.of(
            // IEEE 14
            BenchmarkTestUtils.mockRunResult(benchName, params(Constants.IEEE_14, "BASIC"), 10.0),
            BenchmarkTestUtils.mockRunResult(benchName, params(Constants.IEEE_14, "STANDARD"), 15.0),
            BenchmarkTestUtils.mockRunResult(benchName, params(Constants.IEEE_14, "STANDARD_REACTIVE_LIMITS_NOT_USED"), 12.0),

            // IEEE 118
            BenchmarkTestUtils.mockRunResult(benchName, params(Constants.IEEE_118, "BASIC"), 100.0),
            BenchmarkTestUtils.mockRunResult(benchName, params(Constants.IEEE_118, "STANDARD"), 150.0),
            BenchmarkTestUtils.mockRunResult(benchName, params(Constants.IEEE_118, "STANDARD_REACTIVE_LIMITS_NOT_USED"), 120.0)
        );

        BenchmarkReport report = BenchmarkTestUtils.mockBenchmarkReport(benchClass, runResults);
        LoadFlowBenchmarkReportMarkdownSerializer serializer = new LoadFlowBenchmarkReportMarkdownSerializer();
        String actual = serializer.reportToString(report);

        String expected = new String(Objects.requireNonNull(getClass().getResourceAsStream("/load-flow-report.md")).readAllBytes(), StandardCharsets.UTF_8);

        assertEquals(expected, actual);
    }

    private Map<String, String> params(String networkName, String type) {
        return Map.of("networkName", networkName, "type", type);
    }
}
