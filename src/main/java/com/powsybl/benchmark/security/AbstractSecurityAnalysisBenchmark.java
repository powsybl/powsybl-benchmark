/*
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.security;

import com.powsybl.contingency.Contingency;
import com.powsybl.iidm.network.Network;
import com.powsybl.loadflow.LoadFlowParameters;
import com.powsybl.openloadflow.sa.OpenSecurityAnalysisParameters;
import com.powsybl.security.SecurityAnalysis;
import com.powsybl.security.SecurityAnalysisParameters;
import com.powsybl.security.SecurityAnalysisResult;
import com.powsybl.security.SecurityAnalysisRunParameters;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Warmup;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Nicolas Rol {@literal <nicolas.rol at rte-france.com>}
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 3, time = 10, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 20, timeUnit = TimeUnit.SECONDS)
public abstract class AbstractSecurityAnalysisBenchmark {

    protected AbstractSecurityAnalysisBenchmark() {
        // empty constructor
    }

    protected static final int FORKS = 1;

    protected static SecurityAnalysisResult run(String loadFlowProvider,
                                              Network network,
                                              int threads,
                                              LoadFlowParameters loadFlowParameters,
                                              List<Contingency> contingencies) {
        // Parameters
        SecurityAnalysisParameters parameters = new SecurityAnalysisParameters()
            .setLoadFlowParameters(loadFlowParameters);
        OpenSecurityAnalysisParameters securityAnalysisParametersExt = new OpenSecurityAnalysisParameters();
        parameters.addExtension(OpenSecurityAnalysisParameters.class, securityAnalysisParametersExt);
        securityAnalysisParametersExt.setThreadCount(threads);
        SecurityAnalysisRunParameters runParameters = SecurityAnalysisRunParameters.getDefault().setSecurityAnalysisParameters(parameters);

        return SecurityAnalysis.find(loadFlowProvider).run(network, contingencies, runParameters).getResult();
    }
}
