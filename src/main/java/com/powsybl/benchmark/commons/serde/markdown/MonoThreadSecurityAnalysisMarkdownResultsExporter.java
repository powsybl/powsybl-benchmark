/**
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.commons.serde.markdown;

import com.google.auto.service.AutoService;
import com.powsybl.benchmark.commons.serde.BenchmarkReport;
import com.powsybl.benchmark.commons.serde.BenchmarkResult;
import com.powsybl.benchmark.commons.serde.ResultsExporter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Nicolas Rol {@literal <nicolas.rol at rte-france.com>}
 */
@AutoService(ResultsExporter.class)
public class MonoThreadSecurityAnalysisMarkdownResultsExporter extends ContingenciesMarkdownResultsExporter {

    private static final List<String> BENCHMARK_CLASSES = List.of("MonoThreadSecurityAnalysisBenchmark");

    public MonoThreadSecurityAnalysisMarkdownResultsExporter() {
        super(BENCHMARK_CLASSES);
    }

    @Override
    protected List<BenchmarkReport> splitReport(BenchmarkReport report) {
        // Split the results by method, so that each method has its own table in the markdown report
        return report.results().stream()
            .collect(Collectors.groupingBy(BenchmarkResult::benchmarkName))
            .values().stream()
            .map(l -> new BenchmarkReport(
                report.benchmarkClass(),
                report.powsyblCoreVersion(),
                report.openLoadFlowVersion(),
                report.datetime(),
                l
            ))
            .toList();
    }

    @Override
    protected String getTableName(BenchmarkReport report) {
        String benchmarkName = report.results().getFirst().benchmarkName();
        return benchmarkName.split("\\.")[benchmarkName.split("\\.").length - 1];
    }
}
