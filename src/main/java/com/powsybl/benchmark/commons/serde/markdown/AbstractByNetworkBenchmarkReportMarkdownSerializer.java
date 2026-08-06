/**
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.commons.serde.markdown;

import com.powsybl.benchmark.commons.serde.BenchmarkReport;
import com.powsybl.benchmark.commons.serde.BenchmarkResult;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author Dissoubray Nathan {@literal <nathan.dissoubray at rte-france.com>}
 */
public abstract class AbstractByNetworkBenchmarkReportMarkdownSerializer extends AbstractBenchmarkReportMarkdownSerializer {

    @Override
    protected String[][] valuesByLine(BenchmarkReport report) {
        List<List<BenchmarkResult>> resultsByNetwork = getResultsByNetwork(report);
        String[][] valuesByLine = new String[resultsByNetwork.size()][columnNames().length];
        for (int i = 0; i < resultsByNetwork.size(); ++i) {
            valuesByLine[i] = getLine(resultsByNetwork.get(i));
        }
        return valuesByLine;
    }

    protected abstract String[] getLine(List<BenchmarkResult> resultsForNetwork);

    private List<List<BenchmarkResult>> getResultsByNetwork(BenchmarkReport report) {
        LinkedHashMap<String, List<BenchmarkResult>> byNetwork = new LinkedHashMap<>();
        for (BenchmarkResult result : report.results()) {
            String networkName = result.parameters().get("networkName");
            byNetwork.computeIfAbsent(networkName, k -> new ArrayList<>()).add(result);
        }
        return new ArrayList<>(byNetwork.values());
    }
}
