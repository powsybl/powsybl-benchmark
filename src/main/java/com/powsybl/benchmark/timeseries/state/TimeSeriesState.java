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
import com.powsybl.timeseries.ast.BinaryOperation;
import com.powsybl.timeseries.ast.DoubleNodeCalc;
import com.powsybl.timeseries.ast.NodeCalc;
import com.powsybl.timeseries.ast.TimeSeriesNameNodeCalc;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * @author Nicolas Rol {@literal <nicolas.rol at rte-france.com>}
 */
@State(Scope.Thread)
public class TimeSeriesState {

    // Number of steps in the index
    private static final int STEPS = 500;

    // Number of time series
    private static final int SIZE = 100;

    // Number of values per chunk
    private static final int CHUNK_SIZE = 500;

    // Max depth of the tree
    private static final int MAX_DEPTH = 25;

    private static final int VERSIONS = 5;
    private static final String CSV_FILE_NAME = "timeseries.csv";
    private static final String JSON_FILE_NAME = "timeseries.json";
    private static final Random RANDOM = new Random(31);

    private FileSystem fileSystem;
    private Path csvFilePath;
    private Path jsonFilePath;
    private Path csvOutputPath;
    private Path jsonOutputPath;
    private RegularTimeSeriesIndex index;
    private TimeSeriesTable table;
    private CalculatedTimeSeries calculatedTimeSeries;

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

    public static CalculatedTimeSeries generateCalculatedTimeSeries(RegularTimeSeriesIndex index) {
        List<DoubleTimeSeries> timeSeriesList = new ArrayList<>(SIZE * (SIZE - 1));
        NodeCalc nodeCalc = generateNodeCalc(index, timeSeriesList, 0);

        CalculatedTimeSeries tsCalc = new CalculatedTimeSeries("ts_calc", nodeCalc);
        addResolver(tsCalc, timeSeriesList);
        return tsCalc;
    }

    private static RegularTimeSeriesIndex generateIndex() {
        Duration step = Duration.ofMinutes(1);
        Instant start = Instant.parse("2015-01-01T00:00:00Z");
        Instant end = start.plus(step.multipliedBy((long) STEPS - 1));
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

    private static void addResolver(CalculatedTimeSeries tsCalc, List<DoubleTimeSeries> timeSeriesList) {
        TimeSeriesNameResolver resolver = new TimeSeriesNameResolver() {
            @Override
            public List<TimeSeriesMetadata> getTimeSeriesMetadata(Set<String> timeSeriesNames) {
                return timeSeriesList.stream()
                    .map(DoubleTimeSeries::getMetadata)
                    .toList();
            }

            @Override
            public Set<Integer> getTimeSeriesDataVersions(String timeSeriesName) {
                return Collections.singleton(1);
            }

            @Override
            public List<DoubleTimeSeries> getDoubleTimeSeries(Set<String> timeSeriesNames) {
                return timeSeriesList;
            }
        };
        tsCalc.setTimeSeriesNameResolver(resolver);
    }

    private static NodeCalc generateNodeCalc(RegularTimeSeriesIndex index, List<DoubleTimeSeries> timeSeriesList, int currentLevel) {
        if (currentLevel == MAX_DEPTH || currentLevel > 5 && RANDOM.nextDouble() < 0.2) {
            return generateValueNodeCalc(index, timeSeriesList);
        } else {
            NodeCalc left = generateNodeCalc(index, timeSeriesList, currentLevel + 1);
            NodeCalc right = generateNodeCalc(index, timeSeriesList, currentLevel + 1);
            return generateBinaryNodeCalc(left, right);
        }
    }

    private static NodeCalc generateBinaryNodeCalc(NodeCalc left, NodeCalc right) {
        double rand = RANDOM.nextDouble();
        if (rand < 0.25) {
            return BinaryOperation.plus(left, right);
        } else if (rand < 0.5) {
            return BinaryOperation.minus(left, right);
        } else if (rand < 0.75) {
            return BinaryOperation.multiply(left, right);
        } else {
            return BinaryOperation.div(left, right);
        }
    }

    private static NodeCalc generateValueNodeCalc(RegularTimeSeriesIndex index, List<DoubleTimeSeries> timeSeriesList) {
        if (RANDOM.nextBoolean()) {
            return new DoubleNodeCalc(RANDOM.nextDouble());
        } else {
            if (timeSeriesList.isEmpty() || RANDOM.nextBoolean()) {
                String id = "ts_" + timeSeriesList.size();
                DoubleTimeSeries ts = createDoubleTimeSeries(index, id);
                timeSeriesList.add(ts);
                return new TimeSeriesNameNodeCalc(id);
            } else {
                return new TimeSeriesNameNodeCalc(timeSeriesList.get(RANDOM.nextInt(timeSeriesList.size())).getMetadata().getName());
            }
        }
    }

    private static DoubleTimeSeries createDoubleTimeSeries(RegularTimeSeriesIndex index, String id) {
        return TimeSeries.createDouble(id, index, RANDOM.doubles(STEPS).toArray());
    }

    @Setup(Level.Trial)
    public void setup() throws IOException {
        // Data generation
        index = generateIndex();
        table = generateTimeSeriesTable(index);
        calculatedTimeSeries = generateCalculatedTimeSeries(index);

        // Initialize Jimfs
        fileSystem = Jimfs.newFileSystem(Configuration.unix());
        Path tmpDir = fileSystem.getPath("/tmp");
        Path outputDir = tmpDir.resolve("output");
        Files.createDirectories(outputDir);

        // CSV serialization
        csvFilePath = tmpDir.resolve(CSV_FILE_NAME);
        csvOutputPath = outputDir.resolve(CSV_FILE_NAME);
        table.writeCsv(csvFilePath);

        // JSON serialization
        jsonFilePath = tmpDir.resolve(JSON_FILE_NAME);
        jsonOutputPath = outputDir.resolve(JSON_FILE_NAME);
        TimeSeries.writeJson(jsonFilePath, List.of(calculatedTimeSeries));
    }

    @TearDown(Level.Trial)
    public void closeFileSystem() throws IOException {
        if (fileSystem != null) {
            fileSystem.close();
        }
    }

    public Path getCsvFilePath() {
        return csvFilePath;
    }

    public Path getJsonFilePath() {
        return jsonFilePath;
    }

    public Path getCsvOutputPath() {
        return csvOutputPath;
    }

    public Path getJsonOutputPath() {
        return jsonOutputPath;
    }

    public TimeSeriesTable getTable() {
        return table;
    }

    public RegularTimeSeriesIndex getIndex() {
        return index;
    }

    public CalculatedTimeSeries getCalculatedTimeSeries() {
        return calculatedTimeSeries;
    }
}
