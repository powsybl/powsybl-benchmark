/**
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.commons.serde.markdown;

import com.google.auto.service.AutoService;
import com.powsybl.benchmark.commons.Constants;
import com.powsybl.benchmark.commons.serde.BenchmarkResult;
import com.powsybl.benchmark.commons.serde.ResultsExporter;

import java.util.List;
import java.util.Map;

/**
 * @author Dissoubray Nathan {@literal <nathan.dissoubray at rte-france.com>}
 */
@AutoService(ResultsExporter.class)
public class MultiThreadSecurityAnalysisMarkdownResultsExporter extends AbstractByNetworkMarkdownResultsExporter {

    private static final List<String> BENCHMARKS_CLASSES = List.of("MultiThreadSecurityAnalysisBenchmark");

    @Override
    public boolean isBenchmarkClassSupported(String benchmarkClass) {
        return BENCHMARKS_CLASSES.contains(benchmarkClass);
    }

    @Override
    protected String[] columnNames() {
        return MultiThreadUtil.columnNames("Network");
    }

    @Override
    protected Map<String, String> getLine(List<BenchmarkResult> resultsForNetwork) {
        return MultiThreadUtil.buildTableLine(
            resultsForNetwork,
            "Network",
            results -> Constants.getPrettyNetworkName(results.getFirst().parameters().get("networkName")));
    }
}
