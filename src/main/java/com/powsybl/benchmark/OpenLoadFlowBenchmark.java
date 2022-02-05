/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.benchmark;

import com.powsybl.loadflow.LoadFlow;
import com.powsybl.loadflow.LoadFlowResult;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class OpenLoadFlowBenchmark {

    @Benchmark
    public LoadFlowResult ieee14(IeeeNetworkState networkState, LoadFlowParametersState parametersState) {
        return LoadFlow.run(networkState.getIeee14Network(), parametersState.getParameters());
    }

    @Benchmark
    public LoadFlowResult ieee118(IeeeNetworkState networkState, LoadFlowParametersState parametersState) {
        return LoadFlow.run(networkState.getIeee118Network(), parametersState.getParameters());
    }

    @Benchmark
    public LoadFlowResult ieee300(IeeeNetworkState networkState, LoadFlowParametersState parametersState) {
        return LoadFlow.run(networkState.getIeee300Network(), parametersState.getParameters());
    }

    @Benchmark
    @Warmup(time = 30)
    @Measurement(time = 30)
    public LoadFlowResult rte6515(Rte6515NetworkState networkState, LoadFlowParametersState parametersState) {
        return LoadFlow.run(networkState.getNetwork(), parametersState.getParameters());
    }

    @Benchmark
    @Warmup(time = 30)
    @Measurement(time = 30)
    public LoadFlowResult pegase13659(Pegase13659NetworkState networkState, LoadFlowParametersState parametersState) {
        return LoadFlow.run(networkState.getNetwork(), parametersState.getParameters());
    }
}
