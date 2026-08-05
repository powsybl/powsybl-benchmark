/**
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.commons.runserde.markdown;

import com.powsybl.benchmark.commons.Constants;
import com.powsybl.benchmark.commons.runserde.BenchmarkReport;
import com.powsybl.benchmark.commons.runserde.BenchmarkResult;

import java.util.*;

/**
 * @author Dissoubray Nathan {@literal <nathan.dissoubray at rte-france.com>}
 */
public class LoadFlowBenchmarkReportMarkdownSerDe extends AbstractBenchmarkReportMarkdownSerDe {

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
    protected String[][] valuesByLine(BenchmarkReport report) {
        List<List<BenchmarkResult>> resultsByNetwork = getResultsByNetwork(report);
        String[][] valuesByLine = new String[resultsByNetwork.size()][columnNames().length];
        for (int i = 0; i < resultsByNetwork.size(); ++i) {
            valuesByLine[i] = getLine(resultsByNetwork.get(i));
        }
        return valuesByLine;
    }

    private String[] getLine(List<BenchmarkResult> resultsForNetwork) {
        return new String[]{
            Constants.getPrettyNetworkName(resultsForNetwork.get(0).parameters().get("networkName")),
            getFormattedScore(resultsForNetwork.get(0)),
            getFormattedScore(resultsForNetwork.get(1)),
            getFormattedScore(resultsForNetwork.get(2))
        };
    }

    private List<List<BenchmarkResult>> getResultsByNetwork(BenchmarkReport report) {
        LinkedHashMap<String, List<BenchmarkResult>> byNetwork = new LinkedHashMap<>();
        for (BenchmarkResult result : report.results()) {
            String networkName = result.parameters().get("networkName");
            byNetwork.computeIfAbsent(networkName, k -> new ArrayList<>()).add(result);
        }
        return new ArrayList<>(byNetwork.values());
    }

}
