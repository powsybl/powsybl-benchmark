/**
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.commons.serde.markdown;

import com.powsybl.benchmark.commons.serde.BenchmarkTestUtils;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.results.RunResult;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author Nicolas Rol {@literal <nicolas.rol at rte-france.com>}
 */
public class TimeSeriesSplitMarkdownExporterTest extends AbstractMarkdownSerializerTest {

    @Test
    void testReportToString() throws IOException {
        String benchClass = "SplitPerfBenchmark";
        String benchName = benchClass + ".split";

        List<RunResult> runResults = List.of(
            BenchmarkTestUtils.mockRunResult(benchName, params("SINGLE_CHUNK", "100", "1000"), 1.0),
            BenchmarkTestUtils.mockRunResult(benchName, params("SINGLE_CHUNK", "100", "10000"), 2.0),
            BenchmarkTestUtils.mockRunResult(benchName, params("SINGLE_CHUNK", "100", "100000"), 3.0),
            BenchmarkTestUtils.mockRunResult(benchName, params("SINGLE_CHUNK", "1000", "1000"), 4.0),
            BenchmarkTestUtils.mockRunResult(benchName, params("SINGLE_CHUNK", "1000", "10000"), 5.0),
            BenchmarkTestUtils.mockRunResult(benchName, params("SINGLE_CHUNK", "1000", "100000"), 6.0),
            BenchmarkTestUtils.mockRunResult(benchName, params("FRAGMENTED", "100", "1000"), 7.0),
            BenchmarkTestUtils.mockRunResult(benchName, params("FRAGMENTED", "100", "10000"), 8.0),
            BenchmarkTestUtils.mockRunResult(benchName, params("FRAGMENTED", "100", "100000"), 9.0),
            BenchmarkTestUtils.mockRunResult(benchName, params("FRAGMENTED", "1000", "1000"), 10.0),
            BenchmarkTestUtils.mockRunResult(benchName, params("FRAGMENTED", "1000", "10000"), 11.0),
            BenchmarkTestUtils.mockRunResult(benchName, params("FRAGMENTED", "1000", "100000"), 12.0)
        );

        testReportToString(benchClass, runResults, "/timeseries-split-report.md");
    }

    private Map<String, String> params(String layout, String newChunkSize, String size) {
        return Map.of("layout", layout, "newChunkSize", newChunkSize, "size", size);
    }
}
