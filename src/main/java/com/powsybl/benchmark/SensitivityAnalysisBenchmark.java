/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.benchmark;

import com.google.common.base.Stopwatch;
import com.powsybl.computation.local.LocalComputationManager;
import com.powsybl.contingency.Contingency;
import com.powsybl.iidm.network.Branch;
import com.powsybl.iidm.network.Injection;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.VariantManagerConstants;
import com.powsybl.sensitivity.*;
import com.powsybl.sensitivity.factors.BranchFlowPerInjectionIncrease;
import com.powsybl.sensitivity.factors.functions.BranchFlow;
import com.powsybl.sensitivity.factors.variables.InjectionIncrease;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
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
        SensitivityFactorsProvider factorsProvider = n -> createFactorMatrix(network.getGeneratorStream().collect(Collectors.toList()),
                n.getLineStream().collect(Collectors.toList())).subList(0, factorsLimit);

        Stopwatch stopwatch = Stopwatch.createStarted();
        SensitivityAnalysisResult result = SensitivityAnalysis.find(provider)
                                                              .run(network,
                                                                VariantManagerConstants.INITIAL_VARIANT_ID,
                                                                factorsProvider,
                                                                contingencies,
                                                                parameters,
                                                                LocalComputationManager.getDefault());
        benchmarkResults.add(new SensitivityAnalysisBenchmark.BenchmarkResult(network.getId(), loadFlowParametersType, contingencyLimit, factorsLimit, stopwatch.elapsed(TimeUnit.MILLISECONDS)));
        return result;
    }

    protected static <T extends Injection<T>> List<SensitivityFactor> createFactorMatrix(List<T> injections, List<Branch> branches) {
        Objects.requireNonNull(injections);
        Objects.requireNonNull(branches);
        List<SensitivityFactor> sensiFactorsList = injections.stream().flatMap(injection -> branches.stream().map(branch -> new BranchFlowPerInjectionIncrease(createBranchFlow(branch),
                createInjectionIncrease(injection)))).collect(Collectors.toList());
        return sensiFactorsList;
    }

    protected static BranchFlow createBranchFlow(Branch branch) {
        return new BranchFlow(branch.getId(), branch.getNameOrId(), branch.getId());
    }

    protected static <T extends Injection<T>> InjectionIncrease createInjectionIncrease(T injection) {
        return new InjectionIncrease(injection.getId(), injection.getId(), injection.getId());
    }

    public static void main(String[] args) {
        List<BenchmarkResult> results = new ArrayList<>(4);
        Network case1888rte = MatpowerUtil.importMat("case1888rte");
        Network case6515rte = MatpowerUtil.importMat("case6515rte");

        for (LoadFlowParametersType loadFlowParametersType : LoadFlowParametersType.values()) {
            run("OpenSensitivityAnalysis", case1888rte, loadFlowParametersType, 1000, 10000, results);
            run("OpenSensitivityAnalysis", case6515rte, loadFlowParametersType, 1000, 10000, results);
        }

        for (SensitivityAnalysisBenchmark.BenchmarkResult result : results) {
            LOGGER.info("Sensitivity analysis on network '{}' with {} contingencies and {} factors and load flow parameters {} done in {} ms: {} ms / contingency , {} factors / second",
                    result.getNetworkId(), result.getContingencyCount(), result.getFactorCount(), result.getLoadFlowParametersType(), result.getMilliSeconds(),
                    result.getMilliSeconds() / result.getContingencyCount(), result.getContingencyCount() * result.getFactorCount() / (result.getMilliSeconds() / 1000));
        }
    }
}
