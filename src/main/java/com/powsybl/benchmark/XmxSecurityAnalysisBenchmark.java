/*
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark;

import com.powsybl.benchmark.state.LoadFlowParametersType;
import com.powsybl.benchmark.state.SecurityAnalysisMultiThreadsParametersState;
import com.powsybl.security.SecurityAnalysisResult;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;

/**
 * @author Nicolas Rol {@literal <nicolas.rol at rte-france.com>}
 */
public class XmxSecurityAnalysisBenchmark extends AbstractSecurityAnalysisBenchmark {

    private static final int FORKS = 1;

    @Fork(value = FORKS, jvmArgs = {"-Xmx128m"})
    @Benchmark
    public SecurityAnalysisResult runXmx128M(SecurityAnalysisMultiThreadsParametersState securityAnalysisMultiThreadsParametersState) {
        return runXmx(securityAnalysisMultiThreadsParametersState);
    }

    @Fork(value = FORKS, jvmArgs = {"-Xmx256m"})
    @Benchmark
    public SecurityAnalysisResult runXmx256M(SecurityAnalysisMultiThreadsParametersState securityAnalysisMultiThreadsParametersState) {
        return runXmx(securityAnalysisMultiThreadsParametersState);
    }

    @Fork(value = FORKS, jvmArgs = {"-Xmx512m"})
    @Benchmark
    public SecurityAnalysisResult runXmx512M(SecurityAnalysisMultiThreadsParametersState securityAnalysisMultiThreadsParametersState) {
        return runXmx(securityAnalysisMultiThreadsParametersState);
    }

    @Fork(value = FORKS, jvmArgs = {"-Xmx1g"})
    @Benchmark
    public SecurityAnalysisResult runXmx1G(SecurityAnalysisMultiThreadsParametersState securityAnalysisMultiThreadsParametersState) {
        return runXmx(securityAnalysisMultiThreadsParametersState);
    }

    @Fork(value = FORKS, jvmArgs = {"-Xmx2g"})
    @Benchmark
    public SecurityAnalysisResult runXmx2G(SecurityAnalysisMultiThreadsParametersState securityAnalysisMultiThreadsParametersState) {
        return runXmx(securityAnalysisMultiThreadsParametersState);
    }

    @Fork(value = FORKS, jvmArgs = {"-Xmx4g"})
    @Benchmark
    public SecurityAnalysisResult runXmx4G(SecurityAnalysisMultiThreadsParametersState securityAnalysisMultiThreadsParametersState) {
        return runXmx(securityAnalysisMultiThreadsParametersState);
    }

    @Fork(value = FORKS, jvmArgs = {"-Xmx8g"})
    @Benchmark
    public SecurityAnalysisResult runXmx8G(SecurityAnalysisMultiThreadsParametersState securityAnalysisMultiThreadsParametersState) {
        return runXmx(securityAnalysisMultiThreadsParametersState);
    }

    @Fork(FORKS)
    @Benchmark
    public SecurityAnalysisResult runXmxXmxUndefined(SecurityAnalysisMultiThreadsParametersState securityAnalysisMultiThreadsParametersState) {
        return runXmx(securityAnalysisMultiThreadsParametersState);
    }

    private static SecurityAnalysisResult runXmx(SecurityAnalysisMultiThreadsParametersState securityAnalysisMultiThreadsParametersState) {
        return run(securityAnalysisMultiThreadsParametersState.getProvider(),
            securityAnalysisMultiThreadsParametersState.getNetwork(),
            securityAnalysisMultiThreadsParametersState.getThreadCount(),
            LoadFlowParametersType.STANDARD.getParameters(),
            securityAnalysisMultiThreadsParametersState.getContingencies());
    }
}
