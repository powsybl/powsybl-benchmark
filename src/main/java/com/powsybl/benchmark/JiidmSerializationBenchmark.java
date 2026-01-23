/*
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark;

import com.powsybl.benchmark.state.JiidmSerializationState;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;

/**
 * @author Nicolas Rol {@literal <nicolas.rol at rte-france.com>}
 */
public class JiidmSerializationBenchmark extends AbstractIidmSerializationBenchmark<JiidmSerializationState> {

    @Benchmark
    public void networkDeserialization(Blackhole blackhole, JiidmSerializationState serializationState) {
        doDeserialization(blackhole, serializationState);
    }

    @Benchmark
    public void networkSerialization(Blackhole blackhole, JiidmSerializationState serializationState) throws IOException {
        doSerialization(blackhole, serializationState);
    }

    @Benchmark
    public void networkCopy(Blackhole blackhole, JiidmSerializationState serializationState) {
        doNetworkCopy(blackhole, serializationState);
    }
}
