/**
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.serialization.state;

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
    private static final int STEPS = 100;

    // Number of time series
    private static final int SIZE = 75;

    // Number of values per chunk
    private static final int CHUNK_SIZE = 25;

    // Max depth of the tree
    private static final int MAX_DEPTH = 18;

    // Thresholds
    private static final int THRESHOLD_VALUE_START_DEPTH = 5;
    private static final double THRESHOLD_VALUE_START_RANDOM = 0.25;
    private static final double THRESHOLD_DOUBLE_NODE_RANDOM = 0.5;
    private static final double THRESHOLD_NEW_DOUBLE_TIMESERIES_RANDOM = 0.1;

    private static final int VERSIONS = 5;
    private static final String CSV_FILE_NAME = "timeseries.csv";
    private static final String JSON_FILE_NAME = "timeseries.json";

    private final Random random = new Random(31);

    private FileSystem fileSystem;
    private Path csvFilePath;
    private Path jsonFilePath;
    private Path csvOutputPath;
    private Path jsonOutputPath;
    private RegularTimeSeriesIndex index;
    private TimeSeriesTable table;
    private CalculatedTimeSeries calculatedTimeSeries;
    private List<List<DoubleTimeSeries>> timeSeriesLists;

    public static TimeSeriesTable generateTimeSeriesTable(List<List<DoubleTimeSeries>> timeSeriesLists, RegularTimeSeriesIndex index) {
        TimeSeriesTable table = new TimeSeriesTable(1, VERSIONS, index);
        for (int i = 0; i < VERSIONS; i++) {
            table.load(i + 1, timeSeriesLists.get(i));
        }
        return table;
    }

    public static CalculatedTimeSeries generateCalculatedTimeSeries(RegularTimeSeriesIndex index, Random random) {
        List<DoubleTimeSeries> timeSeriesList = new ArrayList<>(SIZE * (SIZE - 1));
        NodeCalc nodeCalc = generateNodeCalc(index, timeSeriesList, 0, random);

        CalculatedTimeSeries tsCalc = new CalculatedTimeSeries("ts_calc", nodeCalc);
        addResolver(tsCalc, timeSeriesList);
        return tsCalc;
    }

    private static List<List<DoubleTimeSeries>> generateTimeSeriesList(RegularTimeSeriesIndex index, Random random) {
        List<List<DoubleTimeSeries>> timeSeriesList = new ArrayList<>();
        for (int i = 0; i < VERSIONS; i++) {
            List<DoubleTimeSeries> versionTimeSeriesList = new ArrayList<>();
            for (int j = 0; j < SIZE; j++) {
                TimeSeriesMetadata metadata1 = new TimeSeriesMetadata("ts" + j, TimeSeriesDataType.DOUBLE, index);
                DoubleTimeSeries ts = new StoredDoubleTimeSeries(metadata1, generateDataChunk(random));
                versionTimeSeriesList.add(ts);
            }
            timeSeriesList.add(versionTimeSeriesList);
        }
        return timeSeriesList;
    }

    private static RegularTimeSeriesIndex generateIndex() {
        Duration step = Duration.ofMinutes(1);
        Instant start = Instant.parse("2015-01-01T00:00:00Z");
        Instant end = start.plus(step.multipliedBy((long) STEPS - 1));
        return RegularTimeSeriesIndex.create(start, end, step);
    }

    private static DoubleDataChunk[] generateDataChunk(Random random) {
        int chunkNumber = Math.ceilDiv(STEPS, CHUNK_SIZE);
        UncompressedDoubleDataChunk[] chunks = new UncompressedDoubleDataChunk[chunkNumber];
        for (int i = 0; i < chunkNumber; i++) {
            double[] values = i < chunkNumber - 1 ? random.doubles(CHUNK_SIZE).toArray() : random.doubles(STEPS - (long) i * CHUNK_SIZE).toArray();
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

    private static NodeCalc generateNodeCalc(RegularTimeSeriesIndex index, List<DoubleTimeSeries> timeSeriesList, int currentLevel, Random random) {
        if (currentLevel == MAX_DEPTH || currentLevel > THRESHOLD_VALUE_START_DEPTH && random.nextDouble() < THRESHOLD_VALUE_START_RANDOM) {
            return generateValueNodeCalc(index, timeSeriesList, random);
        } else {
            NodeCalc left = generateNodeCalc(index, timeSeriesList, currentLevel + 1, random);
            NodeCalc right = generateNodeCalc(index, timeSeriesList, currentLevel + 1, random);
            return generateBinaryNodeCalc(left, right, random);
        }
    }

    private static NodeCalc generateBinaryNodeCalc(NodeCalc left, NodeCalc right, Random random) {
        double rand = random.nextDouble();
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

    private static NodeCalc generateValueNodeCalc(RegularTimeSeriesIndex index, List<DoubleTimeSeries> timeSeriesList, Random random) {
        if (random.nextDouble() < THRESHOLD_DOUBLE_NODE_RANDOM) {
            return new DoubleNodeCalc(random.nextDouble());
        } else {
            if (timeSeriesList.isEmpty() || random.nextDouble() < THRESHOLD_NEW_DOUBLE_TIMESERIES_RANDOM) {
                String id = "ts_" + timeSeriesList.size();
                DoubleTimeSeries ts = createDoubleTimeSeries(index, id, random);
                timeSeriesList.add(ts);
                return new TimeSeriesNameNodeCalc(id);
            } else {
                return new TimeSeriesNameNodeCalc(timeSeriesList.get(random.nextInt(timeSeriesList.size())).getMetadata().getName());
            }
        }
    }

    private static DoubleTimeSeries createDoubleTimeSeries(RegularTimeSeriesIndex index, String id, Random random) {
        return TimeSeries.createDouble(id, index, random.doubles(STEPS).toArray());
    }

    @Setup(Level.Trial)
    public void setup() throws IOException {
        // Data generation
        index = generateIndex();
        timeSeriesLists = generateTimeSeriesList(index, random);
        table = generateTimeSeriesTable(timeSeriesLists, index);
        calculatedTimeSeries = generateCalculatedTimeSeries(index, random);

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

    public List<List<DoubleTimeSeries>> getTimeSeriesLists() {
        return timeSeriesLists;
    }

    public Random getRandom() {
        return random;
    }
}
