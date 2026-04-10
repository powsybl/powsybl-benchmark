/*
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.serialization.state;

import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

/**
 * @author Nicolas Rol {@literal <nicolas.rol at rte-france.com>}
 */
@State(Scope.Thread)
public class CgmesSerializationState extends AbstractNetworkSerializationState {

    @Param({"CGMES"})
    private String format;

    @Override
    public void setupSpecificData() {
        setOutputPath(getOutputDir().resolve(getFormat().toLowerCase()));
    }

    public String getFormat() {
        return format;
    }

    protected void writeTmpFile() {
        setFilePath(getTmpDir().resolve(getFormat().toLowerCase() + ".zip"));
        getNetwork().write(getFormat(), getProperties(), getFilePath());
    }
}
