/*
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.timeserie;

import com.powsybl.benchmark.commons.FullBenchmark;
import com.powsybl.benchmark.timeserie.state.SplitState;
import com.powsybl.timeseries.TimeSeries;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

/**
 * @author Samir Romdhani {@literal <samir.romdhani at rte-france.com>}
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 4, time = 10)
@Measurement(iterations = 8, time = 10)
@Fork(1)
public class SplitPerfBenchmark {

    @Benchmark
    public void split(Blackhole blackhole, SplitState state) {
        blackhole.consume(TimeSeries.split(state.getTimeSeriesList(), state.getNewChunkSize()));
    }

}
