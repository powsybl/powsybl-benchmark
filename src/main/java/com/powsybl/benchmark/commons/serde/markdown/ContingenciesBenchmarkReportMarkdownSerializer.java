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

import java.util.List;
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
    protected String[] getLine(List<BenchmarkResult> results) {
        //this is formatted like a double in the string, but it's an int, get the integer part with the split
        ToIntFunction<BenchmarkResult> contingenciesNumberGetter = r -> Integer.parseInt(r.parameters().get("numberOfContingencies").split("\\.")[0]);
        int contingenciesNumber = contingenciesNumberGetter.applyAsInt(results.get(0));
        DoubleUnaryOperator scorePerContingency = d -> d / contingenciesNumber;
        //check that all results for a given network have the same number of contingencies
        if (results.stream().allMatch(r -> contingenciesNumber == contingenciesNumberGetter.applyAsInt(r))) {
            return new String[]{
                Constants.getPrettyNetworkName(results.get(0).parameters().get("networkName")),
                String.valueOf(contingenciesNumber),
                getFormattedScoreAndUnit(results.get(0), scorePerContingency),
                getFormattedScoreAndUnit(results.get(1), scorePerContingency),
                getFormattedScoreAndUnit(results.get(2), scorePerContingency)
            };
        } else {
            throw new IllegalStateException("All results for a given network must have the same number of contingencies");
        }
    }
}
