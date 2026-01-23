/*
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.state;

import com.powsybl.commons.io.TreeDataFormat;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

/**
 * @author Nicolas Rol {@literal <nicolas.rol at rte-france.com>}
 */
@State(Scope.Thread)
public class JiidmSerializationState extends AbstractIidmSerializationState {

    private static final TreeDataFormat FORMAT = TreeDataFormat.JSON;
    private static final String PARAMETER_NAME = "jiidm.file";

    /**
     * Default JIIDM network file path.
     * <p>You can provide your own file by using the {@value #PARAMETER_NAME} parameter</p>
     */
    @Param({"src/main/resources/data/europeanLvTestFeederRef.jiidm"})
    private String defaultJiidmFilePath;

    String getDefaultFilePath() {
        return defaultJiidmFilePath;
    }

    String getParameterName() {
        return PARAMETER_NAME;
    }

    public TreeDataFormat getFormat() {
        return FORMAT;
    }
}
