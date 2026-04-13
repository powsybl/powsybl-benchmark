/*
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
import com.powsybl.contingency.json.ContingencyJsonModule;
import com.powsybl.contingency.list.ContingencyList;
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
public class ContingenciesSerializationState extends AbstractSerializationState<ContingencyList> {

    private static final ObjectMapper MAPPER = new ObjectMapper().registerModule(new ContingencyJsonModule());
    private static final ObjectReader READER = MAPPER.readerFor(ContingencyList.class);
    private static final ObjectWriter WRITER = MAPPER.writerWithDefaultPrettyPrinter();
    private static final String PARAMETER_NAME = "contingency.file";

    /**
     * Default contingency list file path.
     * <p>You can provide your own file by using the {@value #PARAMETER_NAME} parameter</p>
     */
    @Param({"src/main/resources/data/contingencyList.json"})
    private String defaultContingencyListPath;

    String getDefaultFilePath() {
        return defaultContingencyListPath;
    }

    String getParameterName() {
        return PARAMETER_NAME;
    }

    protected ContingencyList readData() throws IOException {
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
