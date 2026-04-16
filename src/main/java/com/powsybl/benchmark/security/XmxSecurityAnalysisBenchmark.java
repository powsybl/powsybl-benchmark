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
import org.openjdk.jmh.infra.Blackhole;

/**
 * @author Nicolas Rol {@literal <nicolas.rol at rte-france.com>}
 */
public class XmxSecurityAnalysisBenchmark {

    private static final int FORKS = 1;

    @Fork(value = FORKS, jvmArgs = {"-Xmx128m"})
    @Benchmark
    public void runXmx128M(Blackhole blackhole, MultiThreadsSecurityAnalysisState multiThreadsSecurityAnalysisState) {
        runXmx(blackhole, multiThreadsSecurityAnalysisState);
    }

    @Fork(value = FORKS, jvmArgs = {"-Xmx256m"})
    @Benchmark
    public void runXmx256M(Blackhole blackhole, MultiThreadsSecurityAnalysisState multiThreadsSecurityAnalysisState) {
        runXmx(blackhole, multiThreadsSecurityAnalysisState);
    }

    @Fork(value = FORKS, jvmArgs = {"-Xmx512m"})
    @Benchmark
    public void runXmx512M(Blackhole blackhole, MultiThreadsSecurityAnalysisState multiThreadsSecurityAnalysisState) {
        runXmx(blackhole, multiThreadsSecurityAnalysisState);
    }

    @Fork(value = FORKS, jvmArgs = {"-Xmx1g"})
    @Benchmark
    public void runXmx1G(Blackhole blackhole, MultiThreadsSecurityAnalysisState multiThreadsSecurityAnalysisState) {
        runXmx(blackhole, multiThreadsSecurityAnalysisState);
    }

    @Fork(value = FORKS, jvmArgs = {"-Xmx2g"})
    @Benchmark
    public void runXmx2G(Blackhole blackhole, MultiThreadsSecurityAnalysisState multiThreadsSecurityAnalysisState) {
        runXmx(blackhole, multiThreadsSecurityAnalysisState);
    }

    @Fork(value = FORKS, jvmArgs = {"-Xmx4g"})
    @Benchmark
    public void runXmx4G(Blackhole blackhole, MultiThreadsSecurityAnalysisState multiThreadsSecurityAnalysisState) {
        runXmx(blackhole, multiThreadsSecurityAnalysisState);
    }

    @Fork(value = FORKS, jvmArgs = {"-Xmx8g"})
    @Benchmark
    public void runXmx8G(Blackhole blackhole, MultiThreadsSecurityAnalysisState multiThreadsSecurityAnalysisState) {
        runXmx(blackhole, multiThreadsSecurityAnalysisState);
    }

    @Fork(FORKS)
    @Benchmark
    public void runXmxUndefined(Blackhole blackhole, MultiThreadsSecurityAnalysisState multiThreadsSecurityAnalysisState) {
        runXmx(blackhole, multiThreadsSecurityAnalysisState);
    }

    private static void runXmx(Blackhole blackhole,
                               MultiThreadsSecurityAnalysisState multiThreadsSecurityAnalysisState) {
        SecurityAnalysisResult result = SecurityAnalysis.find(multiThreadsSecurityAnalysisState.getProvider())
            .run(multiThreadsSecurityAnalysisState.getNetwork(),
                multiThreadsSecurityAnalysisState.getContingencies(),
                multiThreadsSecurityAnalysisState.getRunParameters())
            .getResult();
        blackhole.consume(result);
    }
}
