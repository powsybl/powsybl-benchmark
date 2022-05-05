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
import com.powsybl.contingency.ContingencyContext;
import com.powsybl.iidm.import_.Importers;
import com.powsybl.iidm.network.Branch;
import com.powsybl.iidm.network.Injection;
import com.powsybl.iidm.network.Network;
import com.powsybl.sensitivity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public final class SensitivityAnalysisBenchmark {

    private static final Logger LOGGER = LoggerFactory.getLogger(SensitivityAnalysisBenchmark.class);

    private SensitivityAnalysisBenchmark() {
    }

    static class BenchmarkResult {

        private final String networkId;

        private final LoadFlowParametersType loadFlowParametersType;

        private final int contingencyCount;

        private final int factorCount;

        private final long milliSeconds;

        BenchmarkResult(String networkId, LoadFlowParametersType loadFlowParametersType, int contingencyCount, int factorCount, long milliSeconds) {
            this.networkId = networkId;
            this.loadFlowParametersType = loadFlowParametersType;
            this.contingencyCount = contingencyCount;
            this.factorCount = factorCount;
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

        int getFactorCount() {
            return factorCount;
        }

        long getMilliSeconds() {
            return milliSeconds;
        }
    }

    private static SensitivityAnalysisResult run(String provider, Network network, LoadFlowParametersType loadFlowParametersType,
                                                 int contingencyLimit, int factorsLimit, List<BenchmarkResult> benchmarkResults) {
        List<Contingency> contingencies = network.getLineStream()
                                                 .limit(contingencyLimit)
                                                 .map(line -> Contingency.line(line.getId()))
                                                 .collect(Collectors.toList());

        SensitivityAnalysisParameters parameters = new SensitivityAnalysisParameters()
                .setLoadFlowParameters(loadFlowParametersType.getParameters());
        List<SensitivityFactor> factors = createFactorMatrix(network.getGeneratorStream().collect(Collectors.toList()),
                network.getBranchStream().collect(Collectors.toList()), null, Branch.Side.ONE).subList(0, factorsLimit);

        Stopwatch stopwatch = Stopwatch.createStarted();
        SensitivityAnalysisResult result = SensitivityAnalysis.find(provider)
                                                              .run(network,
                                                                      factors,
                                                                      contingencies,
                                                                      parameters);
        benchmarkResults.add(new SensitivityAnalysisBenchmark.BenchmarkResult(network.getId(), loadFlowParametersType, contingencyLimit, factorsLimit, stopwatch.elapsed(TimeUnit.MILLISECONDS)));
        return result;
    }

    protected static <T extends Injection<T>> List<SensitivityFactor> createFactorMatrix(List<T> injections, List<Branch> branches, String contingencyId, Branch.Side side) {
        Objects.requireNonNull(injections);
        Objects.requireNonNull(branches);
        return injections.stream().flatMap(injection -> branches.stream().map(branch -> createBranchFlowPerInjectionIncrease(branch.getId(), injection.getId(), contingencyId, side))).collect(Collectors.toList());
    }

    protected static SensitivityFactor createBranchFlowPerInjectionIncrease(String functionId, String variableId, String contingencyId, Branch.Side side) {
        SensitivityFunctionType ftype = side.equals(Branch.Side.ONE) ? SensitivityFunctionType.BRANCH_ACTIVE_POWER : SensitivityFunctionType.BRANCH_ACTIVE_POWER_2;
        return new SensitivityFactor(ftype, functionId, SensitivityVariableType.INJECTION_ACTIVE_POWER, variableId, false, Objects.isNull(contingencyId) ? ContingencyContext.all() : ContingencyContext.specificContingency(contingencyId));
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

        ArrayList<Integer> contingenciesCounts = new ArrayList<>(
                Arrays.asList(100, 500, 1000, 4000, 6000));

        ArrayList<Integer> factorCount = new ArrayList<>(
                Arrays.asList(100, 500, 1000, 5000, 10000));

        String providerName = "OpenSensitivityAnalysis";

        for(Integer contingencies: contingenciesCounts) {
            for(Integer factors: factorCount) {
                run(providerName, case1888rte, LoadFlowParametersType.BASIC, contingencies, factors, results);
                run(providerName, case1888rte, LoadFlowParametersType.STANDARD, contingencies, factors, results);
                run(providerName, case6515rte, LoadFlowParametersType.BASIC, contingencies, factors, results);
                run(providerName, case6515rte, LoadFlowParametersType.STANDARD, contingencies, factors, results);
                run(providerName, case6051realgrid, LoadFlowParametersType.STANDARDNOGENREACTIVELIMIT, contingencies, factors, results);
            }
        }

        for (SensitivityAnalysisBenchmark.BenchmarkResult result : results) {
            LOGGER.info("Sensitivity analysis on network '{}' with {} contingencies and {} factors and load flow parameters {} done in {} ms: {} ms / contingency , {} factors / second",
                    result.getNetworkId(), result.getContingencyCount(), result.getFactorCount(), result.getLoadFlowParametersType(), result.getMilliSeconds(),
                    result.getMilliSeconds() / result.getContingencyCount(), result.getContingencyCount() * result.getFactorCount() / (result.getMilliSeconds() / 1000));
        }
    }
}
