/*
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.security.state;

import com.powsybl.benchmark.commons.MatpowerUtil;
import com.powsybl.contingency.Contingency;
import com.powsybl.iidm.network.Network;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.util.List;

import static com.powsybl.benchmark.commons.Constants.RTE_6515;

/**
 * @author Nicolas Rol {@literal <nicolas.rol at rte-france.com>}
 */
@State(Scope.Thread)
public class SecurityAnalysisMultiThreadsParametersState {

    private static final int CONTINGENCY_LIMIT = 500;
    private static final String NETWORK_NAME = RTE_6515;

    @Param({"1", "2", "4", "8"})
    private int threadCount;

    @Param({"OpenLoadFlow"})
    private String provider;

    private Network network;
    private List<Contingency> contingencies;

    @Setup(Level.Trial)
    public void doSetup() {
        network = MatpowerUtil.importMat(NETWORK_NAME);
        contingencies = network.getLineStream()
            .limit(CONTINGENCY_LIMIT)
            .map(line -> Contingency.line(line.getId()))
            .toList();
    }

    public int getThreadCount() {
        return threadCount;
    }

    public String getProvider() {
        return provider;
    }

    public Network getNetwork() {
        return network;
    }

    public List<Contingency> getContingencies() {
        return contingencies;
    }
}
