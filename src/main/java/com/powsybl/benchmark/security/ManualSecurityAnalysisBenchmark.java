/*
 * Copyright (c) 2022-2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.security;

import com.google.common.base.Stopwatch;
import com.powsybl.benchmark.loadflow.state.LoadFlowParametersType;
import com.powsybl.benchmark.commons.MatpowerUtil;
import com.powsybl.contingency.Contingency;
import com.powsybl.iidm.network.Network;
import com.powsybl.security.SecurityAnalysis;
import com.powsybl.security.SecurityAnalysisParameters;
import com.powsybl.security.SecurityAnalysisResult;
import com.powsybl.security.SecurityAnalysisRunParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.powsybl.benchmark.commons.Constants.RTE_1888;
import static com.powsybl.benchmark.commons.Constants.RTE_6515;

/**
 * @deprecated since 2025.4.0
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
@Deprecated(forRemoval = true, since = "2025.4.0")
public final class ManualSecurityAnalysisBenchmark {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManualSecurityAnalysisBenchmark.class);

    private ManualSecurityAnalysisBenchmark() {
    }

    record BenchmarkResult(String networkId,
                           LoadFlowParametersType loadFlowParametersType,
                           int contingencyCount,
                           long milliSeconds) {
    }

    private static SecurityAnalysisResult run(String provider, Network network, LoadFlowParametersType loadFlowParametersType,
                                              int contingencyLimit, List<BenchmarkResult> benchmarkResults) {
        List<Contingency> contingencies = network.getLineStream()
                .limit(contingencyLimit)
                .map(line -> Contingency.line(line.getId()))
                .toList();
        SecurityAnalysisParameters parameters = new SecurityAnalysisParameters()
                .setLoadFlowParameters(loadFlowParametersType.getParameters());
        Stopwatch stopwatch = Stopwatch.createStarted();
        var runParameters = SecurityAnalysisRunParameters.getDefault().setSecurityAnalysisParameters(parameters);
        SecurityAnalysisResult result = SecurityAnalysis.find(provider)
                .run(network, contingencies, runParameters)
                .getResult();
        benchmarkResults.add(new BenchmarkResult(network.getId(), loadFlowParametersType, contingencyLimit, stopwatch.elapsed(TimeUnit.MILLISECONDS)));
        return result;
    }

    public static void main(String[] args) {
        List<BenchmarkResult> results = new ArrayList<>(4);

        Network case1888rte = MatpowerUtil.importMat(RTE_1888);
        Network case6515rte = MatpowerUtil.importMat(RTE_6515);
        for (LoadFlowParametersType loadFlowParametersType : LoadFlowParametersType.values()) {
            run("OpenLoadFlow", case1888rte, loadFlowParametersType, 1000, results);
            run("OpenLoadFlow", case6515rte, loadFlowParametersType, 1000, results);
        }

        for (BenchmarkResult result : results) {
            LOGGER.info("Security analysis on network '{}' with {} contingencies and load flow parameters {} done in {} ms: {} ms / contingency",
                    result.networkId(), result.contingencyCount(), result.loadFlowParametersType(), result.milliSeconds(),
                    result.milliSeconds() / result.contingencyCount());
        }
    }
}
