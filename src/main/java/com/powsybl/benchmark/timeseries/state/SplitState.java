/*
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.timeseries.state;

import com.powsybl.timeseries.CompressedDoubleDataChunk;
import com.powsybl.timeseries.DoubleDataChunk;
import com.powsybl.timeseries.DoubleTimeSeries;
import com.powsybl.timeseries.RegularTimeSeriesIndex;
import com.powsybl.timeseries.StoredDoubleTimeSeries;
import com.powsybl.timeseries.TimeSeriesDataType;
import com.powsybl.timeseries.TimeSeriesIndex;
import com.powsybl.timeseries.TimeSeriesMetadata;
import com.powsybl.timeseries.UncompressedDoubleDataChunk;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Samir Romdhani {@literal <samir.romdhani at rte-france.com>}
 */
@State(Scope.Thread)
public class SplitState {

    @Param({"1000", "10000", "100000"})
    private int size;

    @Param({"100", "1000"})
    private int newChunkSize;

    @Param({"SINGLE_CHUNK", "FRAGMENTED"})
    private String layout;

    @Param({"false", "true"})
    private boolean compressed;

    private List<DoubleTimeSeries> timeSeriesList;

    @Setup(Level.Trial)
    public void setup() {
        TimeSeriesIndex index = new RegularTimeSeriesIndex(Instant.ofEpochMilli(0), Instant.ofEpochMilli(size - 1L), Duration.ofMillis(1));
        TimeSeriesMetadata metadata = new TimeSeriesMetadata("ts1", TimeSeriesDataType.DOUBLE, Collections.emptyMap(), index);

        List<DoubleDataChunk> chunks = switch (layout) {
            case "SINGLE_CHUNK" -> {
                if (compressed) {
                    yield List.of(new CompressedDoubleDataChunk(0, size, new double[] {1.0}, new int[] {size}));
                } else {
                    double[] values = new double[size];
                    Arrays.fill(values, 1.0);
                    yield List.of(new UncompressedDoubleDataChunk(0, values));
                }
            }
            case "FRAGMENTED" -> {
                List<DoubleDataChunk> list = new ArrayList<>(size);
                for (int i = 0; i < size; i++) {
                    list.add(compressed
                            ? new CompressedDoubleDataChunk(i, 1, new double[] {i}, new int[] {1})
                            : new UncompressedDoubleDataChunk(i, new double[] {i}));
                }
                yield list;
            }
            default -> throw new IllegalArgumentException("Unknown layout: " + layout);
        };
        timeSeriesList = Collections.singletonList(new StoredDoubleTimeSeries(metadata, chunks));
    }

    public List<DoubleTimeSeries> getTimeSeriesList() {
        return timeSeriesList;
    }

    public int getNewChunkSize() {
        return newChunkSize;
    }

    public boolean isCompressed() {
        return compressed;
    }
}
