/*
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.security.state;

import com.powsybl.benchmark.commons.state.LoadFlowParametersType;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import static com.powsybl.benchmark.commons.Constants.RTE_6515;

/**
 * @author Nicolas Rol {@literal <nicolas.rol at rte-france.com>}
 */
@State(Scope.Thread)
public class MultiThreadsSecurityAnalysisState extends AbstractSecurityAnalysisState {

    @Param({RTE_6515})
    private String networkName;

    @Param({"1", "2", "4", "8"})
    private int threadCount;

    @Override
    protected String getNetworkName() {
        return networkName;
    }

    @Override
    public int getThreadCount() {
        return threadCount;
    }

    @Override
    protected void setLoadFlowParameters() {
        parameters = LoadFlowParametersType.STANDARD.getParameters();
    }
}
