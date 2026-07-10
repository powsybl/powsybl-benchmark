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
import java.util.Collections;
import java.util.List;

/**
 * @author Samir Romdhani {@literal <samir.romdhani at rte-france.com>}
 */
@State(Scope.Thread)
public class SplitState {

    @Param({"100000", "200000", "400000"})
    private int size;
    private List<DoubleTimeSeries> timeSeriesList;
    private int newChunkSize;

    @Setup(Level.Trial)
    public void setup() {
        TimeSeriesIndex index = new RegularTimeSeriesIndex(Instant.ofEpochMilli(0), Instant.ofEpochMilli(size - 1), Duration.ofMillis(1));
        TimeSeriesMetadata metadata = new TimeSeriesMetadata("ts1", TimeSeriesDataType.DOUBLE, Collections.emptyMap(), index);
        DoubleDataChunk[] chunks = new DoubleDataChunk[size];
        for (int i = 0; i < chunks.length; i++) {
            chunks[i] = new UncompressedDoubleDataChunk(i, new double[] {i});
        }
        timeSeriesList = Collections.singletonList(new StoredDoubleTimeSeries(metadata, chunks));
        newChunkSize = size;
    }

    public List<DoubleTimeSeries> getTimeSeriesList() {
        return timeSeriesList;
    }

    public int getNewChunkSize() {
        return newChunkSize;
    }
}
