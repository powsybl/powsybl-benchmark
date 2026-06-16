/*
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.serialization;

import com.powsybl.benchmark.commons.FullBenchmark;
import com.powsybl.benchmark.serialization.state.CgmesSerializationState;
import com.powsybl.iidm.network.Network;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

/**
 * @author Nicolas Rol {@literal <nicolas.rol at rte-france.com>}
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 4, time = 10)
@Measurement(iterations = 8, time = 10)
@Fork(1)
@FullBenchmark
public class CgmesSerializationBenchmark {

    @Benchmark
    public void benchmark1NetworkDeserialization(Blackhole blackhole, CgmesSerializationState serializationState) {
        Network network = Network.read(serializationState.getFilePath());
        blackhole.consume(network);
    }

    @Benchmark
    public void benchmark3NetworkFileSerialization(Blackhole blackhole, CgmesSerializationState serializationState) {
        Path path = serializationState.getOutputPath();
        serializationState.getNetwork().write(serializationState.getFormat(), serializationState.getProperties(), path);
        blackhole.consume(path);
    }
}
