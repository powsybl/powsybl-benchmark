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

import java.util.List;

/**
 * @author Dissoubray Nathan {@literal <nathan.dissoubray at rte-france.com>}
 */
public class MultiThreadSecurityAnalysisBenchmarkReportMarkdownSerializer extends AbstractByNetworkBenchmarkReportMarkdownSerializer {

    @Override
    protected String[] columnNames() {
        return MultiThreadUtil.columnNames("Network");
    }

    @Override
    protected String[] getLine(List<BenchmarkResult> resultsForNetwork) {
        double timeOneThread = MultiThreadUtil.sortColumnsAndGetOneThreadTime(resultsForNetwork);
        return new String[] {
            Constants.getPrettyNetworkName(resultsForNetwork.getFirst().parameters().get("networkName")),
            getFormattedScore(resultsForNetwork.get(0)) + " (1.00)",
            MultiThreadUtil.getFormatedScoreAndEffectiveness(resultsForNetwork.get(1), timeOneThread, 2),
            MultiThreadUtil.getFormatedScoreAndEffectiveness(resultsForNetwork.get(2), timeOneThread, 4),
            MultiThreadUtil.getFormatedScoreAndEffectiveness(resultsForNetwork.get(3), timeOneThread, 8)
        };
    }

}
