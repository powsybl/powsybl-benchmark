/*
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.sensitivity.state;

import com.powsybl.benchmark.commons.state.AbstractAnalysisState;
import com.powsybl.benchmark.commons.state.LoadFlowParametersType;
import com.powsybl.contingency.ContingencyContext;
import com.powsybl.iidm.network.Network;
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

import static com.powsybl.benchmark.commons.Constants.IEEE_118;
import static com.powsybl.benchmark.commons.Constants.IEEE_14;
import static com.powsybl.benchmark.commons.Constants.IEEE_300;
import static com.powsybl.benchmark.commons.Constants.REAL_GRID;
import static com.powsybl.benchmark.commons.Constants.RTE_1888;
import static com.powsybl.benchmark.commons.Constants.RTE_6515;

/**
 * @author Nicolas Rol {@literal <nicolas.rol at rte-france.com>}
 */
@State(Scope.Thread)
public class SensitivityAnalysisState extends AbstractAnalysisState {

    @Param({"10000"})
    private int factorsLimit;

    @Param({IEEE_14, IEEE_118, IEEE_300, RTE_1888, RTE_6515, REAL_GRID})
    private String networkName;

    @Param
    private LoadFlowParametersType type;

    private List<SensitivityFactor> factors;
    private SensitivityAnalysisRunParameters runParameters;

    @Override
    @Setup(Level.Trial)
    public void doSetup() {
        super.doSetup();
        factors = computeSensitivityFactors(network, factorsLimit);
        SensitivityAnalysisParameters parameters = new SensitivityAnalysisParameters()
            .setLoadFlowParameters(getParameters());
        OpenSensitivityAnalysisParameters sensitivityAnalysisParametersExt = new OpenSensitivityAnalysisParameters();
        parameters.addExtension(OpenSensitivityAnalysisParameters.class, sensitivityAnalysisParametersExt);
        sensitivityAnalysisParametersExt.setThreadCount(1);
        runParameters = SensitivityAnalysisRunParameters.getDefault()
            .setParameters(parameters)
            .setContingencies(contingencies);
    }

    @Override
    protected String getNetworkName() {
        return networkName;
    }

    @Override
    protected void setLoadFlowParameters() {
        parameters = type.getParameters();
    }

    private static List<SensitivityFactor> computeSensitivityFactors(Network network, int factorsLimit) {
        return network.getGeneratorStream()
            .flatMap(injection -> network.getBranchStream()
                .map(branch -> createBranchFlowPerInjectionIncrease(branch.getId(), injection.getId())))
            .limit(factorsLimit)
            .toList();
    }

    private static SensitivityFactor createBranchFlowPerInjectionIncrease(String functionId,
                                                                          String variableId) {
        return new SensitivityFactor(SensitivityFunctionType.BRANCH_ACTIVE_POWER_1,
            functionId,
            SensitivityVariableType.INJECTION_ACTIVE_POWER,
            variableId,
            false,
            ContingencyContext.all());
    }

    public List<SensitivityFactor> getFactors() {
        return factors;
    }

    public SensitivityAnalysisRunParameters getRunParameters() {
        return runParameters;
    }
}
