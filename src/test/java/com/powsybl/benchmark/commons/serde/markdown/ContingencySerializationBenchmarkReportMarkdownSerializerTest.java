/**
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.commons.serde.markdown;

import org.openjdk.jmh.results.RunResult;

import java.util.List;
import java.util.Map;

import static com.powsybl.benchmark.commons.serde.BenchmarkTestUtils.mockRunResult;

/**
 * @author Dissoubray Nathan {@literal <nathan.dissoubray at rte-france.com>}
 */
class ContingencySerializationBenchmarkReportMarkdownSerializerTest extends AbstractMarkdownSerializerTest {

    @Override
    protected String getBenchClass() {
        return "ContingencySerializationBenchmark";
    }

    @Override
    protected List<String> getBenchFilenames() {
        return List.of(getBenchClass() + ".md");
    }

    @Override
    protected List<String> getResourcePaths() {
        return List.of("/contingency-serialization-report.md");
    }

    @Override
    protected List<String> getResourceWithBaselinePaths() {
        return List.of("/contingency-serialization-report-with-baseline.md");
    }

    @Override
    protected String getBaselineDirectoryPathString() {
        return "/baseline";
    }

    @Override
    protected List<RunResult> buildResults() {
        String benchName = getBenchClass() + ".";
        return List.of(
            mockRunResult(benchName + "benchmark1Parsing", Map.of(), 1.0),
            mockRunResult(benchName + "benchmark2ParsingFromBytes", Map.of(), 2.0),
            mockRunResult(benchName + "benchmark3JustReading", Map.of(), 3.0),
            mockRunResult(benchName + "benchmark4ReadingToString", Map.of(), 4.0),
            mockRunResult(benchName + "benchmark5Writing", Map.of(), 5.0),
            mockRunResult(benchName + "benchmark6BufferedWriting", Map.of(), 6.0)
        );
    }
}
