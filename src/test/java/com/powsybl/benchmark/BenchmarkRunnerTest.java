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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Dissoubray Nathan {@literal <nathan.dissoubray at rte-france.com>}
 */
class BenchmarkRunnerTest {

    @Test
    void checkReturnedBenchmarkWithoutSuite() {
        BenchmarkRunner runner = new BenchmarkRunner();
        int exitCode = new CommandLine(runner).execute("--list", "benchA", "benchB", "benchC.namedBench");

        assertEquals(0, exitCode);
        assertThat(runner.buildBenchmarkArgs())
            .containsExactlyInAnyOrder("benchA", "benchB", "benchC.namedBench");
    }

    @Test
    void noBenchmarkProvided() {
        BenchmarkRunner runner = new BenchmarkRunner();
        int exitCode = new CommandLine(runner).execute("--list");

        assertEquals(0, exitCode);
        assertEquals(0, runner.buildBenchmarkArgs().length);
    }

    @Test
    void prependBenchmarkWithReleaseSuite() {
        BenchmarkRunner runner = new BenchmarkRunner();
        int exitCode = new CommandLine(runner).execute("--list", "--release", "anotherClass.anotherBench");

        assertEquals(0, exitCode);
        assertThat(runner.buildBenchmarkArgs())
            .hasSize(2) //JMH regex + the other bench
            .contains("anotherClass.anotherBench")
            .anyMatch(s -> s.contains("LoadFlowBenchmark"))
            .noneMatch(s -> s.contains("NetworkSerializationBenchmark"));
    }

    @Test
    void prependBenchmarkWithFullSuite() {
        BenchmarkRunner runner = new BenchmarkRunner();
        int exitCode = new CommandLine(runner).execute("--list", "--full", "anotherClass.anotherBench");

        assertEquals(0, exitCode);
        assertThat(runner.buildBenchmarkArgs())
            .hasSize(2) //JMH regex + the other bench
            .contains("anotherClass.anotherBench")
            .anyMatch(s -> s.contains("LoadFlowBenchmark"))
            .anyMatch(s -> s.contains("NetworkSerializationBenchmark"));
    }

    @Test
    void benchmarkSuiteOnly() {
        BenchmarkRunner runner = new BenchmarkRunner();
        int exitCode = new CommandLine(runner).execute("--list", "--release");

        assertEquals(0, exitCode);
        assertThat(runner.buildBenchmarkArgs())
            .hasSize(1)
            .doesNotContain("anotherClass.anotherBench")
            .anyMatch(s -> s.contains("LoadFlowBenchmark"))
            .noneMatch(s -> s.contains("NetworkSerializationBenchmark"));
    }

    @Test
    void jmhOptionsAreSeparatedFromBenchmarkNames() {
        BenchmarkRunner runner = new BenchmarkRunner();
        int exitCode = BenchmarkRunner.createCommandLine(runner)
            .execute("--list", "benchA", "benchB", "-prof", "gc", "-wi", "2");

        assertEquals(0, exitCode);
        assertThat(runner.buildBenchmarkArgs()).containsExactly("benchA", "benchB");
        assertThat(runner.getJmhOptions()).containsExactly("-prof", "gc", "-wi", "2");
    }

    @Test
    void jmhOptionsWithReleaseSuite() {
        BenchmarkRunner runner = new BenchmarkRunner();
        int exitCode = BenchmarkRunner.createCommandLine(runner)
            .execute("--list", "--release", "-prof", "gc");

        assertEquals(0, exitCode);
        assertThat(runner.buildBenchmarkArgs())
            .hasSize(1)
            .noneMatch(s -> s.contains("gc"));
        assertThat(runner.getJmhOptions()).containsExactly("-prof", "gc");
    }

    @Test
    void onlyJmhOptionsMeansNoBenchmark() {
        BenchmarkRunner runner = new BenchmarkRunner();
        int exitCode = BenchmarkRunner.createCommandLine(runner).execute("--list", "-prof", "gc");

        assertEquals(0, exitCode);
        assertEquals(0, runner.buildBenchmarkArgs().length);
    }

    @Test
    void buildJmhArgsWithoutDebug() {
        String[] result = BenchmarkRunner.buildJmhArgs(false, new String[] {"benchA"});

        assertThat(result).containsExactly("benchA");
    }
}
