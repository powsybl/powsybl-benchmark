/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.benchmark;

import com.powsybl.loadflow.LoadFlowParameters;
import org.openjdk.jmh.annotations.*;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
@State(Scope.Thread)
public class LoadFlowParametersState {

    private LoadFlowParameters basicParameters;

    private LoadFlowParameters standardParameters;

    public enum Type {
        BASIC,
        STANDARD
    }

    @Param
    private Type type;

    @Setup(Level.Trial)
    public void doSetup() {
        basicParameters = new LoadFlowParameters()
                .setVoltageInitMode(LoadFlowParameters.VoltageInitMode.UNIFORM_VALUES)
                .setDistributedSlack(false)
                .setNoGeneratorReactiveLimits(true)
                .setPhaseShifterRegulationOn(false)
                .setTransformerVoltageControlOn(false)
                .setConnectedComponentMode(LoadFlowParameters.ConnectedComponentMode.MAIN);
        standardParameters = new LoadFlowParameters()
                .setVoltageInitMode(LoadFlowParameters.VoltageInitMode.UNIFORM_VALUES)
                .setDistributedSlack(true)
                .setNoGeneratorReactiveLimits(false)
                .setPhaseShifterRegulationOn(false)
                .setTransformerVoltageControlOn(false)
                .setConnectedComponentMode(LoadFlowParameters.ConnectedComponentMode.MAIN);
    }

    public LoadFlowParameters getParameters() {
        switch (type) {
            case BASIC:
                return basicParameters;
            case STANDARD:
                return standardParameters;
            default:
                throw new IllegalStateException("Unknown parameter type: " + type);
        }
    }
}
