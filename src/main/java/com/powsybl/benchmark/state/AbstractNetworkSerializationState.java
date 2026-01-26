/*
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.state;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.powsybl.commons.PowsyblException;
import com.powsybl.iidm.network.Network;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.TearDown;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * <p>You can provide your own file by using the {@value #PARAMETER_NAME} parameter</p>
 * @author Nicolas Rol {@literal <nicolas.rol at rte-france.com>}
 */
public abstract class AbstractNetworkSerializationState {

    private static final String PARAMETER_NAME = "network.file";
    private static final String DEFAULT_NETWORK = "case6515rte";

    private FileSystem fileSystem;
    private Path filePath;
    private Network network;
    private Path outputDir;
    private Path outputPath;
    private Properties properties;
    private Path tmpDir;

    @Setup(Level.Trial)
    public void setup() throws IOException {
        // Initialize Jimfs
        fileSystem = Jimfs.newFileSystem(Configuration.unix());
        tmpDir = fileSystem.getPath("/tmp");
        outputDir = tmpDir.resolve("output");
        Files.createDirectories(outputDir);

        // Get the network
        network = loadNetwork();

        // Properties
        properties = new Properties();

        // Setup specific data
        setupSpecificData();

        // Write the network to a temporary file
        writeTmpFile();
    }

    @TearDown(Level.Trial)
    public void closeFileSystem() throws IOException {
        if (fileSystem != null) {
            fileSystem.close();
        }
    }

    public Path getFilePath() {
        return filePath;
    }

    public Network getNetwork() {
        return network;
    }

    public Path getOutputPath() {
        return outputPath;
    }

    protected Path getOutputDir() {
        return outputDir;
    }

    public Properties getProperties() {
        return properties;
    }

    protected Path getTmpDir() {
        return tmpDir;
    }

    protected void setFilePath(Path filePath) {
        this.filePath = filePath;
    }

    protected void setOutputPath(Path outputPath) {
        this.outputPath = outputPath;
    }

    protected abstract void setupSpecificData();

    protected abstract void writeTmpFile();

    private Network loadNetwork() {
        String networkFile = System.getProperty(PARAMETER_NAME);
        if (networkFile == null) {
            return MatpowerUtil.importMat(DEFAULT_NETWORK);
        }
        Path path = Path.of(networkFile);
        if (!Files.exists(path)) {
            throw new PowsyblException("File not found: " + networkFile);
        }
        return Network.read(path);
    }
}
