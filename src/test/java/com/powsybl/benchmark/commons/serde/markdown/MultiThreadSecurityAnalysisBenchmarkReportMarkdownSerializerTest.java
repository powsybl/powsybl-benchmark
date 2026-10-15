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
class MultiThreadSecurityAnalysisBenchmarkReportMarkdownSerializerTest extends AbstractMarkdownSerializerTest {

    private Map<String, String> params(String networkName, String threadCount) {
        return Map.of("networkName", networkName, "threadCount", threadCount);
    }

    @Override
    protected String getBenchClass() {
        return "MultiThreadSecurityAnalysisBenchmark";
    }

    @Override
    protected List<String> getBenchFilenames() {
        return List.of(getBenchClass() + ".md");
    }

    @Override
    protected List<String> getResourcePaths() {
        return List.of("/multi-thread-security-analysis-report.md");
    }

    @Override
    protected List<String> getResourceWithBaselinePaths() {
        return List.of("/multi-thread-security-analysis-report-with-baseline.md");
    }

    @Override
    protected String getBaselineDirectoryPathString() {
        return "/baseline";
    }

    @Override
    protected List<RunResult> buildResults() {
        String benchName = getBenchClass() + ".benchmark";
        return List.of(
            BenchmarkTestUtils.mockRunResult(benchName, params(Constants.RTE_1888, "1"), 100.0),
            BenchmarkTestUtils.mockRunResult(benchName, params(Constants.RTE_1888, "2"), 55.0),
            BenchmarkTestUtils.mockRunResult(benchName, params(Constants.RTE_1888, "4"), 30.0),
            BenchmarkTestUtils.mockRunResult(benchName, params(Constants.RTE_1888, "8"), 20.0),
            BenchmarkTestUtils.mockRunResult(benchName, params(Constants.RTE_6515, "1"), 200.0),
            BenchmarkTestUtils.mockRunResult(benchName, params(Constants.RTE_6515, "2"), 110.0),
            BenchmarkTestUtils.mockRunResult(benchName, params(Constants.RTE_6515, "4"), 60.0),
            BenchmarkTestUtils.mockRunResult(benchName, params(Constants.RTE_6515, "8"), 40.0),
            BenchmarkTestUtils.mockRunResult(benchName, params("Unknown network", "1"), 150.0),
            BenchmarkTestUtils.mockRunResult(benchName, params("Unknown network", "2"), 77.32),
            BenchmarkTestUtils.mockRunResult(benchName, params("Unknown network", "4"), 56.82),
            BenchmarkTestUtils.mockRunResult(benchName, params("Unknown network", "8"), 89.29)
        );
    }
}
