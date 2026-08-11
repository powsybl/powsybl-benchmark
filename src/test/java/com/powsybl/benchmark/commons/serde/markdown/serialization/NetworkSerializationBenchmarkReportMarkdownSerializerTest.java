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
class NetworkSerializationBenchmarkReportMarkdownSerializerTest extends AbstractMarkdownSerializerTest {

    @Test
    void testReportToString() throws IOException {
        String benchClass = "NetworkSerializationBenchmark";
        String deserialization = benchClass + ".benchmark1NetworkDeserialization";
        String streamSerialization = benchClass + ".benchmark2NetworkStreamSerialization";
        String fileSerialization = benchClass + ".benchmark3NetworkFileSerialization";
        String copy = benchClass + ".benchmark4NetworkCopy";
        Map<String, String> xiidmFormat = Map.of("format", "XIIDM");
        Map<String, String> jiidmFormat = Map.of("format", "JIIDM");
        Map<String, String> biidmFormat = Map.of("format", "BIIDM");
        Map<String, String> cgmesFormat = Map.of("format", "CGMES");

        List<RunResult> runResults = List.of(
            // Deserialization
            BenchmarkTestUtils.mockRunResult(deserialization, xiidmFormat, 1.034),
            BenchmarkTestUtils.mockRunResult(deserialization, jiidmFormat, 2.0),
            BenchmarkTestUtils.mockRunResult(deserialization, biidmFormat, 3.0),
            BenchmarkTestUtils.mockRunResult(deserialization, cgmesFormat, 4.0),

            // Stream Serialization
            BenchmarkTestUtils.mockRunResult(streamSerialization, xiidmFormat, 2.137),
            BenchmarkTestUtils.mockRunResult(streamSerialization, jiidmFormat, 1.0),
            BenchmarkTestUtils.mockRunResult(streamSerialization, biidmFormat, 4.0),
            // no CGMES stream serialization

            // File Serialization
            BenchmarkTestUtils.mockRunResult(fileSerialization, xiidmFormat, 3.0),
            BenchmarkTestUtils.mockRunResult(fileSerialization, jiidmFormat, 2.0002),
            BenchmarkTestUtils.mockRunResult(fileSerialization, biidmFormat, 1.078),
            BenchmarkTestUtils.mockRunResult(fileSerialization, cgmesFormat, 5.12),

            // Copy
            BenchmarkTestUtils.mockRunResult(copy, xiidmFormat, 4.0),
            BenchmarkTestUtils.mockRunResult(copy, jiidmFormat, 3.0),
            BenchmarkTestUtils.mockRunResult(copy, biidmFormat, 2.0)
        //no CGMES network copy
        );

        testReportToString(benchClass, runResults, "/network-serialization-report.md");
    }
}
