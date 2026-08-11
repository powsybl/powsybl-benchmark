/**
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark;

import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Dissoubray Nathan {@literal <nathan.dissoubray at rte-france.com>}
 */
class BenchmarkRunnerTest {

    @Test
    void checkReturnedBenchmarkWithoutSuite() throws Exception {
        BenchmarkRunner runner = new BenchmarkRunner();
        int exitCode = new CommandLine(runner).execute("--list", "benchA", "benchB", "benchC.namedBench");

        assertEquals(0, exitCode);
        assertThat(buildBenchmarkArgs(runner))
            .containsExactlyInAnyOrder("benchA", "benchB", "benchC.namedBench");
    }

    @Test
    void noBenchmarkProvided() throws Exception {
        BenchmarkRunner runner = new BenchmarkRunner();
        int exitCode = new CommandLine(runner).execute("--list");

        assertEquals(0, exitCode);
        assertNull(buildBenchmarkArgs(runner));
    }

    @Test
    void prependBenchmarkWithReleaseSuite() throws Exception {
        BenchmarkRunner runner = new BenchmarkRunner();
        int exitCode = new CommandLine(runner).execute("--list", "--release", "anotherClass.anotherBench");

        assertEquals(0, exitCode);
        assertThat(buildBenchmarkArgs(runner))
            .hasSize(2) //JMH regex + the other bench
            .contains("anotherClass.anotherBench")
            .anyMatch(s -> s.contains("LoadFlowBenchmark"));
    }

    @Test
    void prependBenchmarkWithFullSuite() throws Exception {
        BenchmarkRunner runner = new BenchmarkRunner();
        int exitCode = new CommandLine(runner).execute("--list", "--full", "anotherClass.anotherBench");

        assertEquals(0, exitCode);
        assertThat(buildBenchmarkArgs(runner))
            .hasSize(2) //JMH regex + the other bench
            .contains("anotherClass.anotherBench")
            .anyMatch(s -> s.contains("LoadFlowBenchmark"))
            .anyMatch(s -> s.contains("NetworkSerializationBenchmark"));
    }

    //TODO is there any better way to test this than to use reflection ?
    private static String[] buildBenchmarkArgs(BenchmarkRunner runner) throws Exception {
        Method method = BenchmarkRunner.class.getDeclaredMethod("buildBenchmarkArgs");
        method.setAccessible(true);
        return (String[]) method.invoke(runner);
    }
}
