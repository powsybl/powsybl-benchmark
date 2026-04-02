/*
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark;

import com.powsybl.benchmark.state.AbstractMatpowerNetworkState;
import com.powsybl.benchmark.state.LoadFlowParametersState;
import com.powsybl.benchmark.state.LoadFlowProviderState;
import com.powsybl.benchmark.state.Rte1888NetworkState;
import com.powsybl.benchmark.state.Rte6515NetworkState;
import com.powsybl.contingency.Contingency;
import com.powsybl.iidm.network.Network;
import com.powsybl.security.SecurityAnalysisResult;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Warmup;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Nicolas Rol {@literal <nicolas.rol at rte-france.com>}
 */
@Warmup(iterations = 3, time = 30, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 30, timeUnit = TimeUnit.SECONDS)
public class MonoThreadSecurityAnalysisBenchmark extends AbstractSecurityAnalysisBenchmark {

    private static final int CONTINGENCY_LIMIT = 1000;

    @Benchmark
    @Fork(FORKS)
    public SecurityAnalysisResult rte1888MonoThread(LoadFlowProviderState providerState,
                                                    Rte1888NetworkState networkState,
                                                    LoadFlowParametersState loadFlowParametersState) {
        return runMonoThread(providerState, networkState, loadFlowParametersState);
    }

    @Benchmark
    @Fork(FORKS)
    public SecurityAnalysisResult rte6515MonoThread(LoadFlowProviderState providerState,
                                                    Rte6515NetworkState networkState,
                                                    LoadFlowParametersState loadFlowParametersState) {
        return runMonoThread(providerState, networkState, loadFlowParametersState);
    }

    private static SecurityAnalysisResult runMonoThread(LoadFlowProviderState providerState,
                                                        AbstractMatpowerNetworkState networkState,
                                                        LoadFlowParametersState loadFlowParametersState) {
        Network network = networkState.getNetwork();
        List<Contingency> contingencies = network.getLineStream()
            .limit(CONTINGENCY_LIMIT)
            .map(line -> Contingency.line(line.getId()))
            .toList();

        return run(providerState.getProvider(),
            network,
            1,
            loadFlowParametersState.getType().getParameters(),
            contingencies);
    }
}
