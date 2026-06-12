/*
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.security.state;

import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import static com.powsybl.benchmark.commons.Constants.*;

/**
 * @author Nicolas Rol {@literal <nicolas.rol at rte-france.com>}
 */
@State(Scope.Thread)
public class ReleaseMonoThreadSecurityAnalysisState extends MonoThreadSecurityAnalysisState {

    @Param({IEEE_14, IEEE_118, IEEE_300, RTE_1888, RTE_6515})
    private String networkName;

    @Override
    protected String getNetworkName() {
        return networkName;
    }

}
