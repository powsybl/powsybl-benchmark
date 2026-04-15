/*
 * Copyright (c) 2022-2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.sensitivity;

import com.powsybl.benchmark.loadflow.state.LoadFlowProviderState;
import com.powsybl.benchmark.sensitivity.state.SensitivityAnalysisState;
import com.powsybl.sensitivity.SensitivityAnalysis;
import com.powsybl.sensitivity.SensitivityAnalysisResult;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

/**
 * @author Bertrand Rix {@literal <bertrand.rix at artelys.com>}
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 3, time = 30, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 30, timeUnit = TimeUnit.SECONDS)
public class SensitivityAnalysisBenchmark {

    private static final int FORKS = 1;

    @Fork(FORKS)
    @Benchmark
    public void benchmarkSensitivityAnalysis(Blackhole blackhole,
                                             LoadFlowProviderState providerState,
                                             SensitivityAnalysisState sensitivityAnalysisState) {
        SensitivityAnalysisResult result = SensitivityAnalysis.find(providerState.getProvider())
            .run(sensitivityAnalysisState.getNetwork(),
                sensitivityAnalysisState.getFactors(),
                sensitivityAnalysisState.getRunParameters());
        blackhole.consume(result);
    }
}
