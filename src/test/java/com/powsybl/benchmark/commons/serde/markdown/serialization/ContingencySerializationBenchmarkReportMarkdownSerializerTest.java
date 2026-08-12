/**
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.commons.serde.markdown.serialization;

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
class ContingencySerializationBenchmarkReportMarkdownSerializerTest extends AbstractMarkdownSerializerTest {

    @Test
    void testReportToString() throws IOException {
        String benchClass = "ContingencySerializationBenchmark";
        String baseName = "com.powsybl.benchmark.serialization.ContingencySerializationBenchmark.";

        List<RunResult> runResults = List.of(
            BenchmarkTestUtils.mockRunResult(baseName + "benchmark1Parsing", Map.of(), 1.0),
            BenchmarkTestUtils.mockRunResult(baseName + "benchmark2ParsingFromBytes", Map.of(), 2.0),
            BenchmarkTestUtils.mockRunResult(baseName + "benchmark3JustReading", Map.of(), 3.0),
            BenchmarkTestUtils.mockRunResult(baseName + "benchmark4ReadingToString", Map.of(), 4.0),
            BenchmarkTestUtils.mockRunResult(baseName + "benchmark5Writing", Map.of(), 5.0),
            BenchmarkTestUtils.mockRunResult(baseName + "benchmark6BufferedWriting", Map.of(), 6.0)
        );

        testReportToString(benchClass, runResults, "/contingency-serialization-report.md");
    }
}
