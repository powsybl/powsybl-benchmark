/**
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.commons.serde;

import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.infra.BenchmarkParams;
import org.openjdk.jmh.results.Result;
import org.openjdk.jmh.results.RunResult;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Dissoubray Nathan {@literal <nathan.dissoubray at rte-france.com>}
 */
public final class BenchmarkTestUtils {

    private BenchmarkTestUtils() {
    }

    public static RunResult mockRunResult(String benchmarkName, Map<String, String> parameters, Mode mode, double score, double scoreError, String scoreUnit) {
        return mockRunResult(benchmarkName, parameters, mode, score, scoreError, scoreUnit, Map.of());
    }

    public static RunResult mockRunResult(String benchmarkName, Map<String, String> parameters, Mode mode, double score, double scoreError, String scoreUnit, Map<String, Result<?>> secondaryResults) {
        RunResult runResult = mock(RunResult.class);
        BenchmarkParams params = mock(BenchmarkParams.class);
        Result<?> primaryResult = mock(Result.class);

        when(runResult.getParams()).thenReturn(params);
        when(runResult.getPrimaryResult()).thenReturn(primaryResult);
        when(runResult.getSecondaryResults()).thenReturn(new TreeMap<>(secondaryResults));

        when(params.getBenchmark()).thenReturn(benchmarkName);
        when(params.getMode()).thenReturn(mode);
        when(params.getParamsKeys()).thenReturn(parameters.keySet());
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            when(params.getParam(entry.getKey())).thenReturn(entry.getValue());
        }

        when(primaryResult.getScore()).thenReturn(score);
        when(primaryResult.getScoreError()).thenReturn(scoreError);
        when(primaryResult.getScoreUnit()).thenReturn(scoreUnit);

        return runResult;
    }

    public static RunResult mockRunResult(String benchmarkName, Map<String, String> parameters, double score) {
        return mockRunResult(benchmarkName, parameters, Mode.AverageTime, score, 0.1, "ms/op");
    }

    public static RunResult mockRunResult(String benchmarkName) {
        return mockRunResult(benchmarkName, Map.of("param1", "value1"), 10.5);
    }

    public static Result<?> mockResult(double score) {
        Result<?> result = mock(Result.class);
        when(result.getScore()).thenReturn(score);
        return result;
    }

    public static BenchmarkReport mockBenchmarkReport(String benchmarkClass, Collection<RunResult> runResults) {
        return new BenchmarkReport(
                benchmarkClass,
                "1.0.0",
                "1.0.0",
                "2026-08-11T10:00:00Z",
                runResults.stream().map(BenchmarkResult::new).toList()
        );
    }

    public static void assertResultsEqual(BenchmarkResult expected, BenchmarkResult actual) {
        org.junit.jupiter.api.Assertions.assertEquals(expected.benchmarkName(), actual.benchmarkName());
        org.junit.jupiter.api.Assertions.assertEquals(expected.parameters(), actual.parameters());
        org.junit.jupiter.api.Assertions.assertEquals(expected.mode(), actual.mode());
        org.junit.jupiter.api.Assertions.assertEquals(expected.score(), actual.score());
        org.junit.jupiter.api.Assertions.assertEquals(expected.scoreError(), actual.scoreError());
        org.junit.jupiter.api.Assertions.assertEquals(expected.scoreUnit(), actual.scoreUnit());
    }
}

