/*
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.commons.state;

import com.powsybl.contingency.Contingency;
import com.powsybl.iidm.network.Network;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author Nicolas Rol {@literal <nicolas.rol at rte-france.com>}
 */
@State(Scope.Thread)
public abstract class AbstractAnalysisState extends AbstractLoadFlowState {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractAnalysisState.class);

    @Param({"1000"})
    private int contingenciesLimit;

    protected List<Contingency> contingencies;

    @Override
    @Setup(Level.Trial)
    public void doSetup() {
        super.doSetup();
        contingencies = computeContingencies(network, contingenciesLimit);
        LOGGER.warn("Contingencies : {}", contingencies.size());
    }

    private static List<Contingency> computeContingencies(Network network, int contingenciesLimit) {
        return network.getLineStream()
            .limit(contingenciesLimit)
            .map(line -> Contingency.line(line.getId()))
            .toList();
    }

    public List<Contingency> getContingencies() {
        return contingencies;
    }
}
