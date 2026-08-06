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

import java.util.*;

/**
 * @author Dissoubray Nathan {@literal <nathan.dissoubray at rte-france.com>}
 */
public class LoadFlowBenchmarkReportMarkdownSerDe extends AbstractByNetworkBenchmarkReportMarkdownSerDe {

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
    protected String[] getLine(List<BenchmarkResult> resultsForNetwork) {
        return new String[]{
            Constants.getPrettyNetworkName(resultsForNetwork.get(0).parameters().get("networkName")),
            getFormattedScore(resultsForNetwork.get(0)),
            getFormattedScore(resultsForNetwork.get(1)),
            getFormattedScore(resultsForNetwork.get(2))
        };
    }
}
