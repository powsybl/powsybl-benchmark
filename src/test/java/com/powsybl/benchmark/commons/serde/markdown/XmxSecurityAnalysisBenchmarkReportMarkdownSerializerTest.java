/**
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.commons.serde.markdown;

import com.powsybl.benchmark.commons.serde.BenchmarkTestUtils;
import org.openjdk.jmh.results.RunResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Dissoubray Nathan {@literal <nathan.dissoubray at rte-france.com>}
 */
class XmxSecurityAnalysisBenchmarkReportMarkdownSerializerTest extends AbstractMarkdownSerializerTest {
    private static final Map<String, String> THREAD_COUNT_1 = Map.of("threadCount", "1");
    private static final Map<String, String> THREAD_COUNT_2 = Map.of("threadCount", "2");
    private static final Map<String, String> THREAD_COUNT_4 = Map.of("threadCount", "4");
    private static final Map<String, String> THREAD_COUNT_8 = Map.of("threadCount", "8");

    @Override
    protected String getBenchClass() {
        return "XmxSecurityAnalysisBenchmark";
    }

    @Override
    protected List<String> getBenchFilenames() {
        return List.of(getBenchClass() + ".md");
    }

    @Override
    protected List<String> getResourcePaths() {
        return List.of("/xmx-security-analysis-report.md");
    }

    @Override
    protected List<String> getResourceWithBaselinePaths() {
        return List.of("/xmx-security-analysis-report-with-baseline.md");
    }

    @Override
    protected String getBaselineDirectoryPathString() {
        return "/baseline";
    }

    @Override
    protected List<RunResult> buildResults() {
        String benchClass = getBenchClass();
        List<RunResult> runResults = new ArrayList<>();

        runResults.add(BenchmarkTestUtils.mockRunResult(benchClass + ".runXmx256M", THREAD_COUNT_1, 110.0));
        addRunResults(runResults, benchClass + ".runXmx512M", 120.0, 70.0, 50.0, 30.0);
        addRunResults(runResults, benchClass + ".runXmx1G", 130.0, 80.0, 60.0, 40.0);
        addRunResults(runResults, benchClass + ".runXmx2G", 140.0, 90.0, 70.0, 50.0);
        addRunResults(runResults, benchClass + ".runXmx4G", 150.0, 100.0, 80.0, 60.0);
        addRunResults(runResults, benchClass + ".runXmx8G", 160.0, 110.0, 90.0, 70.0);
        addRunResults(runResults, benchClass + ".runXmxUndefined", 170.0, 120.0, 100.0, 80.0);

        return runResults;
    }

    private void addRunResults(List<RunResult> runResults, String xmxMethodName, double score1, double score2, double score4, double score8) {
        runResults.add(BenchmarkTestUtils.mockRunResult(xmxMethodName, THREAD_COUNT_1, score1));
        runResults.add(BenchmarkTestUtils.mockRunResult(xmxMethodName, THREAD_COUNT_2, score2));
        runResults.add(BenchmarkTestUtils.mockRunResult(xmxMethodName, THREAD_COUNT_4, score4));
        runResults.add(BenchmarkTestUtils.mockRunResult(xmxMethodName, THREAD_COUNT_8, score8));
    }
}
