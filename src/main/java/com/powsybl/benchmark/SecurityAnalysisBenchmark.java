/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.benchmark;

import com.powsybl.computation.local.LocalComputationManager;
import com.powsybl.contingency.BranchContingency;
import com.powsybl.contingency.Contingency;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.VariantManagerConstants;
import com.powsybl.security.LimitViolationFilter;
import com.powsybl.security.SecurityAnalysis;
import com.powsybl.security.SecurityAnalysisParameters;
import com.powsybl.security.SecurityAnalysisResult;
import com.powsybl.security.detectors.DefaultLimitViolationDetector;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class SecurityAnalysisBenchmark {

    private SecurityAnalysisBenchmark() {
    }

    private static SecurityAnalysisResult run(String provider, Network network) {
        List<Contingency> contingencies = network.getLineStream()
                .map(line -> new Contingency(line.getId(), new BranchContingency(line.getId())))
                .collect(Collectors.toList());
        SecurityAnalysisParameters parameters = new SecurityAnalysisParameters();
        return SecurityAnalysis.find(provider)
                .run(network,
                        VariantManagerConstants.INITIAL_VARIANT_ID,
                        new DefaultLimitViolationDetector(),
                        new LimitViolationFilter(),
                        LocalComputationManager.getDefault(),
                        parameters,
                        n -> contingencies,
                        Collections.emptyList(),
                        Collections.emptyList())
                .getResult();
    }

    public static void main(String[] args) {
        Network network = MatpowerUtil.importMat("case6515rte");
        run("OpenSecurityAnalysis", network);
    }
}
