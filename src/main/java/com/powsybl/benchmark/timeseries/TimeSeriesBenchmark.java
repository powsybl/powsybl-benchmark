/**
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.timeseries;

import com.powsybl.benchmark.timeseries.state.TimeSeriesState;
import com.powsybl.timeseries.CalculatedTimeSeries;
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

import static com.powsybl.benchmark.timeseries.state.TimeSeriesState.generateCalculatedTimeSeries;
import static com.powsybl.benchmark.timeseries.state.TimeSeriesState.generateTimeSeriesTable;

/**
 * @author Nicolas Rol {@literal <nicolas.rol at rte-france.com>}
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(1)
public class TimeSeriesBenchmark {

    @Benchmark
    @Warmup(iterations = 2, time = 2, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 4, time = 2, timeUnit = TimeUnit.SECONDS)
    public void benchmark1GenerateTimeSeriesTable(Blackhole blackhole, TimeSeriesState timeSeriesState) {
        TimeSeriesTable timeSeriesTable = generateTimeSeriesTable(timeSeriesState.getIndex());
        blackhole.consume(timeSeriesTable);
    }

    @Benchmark
    @Warmup(iterations = 3, time = 15, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 5, time = 15, timeUnit = TimeUnit.SECONDS)
    public void benchmark2ParseCsv(Blackhole blackhole, TimeSeriesState timeSeriesState) {
        Map<Integer, List<TimeSeries>> timeseriesMap = TimeSeries.parseCsv(timeSeriesState.getCsvFilePath());
        blackhole.consume(timeseriesMap);
    }

    @Benchmark
    @Warmup(iterations = 3, time = 15, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 5, time = 15, timeUnit = TimeUnit.SECONDS)
    public void benchmark3WriteCsv(Blackhole blackhole, TimeSeriesState timeSeriesState) {
        Path filePath = timeSeriesState.getCsvOutputPath();
        timeSeriesState.getTable().writeCsv(filePath);
        blackhole.consume(filePath);
    }

    @Benchmark
    @Warmup(iterations = 2, time = 10, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 4, time = 10, timeUnit = TimeUnit.SECONDS)
    public void benchmark4GenerateCalculatedTimeSeries(Blackhole blackhole, TimeSeriesState timeSeriesState) {
        CalculatedTimeSeries generateCalculatedTimeSeries = generateCalculatedTimeSeries(timeSeriesState.getIndex());
        blackhole.consume(generateCalculatedTimeSeries);
    }

    @Benchmark
    @Warmup(iterations = 2, time = 10, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 4, time = 10, timeUnit = TimeUnit.SECONDS)
    public void benchmark5ParseJson(Blackhole blackhole, TimeSeriesState timeSeriesState) {
        List<TimeSeries> timeseriesMap = TimeSeries.parseJson(timeSeriesState.getJsonFilePath());
        blackhole.consume(timeseriesMap);
    }

    @Benchmark
    @Warmup(iterations = 2, time = 10, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 4, time = 10, timeUnit = TimeUnit.SECONDS)
    public void benchmark6WriteJson(Blackhole blackhole, TimeSeriesState timeSeriesState) {
        Path jsonFilePath = timeSeriesState.getJsonOutputPath();
        TimeSeries.writeJson(jsonFilePath, List.of(timeSeriesState.getCalculatedTimeSeries()));
        blackhole.consume(jsonFilePath);
    }
}
