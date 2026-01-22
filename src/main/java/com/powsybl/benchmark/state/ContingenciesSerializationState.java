/*
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.state;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.powsybl.commons.PowsyblException;
import com.powsybl.contingency.json.ContingencyJsonModule;
import com.powsybl.contingency.list.ContingencyList;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Nicolas Rol {@literal <nicolas.rol at rte-france.com>}
 */
@State(Scope.Thread)
public class ContingenciesSerializationState {

    private static final ObjectMapper MAPPER = new ObjectMapper().registerModule(new ContingencyJsonModule());
    private static final ObjectReader READER = MAPPER.readerFor(ContingencyList.class);

    /**
     * Default contingency list file path.
     * <p>You can provide your own file by using the {@code -Dcontingency.file} paramter</p>
     */
    @Param({"src/main/resources/data/contingencyList.json"})
    private String defaultContingencyListPath;

    private Path contingencyListPath;
    private byte[] fileContent;
    private ContingencyList contingencyList;

    @Setup(Level.Trial)
    public void setup() throws IOException {
        contingencyListPath = getContingencyFilePath();
        fileContent = Files.readAllBytes(contingencyListPath);
        try (InputStream inputStream = Files.newInputStream(contingencyListPath)) {
            contingencyList = READER.readValue(inputStream);
        }
    }

    public Path getContingencyListPath() {
        return contingencyListPath;
    }

    public byte[] getFileContent() {
        return fileContent;
    }

    public ContingencyList getContingencyList() {
        return contingencyList;
    }

    private Path getContingencyFilePath() {
        String contingencyListFile = System.getProperty("contingency.file", defaultContingencyListPath);

        Path path = Path.of(contingencyListFile);
        if (!Files.exists(path)) {
            throw new PowsyblException("File not found: " + contingencyListFile);
        }
        return path;
    }
}
