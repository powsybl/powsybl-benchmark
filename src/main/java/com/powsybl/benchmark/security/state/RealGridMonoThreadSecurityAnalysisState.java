/**
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.security.state;

import org.openjdk.jmh.annotations.Param;

import static com.powsybl.benchmark.commons.Constants.REAL_GRID;

/**
 * @author Nicolas Rol {@literal <nicolas.rol at rte-france.com>}
 */
public class RealGridMonoThreadSecurityAnalysisState extends MonoThreadSecurityAnalysisState {

    @Param({REAL_GRID})
    protected String networkName;

}
