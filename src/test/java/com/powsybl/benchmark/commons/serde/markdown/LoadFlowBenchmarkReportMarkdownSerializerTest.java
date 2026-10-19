/**
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.commons.serde.markdown;

import com.powsybl.benchmark.commons.Constants;
import com.powsybl.benchmark.commons.serde.BenchmarkTestUtils;
import org.openjdk.jmh.results.RunResult;

import java.util.List;
import java.util.Map;

/**
 * @author Dissoubray Nathan {@literal <nathan.dissoubray at rte-france.com>}
 */
class LoadFlowBenchmarkReportMarkdownSerializerTest extends AbstractMarkdownSerializerTest {

    private Map<String, String> params(String networkName, String type) {
        return Map.of("networkName", networkName, "type", type);
    }

    @Override
    protected String getBenchClass() {
        return "LoadFlowBenchmark";
    }

    @Override
    protected List<String> getResourcePaths() {
        return List.of("/load-flow-report.md");
    }

    @Override
    protected List<String> getResourceWithBaselinePaths() {
        return List.of("/load-flow-report-with-baseline.md");
    }

    @Override
    protected List<RunResult> buildResults() {
        String benchName = getBenchClass() + ".benchmarkLoadFlow";
        return List.of(
            BenchmarkTestUtils.mockRunResult(benchName, params(Constants.IEEE_300, "BASIC"), 10.0),
            BenchmarkTestUtils.mockRunResult(benchName, params(Constants.IEEE_300, "STANDARD"), 15.0),
            BenchmarkTestUtils.mockRunResult(benchName, params(Constants.IEEE_300, "STANDARD_REACTIVE_LIMITS_NOT_USED"), 12.0),
            BenchmarkTestUtils.mockRunResult(benchName, params(Constants.REAL_GRID, "BASIC"), 100.0),
            BenchmarkTestUtils.mockRunResult(benchName, params(Constants.REAL_GRID, "STANDARD"), 150.0),
            BenchmarkTestUtils.mockRunResult(benchName, params(Constants.REAL_GRID, "STANDARD_REACTIVE_LIMITS_NOT_USED"), 120.0)
        );
    }
}
