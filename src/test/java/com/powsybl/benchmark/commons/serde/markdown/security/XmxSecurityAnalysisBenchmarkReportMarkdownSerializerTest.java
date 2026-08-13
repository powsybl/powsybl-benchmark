/**
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.commons.serde.markdown.security;

import com.powsybl.benchmark.commons.serde.BenchmarkTestUtils;
import com.powsybl.benchmark.commons.serde.markdown.AbstractMarkdownSerializerTest;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.results.RunResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Dissoubray Nathan {@literal <nathan.dissoubray at rte-france.com>}
 */
class XmxSecurityAnalysisBenchmarkReportMarkdownSerializerTest extends AbstractMarkdownSerializerTest {

    private final Map<String, String> threadCount1 = Map.of("threadCount", "1");
    private final Map<String, String> threadCount2 = Map.of("threadCount", "2");
    private final Map<String, String> threadCount4 = Map.of("threadCount", "4");
    private final Map<String, String> threadCount8 = Map.of("threadCount", "8");

    @Test
    void testReportToString() throws IOException {
        String benchClass = "XmxSecurityAnalysisBenchmark";
        List<RunResult> runResults = new ArrayList<>();

        // 256M - Only 1 thread present
        runResults.add(BenchmarkTestUtils.mockRunResult("runXmx256M", threadCount1, 110.0));

        addRunResults(runResults, "runXmx512M", 120.0, 70.0, 50.0, 30.0);
        addRunResults(runResults, "runXmx1G", 130.0, 80.0, 60.0, 40.0);
        addRunResults(runResults, "runXmx2G", 140.0, 90.0, 70.0, 50.0);
        addRunResults(runResults, "runXmx4G", 150.0, 100.0, 80.0, 60.0);
        addRunResults(runResults, "runXmx8G", 160.0, 110.0, 90.0, 70.0);
        addRunResults(runResults, "runXmxUndefined", 170.0, 120.0, 100.0, 80.0);

        testReportToString(benchClass, runResults, "/xmx-security-analysis-report.md");
    }

    private void addRunResults(List<RunResult> runResults, String xmxMethodName, double score1, double score2, double score4, double score8) {
        runResults.add(BenchmarkTestUtils.mockRunResult(xmxMethodName, threadCount1, score1));
        runResults.add(BenchmarkTestUtils.mockRunResult(xmxMethodName, threadCount2, score2));
        runResults.add(BenchmarkTestUtils.mockRunResult(xmxMethodName, threadCount4, score4));
        runResults.add(BenchmarkTestUtils.mockRunResult(xmxMethodName, threadCount8, score8));
    }
}
