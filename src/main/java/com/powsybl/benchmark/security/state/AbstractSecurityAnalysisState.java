/*
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.security.state;

import com.powsybl.benchmark.commons.state.AbstractAnalysisState;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.SwitchKind;
import com.powsybl.openloadflow.sa.OpenSecurityAnalysisParameters;
import com.powsybl.security.SecurityAnalysisParameters;
import com.powsybl.security.SecurityAnalysisRunParameters;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

/**
 * @author Nicolas Rol {@literal <nicolas.rol at rte-france.com>}
 */
@State(Scope.Thread)
public abstract class AbstractSecurityAnalysisState extends AbstractAnalysisState {

    private SecurityAnalysisRunParameters runParameters;

    @Override
    @Setup(Level.Trial)
    public void doSetup() {
        super.doSetup();

        // Security analysis parameters
        SecurityAnalysisParameters parameters = new SecurityAnalysisParameters()
            .setLoadFlowParameters(getParameters());

        // OLF security analysis parameters
        OpenSecurityAnalysisParameters securityAnalysisParametersExt = new OpenSecurityAnalysisParameters();
        parameters.addExtension(OpenSecurityAnalysisParameters.class, securityAnalysisParametersExt);
        securityAnalysisParametersExt.setThreadCount(getThreadCount());
        if (!hasOpenableBreaker(getNetwork())) {
            // Contingency propagation stops at openable (non fictitious) breakers. In a node-breaker network without
            // any, such as RealGrid whose only switches are open fictitious breakers created by the CGMES importer
            // for disconnected terminals, each contingency would disconnect almost the whole network. Networks
            // without any switch (bus-breaker IEEE and Matpower cases) are not affected by the propagation.
            securityAnalysisParametersExt.setContingencyPropagation(false);
        }

        // Security analysis run parameters
        runParameters = SecurityAnalysisRunParameters.getDefault().setSecurityAnalysisParameters(parameters);
    }

    private static boolean hasOpenableBreaker(Network network) {
        return network.getSwitchStream().anyMatch(sw -> sw.getKind() == SwitchKind.BREAKER && !sw.isFictitious());
    }

    public SecurityAnalysisRunParameters getRunParameters() {
        return runParameters;
    }
}
