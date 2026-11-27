/**
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.commons.serde.markdown;

import com.powsybl.benchmark.commons.Constants;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.results.Result;
import org.openjdk.jmh.results.RunResult;

import java.util.List;
import java.util.Map;

import static com.powsybl.benchmark.commons.serde.BenchmarkTestUtils.mockResult;
import static com.powsybl.benchmark.commons.serde.BenchmarkTestUtils.mockRunResult;

/**
 * Check the export when the benchmark has been run with a single load flow parameters type (e.g. "-p type=BASIC").
 *
 * @author Michaël Gabay {@literal <michael.gabay at artelys.com>}
 */
class ContingenciesSingleParametersTypeBenchmarkReportMarkdownSerializerTest extends AbstractMarkdownSerializerTest {

    private Map<String, String> params(String networkName, String type) {
        return Map.of("networkName", networkName, "type", type);
    }

    private Map<String, Result<?>> paramSecondary(double numberOfContingencies) {
        return Map.of("numberOfContingencies", mockResult(numberOfContingencies));
    }

    @Override
    protected String getBenchClass() {
        return "MonoThreadSecurityAnalysisBenchmark";
    }

    @Override
    protected List<String> getResourcePaths() {
        return List.of("/contingencies-single-parameters-type-report.md");
    }

    @Override
    protected List<String> getResourceWithBaselinePaths() {
        return List.of("/contingencies-single-parameters-type-report-with-baseline.md");
    }

    @Override
    protected List<RunResult> buildResults() {
        String benchName = getBenchClass() + ".benchmark";
        return List.of(
            mockRunResult(benchName, params(Constants.IEEE_14, "BASIC"), Mode.AverageTime, 10.0, 0.1, "ms/op", paramSecondary(10.0)),
            mockRunResult(benchName, params(Constants.IEEE_118, "BASIC"), Mode.AverageTime, 103.0, 0.1, "ms/op", paramSecondary(100.0))
        );
    }
}
