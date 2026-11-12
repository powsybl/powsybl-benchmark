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
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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
        assertNull(runner.buildBenchmarkArgs());
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
    void jmhOptionsAreSplitOnWhitespace() {
        BenchmarkRunner runner = new BenchmarkRunner();
        CommandLine cmd = new CommandLine(runner);
        cmd.parseArgs("--jmh-args=-prof gc", "benchA");

        String[] jmhOptions = cmd.getParseResult().matchedOption("--jmh-args").getValue();

        assertArrayEquals(new String[] {"-prof", "gc"}, jmhOptions);
    }

    @Test
    void jmhOptionsPassedToBuildJmhArgs() {
        BenchmarkRunner runner = new BenchmarkRunner();
        CommandLine cmd = new CommandLine(runner);
        cmd.parseArgs("--debug", "--jmh-args=-prof gc", "benchA");

        String[] jmhOptions = cmd.getParseResult().matchedOption("--jmh-args").getValue();
        String[] result = BenchmarkRunner.buildJmhArgs(true, new String[] {"benchA"}, jmhOptions);

        assertThat(result).containsExactly("benchA", "-prof", "gc", "-f", "0");
    }

    @Test
    void jmhOptionsWithSeparateValueArg() {
        BenchmarkRunner runner = new BenchmarkRunner();
        CommandLine cmd = new CommandLine(runner);
        cmd.parseArgs("--jmh-args", "-prof gc", "benchA");

        String[] jmhOptions = cmd.getParseResult().matchedOption("--jmh-args").getValue();

        assertArrayEquals(new String[] {"-prof", "gc"}, jmhOptions);
    }

    @Test
    void buildJmhArgsWithDebugAndOptions() {
        String[] result = BenchmarkRunner.buildJmhArgs(true, new String[] {"benchA"}, new String[] {"-prof", "gc", " "});

        assertThat(result).containsExactly("benchA", "-prof", "gc", "-f", "0");
    }

    @Test
    void buildJmhArgsWithoutDebug() {
        String[] result = BenchmarkRunner.buildJmhArgs(false, new String[] {"benchA"}, new String[0]);

        assertThat(result).containsExactly("benchA");
    }
}
