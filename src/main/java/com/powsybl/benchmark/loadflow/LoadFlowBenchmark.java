/*
 * Copyright (c) 2022-2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.loadflow;

import com.powsybl.benchmark.serialization.state.IeeeNetworkState;
import com.powsybl.benchmark.loadflow.state.LoadFlowParametersState;
import com.powsybl.benchmark.loadflow.state.LoadFlowProviderState;
import com.powsybl.benchmark.loadflow.state.Rte1888NetworkState;
import com.powsybl.benchmark.loadflow.state.Rte6515NetworkState;
import com.powsybl.loadflow.LoadFlow;
import com.powsybl.loadflow.LoadFlowResult;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Warmup;

import java.util.concurrent.TimeUnit;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class LoadFlowBenchmark {

    @Benchmark
    public LoadFlowResult ieee14LoadFlow(LoadFlowProviderState providerState, IeeeNetworkState networkState, LoadFlowParametersState parametersState) {
        return LoadFlow.find(providerState.getProvider()).run(networkState.getIeee14Network(), parametersState.getType().getParameters());
    }

    @Benchmark
    public LoadFlowResult ieee118LoadFlow(LoadFlowProviderState providerState, IeeeNetworkState networkState, LoadFlowParametersState parametersState) {
        return LoadFlow.find(providerState.getProvider()).run(networkState.getIeee118Network(), parametersState.getType().getParameters());
    }

    @Benchmark
    public LoadFlowResult ieee300LoadFlow(LoadFlowProviderState providerState, IeeeNetworkState networkState, LoadFlowParametersState parametersState) {
        return LoadFlow.find(providerState.getProvider()).run(networkState.getIeee300Network(), parametersState.getType().getParameters());
    }

    @Benchmark
    @Warmup(time = 30)
    @Measurement(time = 30)
    public LoadFlowResult rte1888LoadFlow(LoadFlowProviderState providerState, Rte1888NetworkState networkState, LoadFlowParametersState parametersState) {
        return LoadFlow.find(providerState.getProvider()).run(networkState.getNetwork(), parametersState.getType().getParameters());
    }

    @Benchmark
    @Warmup(time = 30)
    @Measurement(time = 30)
    public LoadFlowResult rte6515LoadFlow(LoadFlowProviderState providerState, Rte6515NetworkState networkState, LoadFlowParametersState parametersState) {
        return LoadFlow.find(providerState.getProvider()).run(networkState.getNetwork(), parametersState.getType().getParameters());
    }
}
