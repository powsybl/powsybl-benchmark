/*
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.iidm;

import com.powsybl.iidm.network.*;
import com.powsybl.iidm.network.test.FourSubstationsNodeBreakerWithExtensionsFactory;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/**
 * @author Samir Romdhani {@literal <samir.romdhani at rte-france.com>}
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 5)
@Measurement(iterations = 8)
@Fork(value = 2, warmups = 1)
public class NetworkConnectableBenchmark {

    private static final Network NETWORK_TEST1 = createNetwork();
    private static final Network NETWORK_TEST2 = FourSubstationsNodeBreakerWithExtensionsFactory.create();

    @Benchmark
    public void getConnectableCallIndexGetAll() {
        NETWORK_TEST2.getConnectableStream(Load.class).count();
    }

    @Benchmark
    public void connectableCallIndexGetAllAssignable() {
        NETWORK_TEST2.getConnectableStream2(Load.class).count();
    }

    private static Network createNetwork() {
        Network network = Network.create("test", "test");
        Substation s = network.newSubstation().setId("Substation").add();
        VoltageLevel vl = s.newVoltageLevel()
                .setId("VL")
                .setNominalV(400)
                .setTopologyKind(TopologyKind.NODE_BREAKER)
                .add();
        vl.getNodeBreakerView().newBusbarSection().setId("BBS_A").setNode(1).add();
        vl.newLoad().setId("Load_A").setNode(2).setP0(10).setQ0(-5).add();
        vl.getNodeBreakerView().newBreaker()
                .setId("switch_A")
                .setNode1(1)
                .setNode2(2)
                .setOpen(false)
                .add();
        vl.getNodeBreakerView().newBusbarSection().setId("BBS_B").setNode(4).add();
        vl.newLoad().setId("Load_B").setNode(5).setP0(4).setQ0(-5).add();
        vl.getNodeBreakerView().newBreaker()
                .setId("switch_B")
                .setNode1(4)
                .setNode2(5)
                .setOpen(false)
                .add();
        vl.newLoad().setId("Load").setNode(3).setP0(5).setQ0(-2).add();
        vl.getNodeBreakerView().newBreaker().setId("switch_AL").setNode1(1).setNode2(3).setOpen(false).add();
        vl.getNodeBreakerView().newBreaker().setId("switch_BL").setNode1(3).setNode2(4).setOpen(false).add();
        return network;
    }
}
