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
import com.powsybl.benchmark.commons.state.LoadFlowParametersType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Dissoubray Nathan {@literal <nathan.dissoubray at rte-france.com>}
 */
@AutoService(ResultsExporter.class)
public class LoadFlowMarkdownResultsExporter extends AbstractByNetworkMarkdownResultsExporter {

    private static final List<String> BENCHMARKS_CLASSES = List.of("LoadFlowBenchmark");
    private static final Map<LoadFlowParametersType, String> LOAD_FLOW_PARAMETERS_TYPE_COLUMN_NAMES = Map.of(
        LoadFlowParametersType.BASIC, "Basic parameters",
        LoadFlowParametersType.STANDARD, "Standard parameters",
        LoadFlowParametersType.STANDARD_REACTIVE_LIMITS_NOT_USED, "Standard parameters <br/>with reactive limits not used"
    );

    public LoadFlowMarkdownResultsExporter() {
        super(BENCHMARKS_CLASSES);
    }

    public LoadFlowMarkdownResultsExporter(List<String> supportedBenchmarkClasses) {
        super(supportedBenchmarkClasses);
    }

    @Override
    protected String[] columnNames(List<BenchmarkResult> resultsForNetwork) {
        return getColumnNamesWithFirstColumnsAndColumnsProvider(resultsForNetwork, this::getPrettyColumnName, "Network");
    }

    @Override
    protected Map<String, String> getLine(List<BenchmarkResult> resultsForNetwork) {
        Map<String, String> line = new HashMap<>(resultsForNetwork.size() + 1, 1);
        line.put("Network", Constants.getPrettyNetworkName(resultsForNetwork.getFirst().parameters().get("networkName")));
        resultsForNetwork.forEach(result -> line.put(getPrettyColumnName(result), getFormattedScoreAndUnit(result)));
        return line;
    }

    protected String getPrettyColumnName(BenchmarkResult benchmarkResult) {
        return LOAD_FLOW_PARAMETERS_TYPE_COLUMN_NAMES.get(LoadFlowParametersType.valueOf(benchmarkResult.parameters().get("type")));
    }
}
