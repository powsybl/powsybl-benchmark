/**
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.commons.serde.markdown.loadflow;

import com.powsybl.benchmark.commons.Constants;
import com.powsybl.benchmark.commons.serde.BenchmarkResult;
import com.powsybl.benchmark.commons.serde.markdown.AbstractByNetworkBenchmarkReportMarkdownSerializer;
import com.powsybl.benchmark.commons.state.LoadFlowParametersType;

import java.util.*;

/**
 * @author Dissoubray Nathan {@literal <nathan.dissoubray at rte-france.com>}
 */
public class LoadFlowBenchmarkReportMarkdownSerializer extends AbstractByNetworkBenchmarkReportMarkdownSerializer {

    @Override
    protected String[] columnNames() {
        return new String[]{
            "Network",
            "Basic parameters",
            "Standard parameters",
            "Standard parameters <br/>with reactive limits not used"
        };

    }

    @Override
    protected Map<String, String> getLine(List<BenchmarkResult> resultsForNetwork) {
        return Map.of(
            "Network", Constants.getPrettyNetworkName(resultsForNetwork.get(0).parameters().get("networkName")),
            getPrettyColumnName(resultsForNetwork.get(0)), getFormattedScoreAndUnit(resultsForNetwork.get(0)),
            getPrettyColumnName(resultsForNetwork.get(1)), getFormattedScoreAndUnit(resultsForNetwork.get(1)),
            getPrettyColumnName(resultsForNetwork.get(2)), getFormattedScoreAndUnit(resultsForNetwork.get(2))
        );
    }

    private String getPrettyColumnName(BenchmarkResult benchmarkResult) {
        return switch (LoadFlowParametersType.valueOf(benchmarkResult.parameters().get("type"))) {
            case BASIC -> "Basic parameters";
            case STANDARD -> "Standard parameters";
            case STANDARD_REACTIVE_LIMITS_NOT_USED -> "Standard parameters <br/>with reactive limits not used";
        };
    }
}
