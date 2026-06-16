/*
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.serialization;

import com.powsybl.benchmark.commons.FullBenchmark;
import com.powsybl.benchmark.serialization.state.ContingenciesSerializationState;
import com.powsybl.contingency.list.ContingencyList;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

/**
 * @author Nicolas Rol {@literal <nicolas.rol at rte-france.com>}
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 4, time = 1)
@Measurement(iterations = 8, time = 1)
@Fork(3)
@FullBenchmark
public class ContingencySerializationBenchmark {

    @Benchmark
    public void benchmark1Parsing(Blackhole blackhole, ContingenciesSerializationState serializationState) throws IOException {
        ContingencyList list;
        try (InputStream inputStream = Files.newInputStream(serializationState.getFilePath())) {
            list = serializationState.reader().readValue(inputStream);
        }
        blackhole.consume(list);
    }

    @Benchmark
    public void benchmark2ParsingFromBytes(Blackhole blackhole, ContingenciesSerializationState serializationState) throws IOException {
        ContingencyList list = serializationState.reader().readValue(serializationState.getFileContent());
        blackhole.consume(list);
    }

    @Benchmark
    public void benchmark3JustReading(Blackhole blackhole, ContingenciesSerializationState serializationState) throws IOException {
        byte[] list = Files.readAllBytes(serializationState.getFilePath());
        blackhole.consume(list);
    }

    @Benchmark
    public void benchmark4ReadingToString(Blackhole blackhole, ContingenciesSerializationState serializationState) throws IOException {
        String list = Files.readString(serializationState.getFilePath());
        blackhole.consume(list);
    }

    @Benchmark
    public void benchmark5Writing(Blackhole blackhole, ContingenciesSerializationState serializationState) throws IOException {
        try (OutputStream outputStream = OutputStream.nullOutputStream()) {
            serializationState.writer().writeValue(outputStream, serializationState.getData());
            blackhole.consume(outputStream);
        }
    }

    @Benchmark
    public void benchmark6BufferedWriting(Blackhole blackhole, ContingenciesSerializationState serializationState) throws IOException {
        try (Writer writer = Writer.nullWriter()) {
            serializationState.writer().writeValue(writer, serializationState.getData());
            blackhole.consume(writer);
        }
    }
}
