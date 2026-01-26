/*
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark;

import com.powsybl.benchmark.state.IidmSerializationState;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.serde.NetworkSerDe;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

/**
 * @author Nicolas Rol {@literal <nicolas.rol at rte-france.com>}
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 4, time = 2)
@Measurement(iterations = 8, time = 2)
@Fork(2)
public class NetworkSerializationBenchmark {

    @Benchmark
    public void benchmark1NetworkDeserialization(Blackhole blackhole, IidmSerializationState serializationState) {
        Network network = Network.read(serializationState.getFilePath());
        blackhole.consume(network);
    }

    @Benchmark
    public void benchmark2NetworkStreamSerialization(Blackhole blackhole, IidmSerializationState serializationState) throws IOException {
        try (OutputStream os = OutputStream.nullOutputStream()) {
            NetworkSerDe.write(serializationState.getNetwork(), serializationState.getExportOptions(), os);
            blackhole.consume(os);
        }
    }

    @Benchmark
    public void benchmark3NetworkFileSerialization(Blackhole blackhole, IidmSerializationState serializationState) {
        Path path = serializationState.getOutputPath();
        serializationState.getNetwork().write(serializationState.getFormat(), serializationState.getProperties(), path);
        blackhole.consume(path);
    }

    @Benchmark
    public void benchmark4NetworkCopy(Blackhole blackhole, IidmSerializationState serializationState) {
        if (serializationState.getTreeDataFormat() == null) {
            // Should not happen
            return;
        }
        Network copy = NetworkSerDe.copy(serializationState.getNetwork(), serializationState.getTreeDataFormat());
        blackhole.consume(copy);
    }
}
