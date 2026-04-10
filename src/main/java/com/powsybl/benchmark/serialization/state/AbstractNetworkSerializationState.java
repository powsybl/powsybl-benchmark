/*
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.serialization.state;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.powsybl.benchmark.commons.MatpowerUtil;
import com.powsybl.commons.datasource.ResourceDataSource;
import com.powsybl.commons.datasource.ResourceSet;
import com.powsybl.iidm.network.Network;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import static com.powsybl.benchmark.commons.Constants.REAL_GRID;
import static com.powsybl.benchmark.commons.Constants.RTE_6515;

/**
 * @author Nicolas Rol {@literal <nicolas.rol at rte-france.com>}
 */
@State(Scope.Thread)
public abstract class AbstractNetworkSerializationState {

    private FileSystem fileSystem;
    private Path filePath;
    private Path outputDir;
    private Path outputPath;
    private Properties properties;
    private Path tmpDir;

    @Param({REAL_GRID, RTE_6515})
    private String networkName;

    private Network network;

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
        return switch (networkName) {
            case REAL_GRID -> Network.read(new ResourceDataSource(networkName, new ResourceSet("/data", networkName + ".zip")));
            case RTE_6515 -> MatpowerUtil.importMat(networkName);
            default -> throw new IllegalArgumentException("Unknown network: " + networkName);
        };
    }
}
