/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.benchmark;

import com.google.common.base.Stopwatch;
import com.powsybl.commons.datasource.ResourceDataSource;
import com.powsybl.commons.datasource.ResourceSet;
import com.powsybl.contingency.Contingency;
import com.powsybl.iidm.import_.Importers;
import com.powsybl.iidm.network.Network;
import com.powsybl.security.SecurityAnalysis;
import com.powsybl.security.SecurityAnalysisParameters;
import com.powsybl.security.SecurityAnalysisResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public final class SecurityAnalysisBenchmark {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityAnalysisBenchmark.class);

    private SecurityAnalysisBenchmark() {
    }

    static class BenchmarkResult {

        private final String networkId;

        private final LoadFlowParametersType loadFlowParametersType;

        private final int contingencyCount;

        private final long milliSeconds;

        BenchmarkResult(String networkId, LoadFlowParametersType loadFlowParametersType, int contingencyCount, long milliSeconds) {
            this.networkId = networkId;
            this.loadFlowParametersType = loadFlowParametersType;
            this.contingencyCount = contingencyCount;
            this.milliSeconds = milliSeconds;
        }

        String getNetworkId() {
            return networkId;
        }

        LoadFlowParametersType getLoadFlowParametersType() {
            return loadFlowParametersType;
        }

        int getContingencyCount() {
            return contingencyCount;
        }

        long getMilliSeconds() {
            return milliSeconds;
        }
    }

    private static SecurityAnalysisResult run(String provider, Network network, LoadFlowParametersType loadFlowParametersType,
                                              int contingencyLimit, List<BenchmarkResult> benchmarkResults) {
        List<Contingency> contingencies = network.getLineStream()
                .limit(contingencyLimit)
                .map(line -> Contingency.line(line.getId()))
                .collect(Collectors.toList());
        SecurityAnalysisParameters parameters = new SecurityAnalysisParameters()
                .setLoadFlowParameters(loadFlowParametersType.getParameters());
        Stopwatch stopwatch = Stopwatch.createStarted();
        SecurityAnalysisResult result = SecurityAnalysis.find(provider)
                .run(network, contingencies, parameters)
                .getResult();
        benchmarkResults.add(new BenchmarkResult(network.getId(), loadFlowParametersType, contingencyLimit, stopwatch.elapsed(TimeUnit.MILLISECONDS)));
        return result;
    }

    public static void main(String[] args) {
        List<BenchmarkResult> results = new ArrayList<>(4);

        Network case1888rte = MatpowerUtil.importMat("case1888rte");
        Network case6515rte = MatpowerUtil.importMat("case6515rte");
        Network case6051realgrid = Importers.importData("CGMES",
                                                        new ResourceDataSource("CGMES_v2.4.15_RealGridTestConfiguration",
                                                                               new ResourceSet("/data/CGMES_RealGrid", "CGMES_v2.4.15_RealGridTestConfiguration_EQ_V2.xml",
                                                                                                                       "CGMES_v2.4.15_RealGridTestConfiguration_SSH_V2.xml",
                                                                                                                       "CGMES_v2.4.15_RealGridTestConfiguration_SV_V2.xml",
                                                                                                                       "CGMES_v2.4.15_RealGridTestConfiguration_TP_V2.xml")),
                                                        null);

        for (LoadFlowParametersType loadFlowParametersType : LoadFlowParametersType.values()) {
            run("OpenSecurityAnalysis", case1888rte, loadFlowParametersType, 1000, results);
            run("OpenSecurityAnalysis", case6515rte, loadFlowParametersType, 1000, results);
        }

        run("OpenSecurityAnalysis", case6051realgrid, LoadFlowParametersType.BASIC, 1000, results);

        for (BenchmarkResult result : results) {
            LOGGER.info("Security analysis on network '{}' with {} contingencies and load flow parameters {} done in {} ms: {} ms / contingency",
                    result.getNetworkId(), result.getContingencyCount(), result.getLoadFlowParametersType(), result.getMilliSeconds(),
                    result.getMilliSeconds() / result.getContingencyCount());
        }
    }
}
