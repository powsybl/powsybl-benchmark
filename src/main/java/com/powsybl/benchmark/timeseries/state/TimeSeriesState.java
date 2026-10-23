/**
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.timeseries.state;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.powsybl.timeseries.*;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Nicolas Rol {@literal <nicolas.rol at rte-france.com>}
 */
@State(Scope.Thread)
public class TimeSeriesState {

    private static final int CHUNK_SIZE = 500;
    private static final int SIZE = 800;
    private static final int STEPS = 2500;
    private static final int VERSIONS = 5;
    private static final String FILE_NAME = "timeseries.csv";
    private static final Random RANDOM = new Random(31);

    private FileSystem fileSystem;
    private Path filePath;
    private Path outputPath;
    private RegularTimeSeriesIndex index;
    private TimeSeriesTable table;

    public static TimeSeriesTable generateTimeSeriesTable(RegularTimeSeriesIndex index) {
        TimeSeriesTable table = new TimeSeriesTable(1, VERSIONS, index);
        for (int i = 0; i < VERSIONS; i++) {
            List<DoubleTimeSeries> timeSeriesList = new ArrayList<>();
            for (int j = 0; j < SIZE; j++) {
                TimeSeriesMetadata metadata1 = new TimeSeriesMetadata("ts" + j, TimeSeriesDataType.DOUBLE, index);
                DoubleTimeSeries ts = new StoredDoubleTimeSeries(metadata1, generateDataChunk());
                timeSeriesList.add(ts);
            }
            table.load(i + 1, timeSeriesList);
        }
        return table;
    }

    private static RegularTimeSeriesIndex generateIndex() {
        Duration step = Duration.ofMinutes(1);
        Instant start = Instant.parse("2015-01-01T00:00:00Z");
        Instant end = start.plus(step.multipliedBy(STEPS));
        return RegularTimeSeriesIndex.create(start, end, step);
    }

    private static DoubleDataChunk[] generateDataChunk() {
        int chunkNumber = Math.ceilDiv(SIZE, CHUNK_SIZE);
        UncompressedDoubleDataChunk[] chunks = new UncompressedDoubleDataChunk[chunkNumber];
        for (int i = 0; i < chunkNumber; i++) {
            double[] values = i < chunkNumber - 1 ? RANDOM.doubles(CHUNK_SIZE).toArray() : RANDOM.doubles(SIZE - (long) i * CHUNK_SIZE).toArray();
            chunks[i] = new UncompressedDoubleDataChunk(i * CHUNK_SIZE, values);
        }
        return chunks;
    }

    @Setup(Level.Trial)
    public void setup() throws IOException {
        // Data generation
        index = generateIndex();
        table = generateTimeSeriesTable(index);

        // Initialize Jimfs
        fileSystem = Jimfs.newFileSystem(Configuration.unix());
        Path tmpDir = fileSystem.getPath("/tmp");
        Path outputDir = tmpDir.resolve("output");
        Files.createDirectories(outputDir);
        filePath = tmpDir.resolve(FILE_NAME);
        outputPath = outputDir.resolve(FILE_NAME);

        // Serialization
        table.writeCsv(filePath);
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

    public Path getOutputPath() {
        return outputPath;
    }

    public TimeSeriesTable getTable() {
        return table;
    }

    public RegularTimeSeriesIndex getIndex() {
        return index;
    }
}
