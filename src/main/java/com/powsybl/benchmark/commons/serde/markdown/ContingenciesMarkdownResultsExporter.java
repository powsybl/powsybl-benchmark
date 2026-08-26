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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.DoubleUnaryOperator;
import java.util.function.ToIntFunction;

/**
 * @author Dissoubray Nathan {@literal <nathan.dissoubray at rte-france.com>}
 */
@AutoService(ResultsExporter.class)
public class ContingenciesMarkdownResultsExporter extends LoadFlowMarkdownResultsExporter {

    private static final List<String> BENCHMARKS_CLASSES = List.of(
        "MonoThreadSecurityAnalysisBenchmark",
        "SensitivityAnalysisBenchmark");

    public ContingenciesMarkdownResultsExporter() {
        super(BENCHMARKS_CLASSES);
    }

    @Override
    protected String[] columnNames(List<BenchmarkResult> resultsForNetwork) {
        return getColumnNamesWithFirstColumnsAndColumnsProvider(resultsForNetwork, this::getPrettyColumnName, "Network", "Contingencies");
    }

    @Override
    protected Map<String, String> getLine(List<BenchmarkResult> results) {
        Map<String, Double> lineScores = getLineScores(results);
        //this is formatted like a double in the string, but it's an int, get the integer part with the split
        ToIntFunction<BenchmarkResult> contingenciesNumberGetter = r -> Integer.parseInt(r.parameters().get("numberOfContingencies").split("\\.")[0]);
        //all results have the same number of contingencies, just get the first
        int contingenciesNumber = contingenciesNumberGetter.applyAsInt(results.getFirst());
        DoubleUnaryOperator scorePerContingency = d -> d / contingenciesNumber;
        //check that all results for a given network have the same number of contingencies
        if (results.stream().allMatch(r -> contingenciesNumber == contingenciesNumberGetter.applyAsInt(r))) {
            Map<String, String> lineStrings = new HashMap<>();
            lineStrings.put("Network", Constants.getPrettyNetworkName(results.getFirst().parameters().get("networkName")));
            lineStrings.put("Contingencies", String.valueOf(contingenciesNumber));
            int resultIndex = 0;
            for (Map.Entry<String, Double> scorePerColumn : lineScores.entrySet()) {
                lineStrings.put(
                    scorePerColumn.getKey(),
                    getFormattedScoreAndUnit(scorePerColumn.getValue(), results.get(resultIndex).scoreUnit(), scorePerContingency)
                );
                ++resultIndex;
            }
            return lineStrings;
        } else {
            throw new IllegalStateException("All results for a given network must have the same number of contingencies");
        }
    }

    @Override
    protected Map<String, Double> getLineScores(List<BenchmarkResult> results) {
        return Map.of(
            getPrettyColumnName(results.get(0)), results.get(0).score(),
            getPrettyColumnName(results.get(1)), results.get(1).score(),
            getPrettyColumnName(results.get(2)), results.get(2).score()
        );
    }
}
