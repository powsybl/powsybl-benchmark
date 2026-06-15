/*
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.security;

import com.powsybl.benchmark.commons.ReleaseBenchmark;
import com.powsybl.benchmark.security.state.MonoThreadSecurityAnalysisState;
import com.powsybl.benchmark.security.state.ReleaseMonoThreadSecurityAnalysisState;
import com.powsybl.security.SecurityAnalysis;
import com.powsybl.security.SecurityAnalysisResult;
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
 * @author Nicolas Rol {@literal <nicolas.rol at rte-france.com>}
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3, time = 30, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 30, timeUnit = TimeUnit.SECONDS)
public class MonoThreadSecurityAnalysisBenchmark {

    private static final int FORKS = 1;

    @Benchmark
    @Fork(FORKS)
    public void benchmarkMonoThreadSecurityAnalysis(Blackhole blackhole,
                                                    MonoThreadSecurityAnalysisState monoThreadSecurityAnalysisState) {
        SecurityAnalysisResult result = SecurityAnalysis.find(monoThreadSecurityAnalysisState.getProvider())
            .run(monoThreadSecurityAnalysisState.getNetwork(),
                monoThreadSecurityAnalysisState.getContingencies(),
                monoThreadSecurityAnalysisState.getRunParameters())
            .getResult();
        blackhole.consume(result);
    }

    /**
     * This benchmark is only for release purpose as Security analysis on the RealGrid network takes A LOT of time.
     */
    @Benchmark
    @Fork(FORKS)
    @ReleaseBenchmark
    public void benchmarkMonoThreadSecurityAnalysisWithoutRealGrid(Blackhole blackhole,
                                                                   ReleaseMonoThreadSecurityAnalysisState monoThreadSecurityAnalysisState) {
        SecurityAnalysisResult result = SecurityAnalysis.find(monoThreadSecurityAnalysisState.getProvider())
            .run(monoThreadSecurityAnalysisState.getNetwork(),
                monoThreadSecurityAnalysisState.getContingencies(),
                monoThreadSecurityAnalysisState.getRunParameters())
            .getResult();
        blackhole.consume(result);
    }
}
