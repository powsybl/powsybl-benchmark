/**
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.commons.serde;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.results.RunResult;

import java.util.List;

import static com.powsybl.benchmark.commons.serde.BenchmarkTestUtils.assertResultsEqual;
import static com.powsybl.benchmark.commons.serde.BenchmarkTestUtils.mockRunResult;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Dissoubray Nathan {@literal <nathan.dissoubray at rte-france.com>}
 */
class BenchmarkReportTest {

    @Test
    void testConstructorFromRunResults() {
        String className = "MyBenchmarkClass";
        RunResult rr1 = mockRunResult(className + ".method1");
        RunResult rr2 = mockRunResult(className + ".method2");

        BenchmarkReport report = new BenchmarkReport(className, List.of(rr1, rr2));

        assertEquals(className, report.benchmarkClass());
        assertEquals("7.2.1", report.powsyblCoreVersion());
        assertEquals("2.2.1", report.openLoadFlowVersion());
        assertNotNull(report.datetime());
        assertEquals(2, report.results().size());
        assertResultsEqual(new BenchmarkResult(rr1), report.results().get(0));
        assertResultsEqual(new BenchmarkResult(rr2), report.results().get(1));
    }

    @Test
    void testBuildAllReports() {
        RunResult rr1 = mockRunResult("com.powsybl.ClassA.method1");
        RunResult rr2 = mockRunResult("com.powsybl.ClassA.method2");
        RunResult rr3 = mockRunResult("com.powsybl.ClassB.method3");

        List<BenchmarkReport> reports = BenchmarkReport.buildAllReports(List.of(rr1, rr2, rr3));

        Assertions.assertThat(reports).hasSize(2)
            .extracting(
                r -> r.results().stream().map(BenchmarkResult::benchmarkName).toList()
            ).containsExactlyInAnyOrder(
                List.of("com.powsybl.ClassA.method1", "com.powsybl.ClassA.method2"),
                List.of("com.powsybl.ClassB.method3")
        );
    }
}
