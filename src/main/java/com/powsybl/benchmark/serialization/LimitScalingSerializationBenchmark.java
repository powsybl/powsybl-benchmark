/**
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.serialization;

import com.powsybl.benchmark.commons.FullBenchmark;
import com.powsybl.benchmark.serialization.state.LimitScalingSerializationState;
import com.powsybl.security.limitscaling.LimitScalingList;
import org.openjdk.jmh.annotations.*;
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
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(2)
@FullBenchmark
public class LimitScalingSerializationBenchmark {

    @Benchmark
    public void limitScaling1Deserializing(Blackhole blackhole, LimitScalingSerializationState serializationState) throws IOException {
        LimitScalingList list;
        try (InputStream inputStream = Files.newInputStream(serializationState.getFilePath())) {
            list = serializationState.reader().readValue(inputStream);
        }
        blackhole.consume(list);
    }

    @Benchmark
    public void limitScaling2DeserializingFromBytes(Blackhole blackhole, LimitScalingSerializationState serializationState) throws IOException {
        LimitScalingList list = serializationState.reader().readValue(serializationState.getFileContent());
        blackhole.consume(list);
    }

    @Benchmark
    public void limitScaling3Writing(Blackhole blackhole, LimitScalingSerializationState serializationState) throws IOException {
        try (OutputStream outputStream = OutputStream.nullOutputStream()) {
            serializationState.writer().writeValue(outputStream, serializationState.getData());
            blackhole.consume(outputStream);
        }
    }

    @Benchmark
    public void limitScaling4BufferedWriting(Blackhole blackhole, LimitScalingSerializationState serializationState) throws IOException {
        try (Writer writer = Writer.nullWriter()) {
            serializationState.writer().writeValue(writer, serializationState.getData());
            blackhole.consume(writer);
        }
    }
}
