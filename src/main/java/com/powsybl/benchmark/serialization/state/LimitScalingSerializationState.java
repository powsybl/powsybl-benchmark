/**
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.serialization.state;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.powsybl.security.json.limitscaling.LimitScalingModule;
import com.powsybl.security.limitscaling.LimitScalingList;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

/**
 * @author Nicolas Rol {@literal <nicolas.rol at rte-france.com>}
 */
@State(Scope.Thread)
public class LimitScalingSerializationState extends AbstractSerializationState<LimitScalingList> {

    private static final ObjectMapper MAPPER = new ObjectMapper().registerModule(new LimitScalingModule());
    private static final ObjectReader READER = MAPPER.readerFor(LimitScalingList.class);
    private static final ObjectWriter WRITER = MAPPER.writerWithDefaultPrettyPrinter();
    private static final String PARAMETER_NAME = "limit_scalings.file";

    /**
     * Default limit scaling file path.
     * <p>You can provide your own file by using the {@value #PARAMETER_NAME} parameter</p>
     */
    @Param({"src/main/resources/data/limit_scalings.json"})
    private String defaultLimitScalingPath;

    String getDefaultFilePath() {
        return defaultLimitScalingPath;
    }

    String getParameterName() {
        return PARAMETER_NAME;
    }

    protected LimitScalingList readData() throws IOException {
        try (InputStream inputStream = Files.newInputStream(filePath)) {
            return READER.readValue(inputStream);
        }
    }

    public ObjectReader reader() {
        return READER;
    }

    public ObjectWriter writer() {
        return WRITER;
    }
}
