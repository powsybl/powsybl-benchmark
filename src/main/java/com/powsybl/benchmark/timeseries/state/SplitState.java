/*
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.timeseries.state;

import com.powsybl.timeseries.*;
import org.openjdk.jmh.annotations.*;

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
    private List<DoubleTimeSeries> timeSeriesList;

    @Setup(Level.Trial)
    public void setup() {
        TimeSeriesIndex index = new RegularTimeSeriesIndex(Instant.ofEpochMilli(0), Instant.ofEpochMilli(size - 1L), Duration.ofMillis(1));
        TimeSeriesMetadata metadata = new TimeSeriesMetadata("ts1", TimeSeriesDataType.DOUBLE, Collections.emptyMap(), index);

        List<DoubleDataChunk> chunks = switch (layout) {
            // single large uncompressed chunk
            case "SINGLE_CHUNK" -> {
                double[] values = new double[size];
                Arrays.fill(values, 1.0);
                yield List.of(new UncompressedDoubleDataChunk(0, values));
            }
            // many single uncompressed chunks
            case "FRAGMENTED" -> {
                List<DoubleDataChunk> list = new ArrayList<>(size);
                for (int i = 0; i < size; i++) {
                    list.add(new UncompressedDoubleDataChunk(i, new double[] {i}));
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
}
