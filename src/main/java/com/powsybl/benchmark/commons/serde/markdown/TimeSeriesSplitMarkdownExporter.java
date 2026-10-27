/**
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.commons.serde.markdown;

import com.google.auto.service.AutoService;
import com.powsybl.benchmark.commons.serde.BenchmarkResult;
import com.powsybl.benchmark.commons.serde.ResultsExporter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Nicolas Rol {@literal <nicolas.rol at rte-france.com>}
 */
@AutoService(ResultsExporter.class)
public class TimeSeriesSplitMarkdownExporter extends AbstractMarkdownResultsExporter {

    private static final List<String> BENCHMARKS_CLASSES = List.of("SplitPerfBenchmark");

    public TimeSeriesSplitMarkdownExporter() {
        super(BENCHMARKS_CLASSES);
    }

    @Override
    protected String[] columnNames(List<BenchmarkResult> results) {
        return getColumnNamesWithFirstColumnsAndColumnsProvider(results, this::getColumnName, "Layout", "newChunkSize (k)");
    }

    @Override
    protected Function<BenchmarkResult, String> getLineSorter() {
        return result -> result.parameters().get("layout") + "_" + result.parameters().get("newChunkSize");
    }

    @Override
    protected Map<String, String> getLine(List<BenchmarkResult> results) {
        Map<String, String> line = new HashMap<>(results.size() + 1, 1);
        line.put("Layout", results.getFirst().parameters().get("layout"));
        line.put("newChunkSize (k)", results.getFirst().parameters().get("newChunkSize"));
        results.forEach(result -> line.put(getColumnName(result), getFormattedScoreAndUnit(result, 3)));
        return line;
    }

    @Override
    protected Map<String, Double> getLineScores(List<BenchmarkResult> results) {
        return getLineData(results, BenchmarkResult::score);
    }

    @Override
    protected Map<String, Double> getLineScoresError(List<BenchmarkResult> results) {
        return getLineData(results, BenchmarkResult::scoreError);
    }

    protected String getColumnName(BenchmarkResult benchmarkResult) {
        return "size = " + benchmarkResult.parameters().get("size");
    }

    private <T> Map<String, T> getLineData(List<BenchmarkResult> results, Function<BenchmarkResult, T> getter) {
        Map<String, T> lineScores = new HashMap<>(results.size() + 1, 1);
        for (BenchmarkResult result : results) {
            lineScores.put(getColumnName(result), getter.apply(result));
        }
        return lineScores;
    }
}
