/**
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.serialization.state;

import org.openjdk.jmh.annotations.Param;

/**
 * @author Dissoubray Nathan {@literal <nathan.dissoubray at rte-france.com>}
 */
public class CommonFormatsNetworkSerializationState extends AbstractNetworkSerializationState {

    @Param({"XIIDM", "JIIDM", "BIIDM", "CGMES"})
    private String format;

    @Override
    public void setupSpecificData() {
        setOutputPath(getOutputDir().resolve("network." + getFormat().toLowerCase()));
    }

    public String getFormat() {
        return format;
    }

    @Override
    protected void writeTmpFile() {
        setFilePath(getTmpDir().resolve(getTmpDirPath()));
        getNetwork().write(
            getFormat(),
            "CGMES".equals(getFormat()) ? getProperties() : null,
            getFilePath()
        );
    }

    private String getTmpDirPath() {
        return String.format("network.%s%s",
            getFormat().toLowerCase(),
            "CGMES".equals(getFormat()) ? ".zip" : ""
        );
    }
}
