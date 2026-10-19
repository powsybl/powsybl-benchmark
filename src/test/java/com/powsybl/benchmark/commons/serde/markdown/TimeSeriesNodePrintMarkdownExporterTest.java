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
 * @author Nicolas Rol {@literal <nicolas.rol at rte-france.com>}
 */
public class TimeSeriesNodePrintMarkdownExporterTest extends AbstractMarkdownSerializerTest {

    @Override
    protected String getBenchClass() {
        return "NodePrintBenchmark";
    }

    @Override
    protected List<String> getResourcePaths() {
        return List.of("/timeseries-node-printing-report.md");
    }

    @Override
    protected List<String> getResourceWithBaselinePaths() {
        return List.of("/timeseries-node-printing-report-with-baseline.md");
    }

    @Override
    protected List<RunResult> buildResults() {
        String benchName = getBenchClass() + ".print";

        return List.of(
            BenchmarkTestUtils.mockRunResult(benchName, params("Node printing", "1000"), 10.0),
            BenchmarkTestUtils.mockRunResult(benchName, params("Node printing", "2000"), 15.0),
            BenchmarkTestUtils.mockRunResult(benchName, params("Node printing", "4000"), 20.0),
            BenchmarkTestUtils.mockRunResult(benchName, params("Node printing", "8000"), 25.0)
        );
    }

    private Map<String, String> params(String operation, String size) {
        return Map.of("Operation", operation, "size", size);
    }
}
