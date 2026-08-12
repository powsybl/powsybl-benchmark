/**
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.commons.serde.markdown.security;

import com.powsybl.benchmark.commons.Constants;
import com.powsybl.benchmark.commons.serde.BenchmarkResult;
import com.powsybl.benchmark.commons.serde.markdown.AbstractByNetworkBenchmarkReportMarkdownSerializer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Dissoubray Nathan {@literal <nathan.dissoubray at rte-france.com>}
 */
public class MultiThreadSecurityAnalysisBenchmarkReportMarkdownSerializer extends AbstractByNetworkBenchmarkReportMarkdownSerializer {

    @Override
    protected String[] columnNames() {
        return MultiThreadUtil.columnNames("Network");
    }

    @Override
    protected Map<String, String> getLine(List<BenchmarkResult> resultsForNetwork) {
        Map<Integer, BenchmarkResult> timePerThread = resultsForNetwork.stream()
            .collect(Collectors.toMap(MultiThreadUtil::getThreadCount, Function.identity()));
        BenchmarkResult oneThread = timePerThread.get(1);
        if (oneThread == null) {
            throw new NoSuchElementException("There is no result for 1 thread");
        }
        double timeOneThread = oneThread.score();
        Map<String, String> line = new HashMap<>();
        line.put("Network", Constants.getPrettyNetworkName(resultsForNetwork.getFirst().parameters().get("networkName")));
        for (Map.Entry<Integer, BenchmarkResult> threadEntry : timePerThread.entrySet()) {
            line.put(
                MultiThreadUtil.getPrettyColumnName(threadEntry.getKey()),
                MultiThreadUtil.getFormattedScoreAndEffectiveness(threadEntry, timeOneThread)
            );
        }
        return line;
    }
}
