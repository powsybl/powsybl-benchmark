/**
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.commons.serde.markdown;

import com.powsybl.benchmark.commons.Constants;
import com.powsybl.benchmark.commons.serde.BenchmarkResult;
import com.powsybl.benchmark.commons.state.LoadFlowParametersType;

import java.util.List;
import java.util.Map;
import java.util.function.DoubleUnaryOperator;
import java.util.function.ToIntFunction;

/**
 * @author Dissoubray Nathan {@literal <nathan.dissoubray at rte-france.com>}
 */
public class ContingenciesBenchmarkReportMarkdownSerializer extends AbstractByNetworkBenchmarkReportMarkdownSerializer {
    @Override
    protected String[] columnNames() {
        return new String[]{
            "Network",
            "Contingencies",
            "Basic parameters",
            "Standard parameters",
            "Standard parameters <br/>with reactive limits not used"
        };
    }

    @Override
    protected Map<String, String> getLine(List<BenchmarkResult> results) {
        //this is formatted like a double in the string, but it's an int, get the integer part with the split
        ToIntFunction<BenchmarkResult> contingenciesNumberGetter = r -> Integer.parseInt(r.parameters().get("numberOfContingencies").split("\\.")[0]);
        //all results have the same number of contingencies, just get the first
        int contingenciesNumber = contingenciesNumberGetter.applyAsInt(results.getFirst());
        DoubleUnaryOperator scorePerContingency = d -> d / contingenciesNumber;
        //check that all results for a given network have the same number of contingencies
        if (results.stream().allMatch(r -> contingenciesNumber == contingenciesNumberGetter.applyAsInt(r))) {
            return Map.of(
                "Network", Constants.getPrettyNetworkName(results.get(0).parameters().get("networkName")),
                "Contingencies", String.valueOf(contingenciesNumber),
                getPrettyColumnName(results.get(0)), getFormattedScoreAndUnit(results.get(0), scorePerContingency),
                getPrettyColumnName(results.get(1)), getFormattedScoreAndUnit(results.get(1), scorePerContingency),
                getPrettyColumnName(results.get(2)), getFormattedScoreAndUnit(results.get(2), scorePerContingency)
            );
        } else {
            throw new IllegalStateException("All results for a given network must have the same number of contingencies");
        }
    }

    private String getPrettyColumnName(BenchmarkResult benchmarkResult) {
        return switch (LoadFlowParametersType.valueOf(benchmarkResult.parameters().get("type"))) {
            case BASIC -> "Basic parameters";
            case STANDARD -> "Standard parameters";
            case STANDARD_REACTIVE_LIMITS_NOT_USED -> "Standard parameters <br/>with reactive limits not used";
        };
    }
}
