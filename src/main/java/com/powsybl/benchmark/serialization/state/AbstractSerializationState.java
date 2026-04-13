/*
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.serialization.state;

import com.powsybl.commons.PowsyblException;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Setup;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Nicolas Rol {@literal <nicolas.rol at rte-france.com>}
 */
public abstract class AbstractSerializationState<T> {

    protected Path filePath;
    private byte[] fileContent;
    private T data;

    @Setup(Level.Trial)
    public void setup() throws IOException {
        filePath = getActualFilePath();
        fileContent = Files.readAllBytes(filePath);
        data = readData();
    }

    public Path getFilePath() {
        return filePath;
    }

    public byte[] getFileContent() {
        return fileContent;
    }

    public T getData() {
        return data;
    }

    abstract String getDefaultFilePath();

    abstract String getParameterName();

    abstract T readData() throws IOException;

    private Path getActualFilePath() {
        String contingencyListFile = System.getProperty(getParameterName(), getDefaultFilePath());

        Path path = Path.of(contingencyListFile);
        if (!Files.exists(path)) {
            throw new PowsyblException("File not found: " + contingencyListFile);
        }
        return path;
    }
}
