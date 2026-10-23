/**
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.timeseries;

import com.powsybl.benchmark.timeseries.state.TimeSeriesState;
import com.powsybl.timeseries.TimeSeries;
import com.powsybl.timeseries.TimeSeriesTable;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.powsybl.benchmark.timeseries.state.TimeSeriesState.generateTimeSeriesTable;

/**
 * @author Nicolas Rol {@literal <nicolas.rol at rte-france.com>}
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class TimeSeriesBenchmark {

    @Benchmark
    public void benchmark1GenerateTimeSeries(Blackhole blackhole, TimeSeriesState timeSeriesState) {
        TimeSeriesTable timeSeriesTable = generateTimeSeriesTable(timeSeriesState.getIndex());
        blackhole.consume(timeSeriesTable);
    }

    @Benchmark
    public void benchmark2ParseCsv(Blackhole blackhole, TimeSeriesState timeSeriesState) {
        Map<Integer, List<TimeSeries>> timeseriesMap = TimeSeries.parseCsv(timeSeriesState.getFilePath());
        blackhole.consume(timeseriesMap);
    }

    @Benchmark
    public void benchmark3WriteCsv(Blackhole blackhole, TimeSeriesState timeSeriesState) {
        Path filePath = timeSeriesState.getOutputPath();
        timeSeriesState.getTable().writeCsv(filePath);
        blackhole.consume(filePath);
    }
}
