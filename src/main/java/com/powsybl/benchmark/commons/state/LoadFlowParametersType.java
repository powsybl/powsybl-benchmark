/*
 * Copyright (c) 2022-2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.commons.state;

import com.powsybl.loadflow.LoadFlowParameters;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public enum LoadFlowParametersType {
    BASIC(new LoadFlowParameters()
            .setVoltageInitMode(LoadFlowParameters.VoltageInitMode.UNIFORM_VALUES)
            .setDistributedSlack(false)
            .setUseReactiveLimits(false)
            .setPhaseShifterRegulationOn(false)
            .setTransformerVoltageControlOn(false)
            .setComponentMode(LoadFlowParameters.ComponentMode.MAIN_CONNECTED)),
    STANDARD(new LoadFlowParameters()
            .setVoltageInitMode(LoadFlowParameters.VoltageInitMode.UNIFORM_VALUES)
            .setDistributedSlack(true)
            .setUseReactiveLimits(true)
            .setPhaseShifterRegulationOn(false)
            .setTransformerVoltageControlOn(false)
            .setComponentMode(LoadFlowParameters.ComponentMode.MAIN_CONNECTED)),
    STANDARD_REACTIVE_LIMITS_NOT_USED(new LoadFlowParameters()
            .setVoltageInitMode(LoadFlowParameters.VoltageInitMode.UNIFORM_VALUES)
            .setDistributedSlack(true)
            .setUseReactiveLimits(false)
            .setPhaseShifterRegulationOn(false)
            .setTransformerVoltageControlOn(false)
            .setComponentMode(LoadFlowParameters.ComponentMode.MAIN_CONNECTED));

    private final LoadFlowParameters parameters;

    LoadFlowParametersType(LoadFlowParameters parameters) {
        this.parameters = parameters;
    }

    public LoadFlowParameters getParameters() {
        return parameters;
    }
}
