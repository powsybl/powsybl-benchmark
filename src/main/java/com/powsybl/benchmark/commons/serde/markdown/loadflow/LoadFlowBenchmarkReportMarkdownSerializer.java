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
    protected String[] getLine(List<BenchmarkResult> resultsForNetwork) {
        //TODO there is no guarantee that 0, 1 and 2 correspond to basic, standard and without reactive limits in that order, need to sort them first
        return new String[]{
            Constants.getPrettyNetworkName(resultsForNetwork.get(0).parameters().get("networkName")),
            getFormattedScoreAndUnit(resultsForNetwork.get(0)),
            getFormattedScoreAndUnit(resultsForNetwork.get(1)),
            getFormattedScoreAndUnit(resultsForNetwork.get(2))
        };
    }
}
