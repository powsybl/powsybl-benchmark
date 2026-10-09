/**
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.commons.serde.markdown;

import com.powsybl.benchmark.commons.serde.BenchmarkTestUtils;
import org.openjdk.jmh.results.RunResult;

import java.util.List;
import java.util.Map;

/**
 * @author Dissoubray Nathan {@literal <nathan.dissoubray at rte-france.com>}
 */
class NetworkSerializationBenchmarkReportMarkdownSerializerTest extends AbstractMarkdownSerializerTest {

    private static Map<String, String> params(String networkName, String format) {
        return Map.of("networkName", networkName, "format", format);
    }

    @Override
    protected String getBenchClass() {
        return "NetworkSerializationBenchmark";
    }

    @Override
    protected List<String> getBenchFilenames() {
        return List.of(getBenchClass() + "_firstNetwork.md", getBenchClass() + "_secondNetwork.md");
    }

    @Override
    protected List<String> getResourcePaths() {
        return List.of("/network-serialization-report_1.md", "/network-serialization-report_2.md");
    }

    @Override
    protected List<String> getResourceWithBaselinePaths() {
        return List.of("/network-serialization-report_1-with-baseline.md", "/network-serialization-report_2-with-baseline.md");
    }

    @Override
    protected String getBaselineDirectoryPathString() {
        return "/baseline";
    }

    @Override
    protected List<RunResult> buildResults() {
        String benchClass = getBenchClass();
        String deserialization = benchClass + ".benchmark1NetworkDeserialization";
        String streamSerialization = benchClass + ".benchmark2NetworkStreamSerialization";
        String fileSerialization = benchClass + ".benchmark3NetworkFileSerialization";
        String copy = benchClass + ".benchmark4NetworkCopy";

        Map<String, String> xiidmFormat1 = params("firstNetwork", "XIIDM");
        Map<String, String> jiidmFormat1 = params("firstNetwork", "JIIDM");
        Map<String, String> biidmFormat1 = params("firstNetwork", "BIIDM");
        Map<String, String> cgmesFormat1 = params("firstNetwork", "CGMES");

        Map<String, String> xiidmFormat2 = params("secondNetwork", "XIIDM");
        Map<String, String> jiidmFormat2 = params("secondNetwork", "JIIDM");
        Map<String, String> biidmFormat2 = params("secondNetwork", "BIIDM");
        Map<String, String> cgmesFormat2 = params("secondNetwork", "CGMES");

        return List.of(
            // Deserialization
            BenchmarkTestUtils.mockRunResult(deserialization, xiidmFormat1, 1.034),
            BenchmarkTestUtils.mockRunResult(deserialization, jiidmFormat1, 2.0),
            BenchmarkTestUtils.mockRunResult(deserialization, biidmFormat1, 3.0),
            BenchmarkTestUtils.mockRunResult(deserialization, cgmesFormat1, 4.0),

            // Stream Serialization
            BenchmarkTestUtils.mockRunResult(streamSerialization, xiidmFormat1, 2.137),
            BenchmarkTestUtils.mockRunResult(streamSerialization, jiidmFormat1, 1.0),
            BenchmarkTestUtils.mockRunResult(streamSerialization, biidmFormat1, 4.0),
            // no CGMES stream serialization

            // File Serialization
            BenchmarkTestUtils.mockRunResult(fileSerialization, xiidmFormat1, 3.0),
            BenchmarkTestUtils.mockRunResult(fileSerialization, jiidmFormat1, 2.0002),
            BenchmarkTestUtils.mockRunResult(fileSerialization, biidmFormat1, 1.078),
            BenchmarkTestUtils.mockRunResult(fileSerialization, cgmesFormat1, 5.12),

            // Copy
            BenchmarkTestUtils.mockRunResult(copy, xiidmFormat1, 4.0),
            BenchmarkTestUtils.mockRunResult(copy, jiidmFormat1, 3.0),
            BenchmarkTestUtils.mockRunResult(copy, biidmFormat1, 2.0),
            //no CGMES network copy

            BenchmarkTestUtils.mockRunResult(deserialization, xiidmFormat2, 2.3),
            BenchmarkTestUtils.mockRunResult(deserialization, jiidmFormat2, 4.0),
            BenchmarkTestUtils.mockRunResult(deserialization, biidmFormat2, 1.7),
            BenchmarkTestUtils.mockRunResult(deserialization, cgmesFormat2, 5.5)
        );
    }
}
