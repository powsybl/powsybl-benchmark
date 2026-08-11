/**
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.commons.serde.markdown.serialization;

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
class NetworkSerializationBenchmarkReportMarkdownSerializerTest {

    @Test
    void testReportToString() throws IOException {
        String benchClass = "NetworkSerializationBenchmark";
        String deserialization = benchClass + ".benchmark1NetworkDeserialization";
        String streamSerialization = benchClass + ".benchmark2NetworkStreamSerialization";
        String fileSerialization = benchClass + ".benchmark3NetworkFileSerialization";
        String copy = benchClass + ".benchmark4NetworkCopy";

        List<RunResult> runResults = List.of(
            // Deserialization
            BenchmarkTestUtils.mockRunResult(deserialization, Map.of("format", "XIIDM"), 1.0),
            BenchmarkTestUtils.mockRunResult(deserialization, Map.of("format", "JIIDM"), 2.0),
            BenchmarkTestUtils.mockRunResult(deserialization, Map.of("format", "BIIDM"), 3.0),
            BenchmarkTestUtils.mockRunResult(deserialization, Map.of("format", "CGMES"), 4.0),

            // Stream Serialization
            BenchmarkTestUtils.mockRunResult(streamSerialization, Map.of("format", "XIIDM"), 2.0),
            BenchmarkTestUtils.mockRunResult(streamSerialization, Map.of("format", "JIIDM"), 1.0),
            BenchmarkTestUtils.mockRunResult(streamSerialization, Map.of("format", "BIIDM"), 4.0),
            BenchmarkTestUtils.mockRunResult(streamSerialization, Map.of("format", "CGMES"), 3.0),

            // File Serialization
            BenchmarkTestUtils.mockRunResult(fileSerialization, Map.of("format", "XIIDM"), 3.0),
            BenchmarkTestUtils.mockRunResult(fileSerialization, Map.of("format", "JIIDM"), 2.0),
            BenchmarkTestUtils.mockRunResult(fileSerialization, Map.of("format", "BIIDM"), 1.0),
            BenchmarkTestUtils.mockRunResult(fileSerialization, Map.of("format", "CGMES"), 4.0),

            // Copy
            BenchmarkTestUtils.mockRunResult(copy, Map.of("format", "XIIDM"), 4.0),
            BenchmarkTestUtils.mockRunResult(copy, Map.of("format", "JIIDM"), 3.0),
            BenchmarkTestUtils.mockRunResult(copy, Map.of("format", "BIIDM"), 2.0),
            BenchmarkTestUtils.mockRunResult(copy, Map.of("format", "CGMES"), 1.0)
        );

        BenchmarkReport report = BenchmarkTestUtils.mockBenchmarkReport(benchClass, runResults);
        NetworkSerializationBenchmarkReportMarkdownSerializer serializer = new NetworkSerializationBenchmarkReportMarkdownSerializer();
        String actual = serializer.reportToString(report);

        String expected = new String(Objects.requireNonNull(getClass().getResourceAsStream("/network-serialization-report.md")).readAllBytes(), StandardCharsets.UTF_8);

        assertEquals(expected, actual);
    }
}
