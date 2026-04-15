/*
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.sensitivity.state;

import com.powsybl.benchmark.commons.MatpowerUtil;
import com.powsybl.benchmark.loadflow.state.LoadFlowParametersType;
import com.powsybl.commons.datasource.ResourceDataSource;
import com.powsybl.commons.datasource.ResourceSet;
import com.powsybl.contingency.Contingency;
import com.powsybl.contingency.ContingencyContext;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.TwoSides;
import com.powsybl.openloadflow.sensi.OpenSensitivityAnalysisParameters;
import com.powsybl.sensitivity.SensitivityAnalysisParameters;
import com.powsybl.sensitivity.SensitivityAnalysisRunParameters;
import com.powsybl.sensitivity.SensitivityFactor;
import com.powsybl.sensitivity.SensitivityFunctionType;
import com.powsybl.sensitivity.SensitivityVariableType;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.util.List;
import java.util.Objects;

import static com.powsybl.benchmark.commons.Constants.REAL_GRID;
import static com.powsybl.benchmark.commons.Constants.RTE_1888;
import static com.powsybl.benchmark.commons.Constants.RTE_6515;

/**
 * @author Nicolas Rol {@literal <nicolas.rol at rte-france.com>}
 */
@State(Scope.Thread)
public class SensitivityAnalysisState {

    @Param({"1000"})
    private int contingenciesLimit;

    @Param({"10000"})
    private int factorsLimit;

    @Param({RTE_1888, RTE_6515, REAL_GRID})
    private String networkName;

    @Param
    private LoadFlowParametersType type;

    private Network network;

    private List<SensitivityFactor> factors;
    private SensitivityAnalysisRunParameters runParameters;

    @Setup(Level.Trial)
    public void doSetup() {
        network = loadNetwork(networkName);
        List<Contingency> contingencies = computeContingencies(network, contingenciesLimit);
        factors = computeSensitivityFactors(network, factorsLimit);
        SensitivityAnalysisParameters parameters = new SensitivityAnalysisParameters()
            .setLoadFlowParameters(type.getParameters());
        OpenSensitivityAnalysisParameters sensitivityAnalysisParametersExt = new OpenSensitivityAnalysisParameters();
        parameters.addExtension(OpenSensitivityAnalysisParameters.class, sensitivityAnalysisParametersExt);
        sensitivityAnalysisParametersExt.setThreadCount(1);
        runParameters = SensitivityAnalysisRunParameters.getDefault()
            .setParameters(parameters)
            .setContingencies(contingencies);
    }

    private static SensitivityFactor createBranchFlowPerInjectionIncrease(String functionId,
                                                                          String variableId,
                                                                          String contingencyId,
                                                                          TwoSides side) {
        SensitivityFunctionType sensitivityFunctionType = side.equals(TwoSides.ONE) ?
            SensitivityFunctionType.BRANCH_ACTIVE_POWER_1 :
            SensitivityFunctionType.BRANCH_ACTIVE_POWER_2;
        return new SensitivityFactor(sensitivityFunctionType, functionId,
            SensitivityVariableType.INJECTION_ACTIVE_POWER,
            variableId,
            false,
            Objects.isNull(contingencyId) ?
                ContingencyContext.all() :
                ContingencyContext.specificContingency(contingencyId));
    }

    private static List<Contingency> computeContingencies(Network network, int contingenciesLimit) {
        return network.getLineStream()
            .limit(contingenciesLimit)
            .map(line -> Contingency.line(line.getId()))
            .toList();
    }

    private static List<SensitivityFactor> computeSensitivityFactors(Network network, int factorsLimit) {
        return network.getGeneratorStream()
            .flatMap(injection -> network.getBranchStream()
                .map(branch -> createBranchFlowPerInjectionIncrease(branch.getId(), injection.getId(), null, TwoSides.ONE)))
            .limit(factorsLimit)
            .toList();
    }

    public Network getNetwork() {
        return network;
    }

    public List<SensitivityFactor> getFactors() {
        return factors;
    }

    public SensitivityAnalysisRunParameters getRunParameters() {
        return runParameters;
    }

    private Network loadNetwork(String networkName) {
        return switch (networkName) {
            case REAL_GRID -> Network.read(new ResourceDataSource(networkName, new ResourceSet("/data", networkName + ".zip")));
            case RTE_1888, RTE_6515 -> MatpowerUtil.importMat(networkName);
            default -> throw new IllegalArgumentException("Unknown network: " + networkName);
        };
    }
}
