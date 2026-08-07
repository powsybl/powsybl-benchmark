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
import java.util.function.Function;

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
        Function<BenchmarkResult, String> contingenciesNumberGetter = r -> r.parameters().get("contingencies");
        String contingenciesNumber = contingenciesNumberGetter.apply(results.get(0));
        //check that all results for a given network have the same number of contingencies
        if (results.stream().allMatch(r -> contingenciesNumber.equals(contingenciesNumberGetter.apply(r)))) {
            return new String[]{
                Constants.getPrettyNetworkName(results.get(0).parameters().get("networkName")),
                results.get(0).parameters().get("contingencies"),
                getFormattedScore(results.get(0)),
                getFormattedScore(results.get(1)),
                getFormattedScore(results.get(2))
            };
        } else {
            throw new IllegalStateException("All results for a given network must have the same number of contingencies");
        }
    }
}
