/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.benchmark;

import com.powsybl.loadflow.LoadFlowParameters;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
@State(Scope.Thread)
public class LoadFlowParametersState {

    private LoadFlowParameters parameters;

    @Setup(Level.Trial)
    public void doSetup() {
        parameters = new LoadFlowParameters()
                .setVoltageInitMode(LoadFlowParameters.VoltageInitMode.UNIFORM_VALUES)
                .setDistributedSlack(false)
                .setNoGeneratorReactiveLimits(true)
                .setPhaseShifterRegulationOn(false)
                .setTransformerVoltageControlOn(false)
                .setConnectedComponentMode(LoadFlowParameters.ConnectedComponentMode.MAIN);
    }

    public LoadFlowParameters getParameters() {
        return parameters;
    }
}
