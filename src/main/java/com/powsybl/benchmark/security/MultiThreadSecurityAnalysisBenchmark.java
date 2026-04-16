/*
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.security;

import com.powsybl.benchmark.security.state.MultiThreadsSecurityAnalysisState;
import com.powsybl.security.SecurityAnalysis;
import com.powsybl.security.SecurityAnalysisResult;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

/**
 * @author Nicolas Rol {@literal <nicolas.rol at rte-france.com>}
 */
@Warmup(iterations = 3, time = 30, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 30, timeUnit = TimeUnit.SECONDS)
public class MultiThreadSecurityAnalysisBenchmark {

    private static final int FORKS = 1;

    @Fork(FORKS)
    @Benchmark
    public void benchmarkMultiThreadsSecurityAnalysis(Blackhole blackhole,
                                MultiThreadsSecurityAnalysisState multiThreadsSecurityAnalysisState) {
        SecurityAnalysisResult result = SecurityAnalysis.find(multiThreadsSecurityAnalysisState.getProvider())
            .run(multiThreadsSecurityAnalysisState.getNetwork(),
                multiThreadsSecurityAnalysisState.getContingencies(),
                multiThreadsSecurityAnalysisState.getRunParameters())
            .getResult();
        blackhole.consume(result);
    }
}
