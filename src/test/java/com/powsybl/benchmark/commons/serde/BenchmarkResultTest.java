/**
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.commons.serde;

import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.results.RunResult;

import java.util.Map;

import static com.powsybl.benchmark.commons.serde.BenchmarkTestUtils.assertResultsEqual;
import static com.powsybl.benchmark.commons.serde.BenchmarkTestUtils.mockRunResult;

/**
 * @author Dissoubray Nathan {@literal <nathan.dissoubray at rte-france.com>}
 */
class BenchmarkResultTest {

    @Test
    void testConstructorFromRunResult() {
        Map<String, String> paramsMap = Map.of("param1", "value1", "param2", "value2");
        RunResult runResult = mockRunResult("myBenchmark", paramsMap, Mode.AverageTime, 10.5, 0.1, "ms/op");

        BenchmarkResult benchmarkResult = new BenchmarkResult(runResult);

        BenchmarkResult expected = new BenchmarkResult(
            "myBenchmark",
            paramsMap,
            Mode.AverageTime,
            10.5,
            0.1,
            "ms/op"
        );
        assertResultsEqual(expected, benchmarkResult);
    }
}
