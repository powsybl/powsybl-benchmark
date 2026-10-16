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

import java.util.List;
import java.util.Map;

/**
 * @author Dissoubray Nathan {@literal <nathan.dissoubray at rte-france.com>}
 */
@AutoService(ResultsExporter.class)
public class ContingencySerializationMarkdownResultsExporter extends AbstractMarkdownResultsExporter {

    private static final List<String> BENCHMARKS_CLASSES = List.of("ContingencySerializationBenchmark");
    private static final String TIME_STRING = "Time (ms/op)";

    public ContingencySerializationMarkdownResultsExporter() {
        super(BENCHMARKS_CLASSES);
    }

    @Override
    protected String[] columnNames(List<BenchmarkResult> resultsForNetwork) {
        return new String[] {
            "Benchmark Operation",
            TIME_STRING
        };
    }

    @Override
    protected Map<String, String> getLine(List<BenchmarkResult> results) {
        return Map.of(
            "Benchmark Operation", getPrettyOperationName(results.getFirst().benchmarkName()),
            TIME_STRING, getFormattedScore(results.getFirst(), 3)
        );
    }

    @Override
    protected Map<String, Double> getLineScores(List<BenchmarkResult> results) {
        return Map.of(TIME_STRING, results.getFirst().score());
    }

    private String getPrettyOperationName(String fullOperationName) {
        String shortName = fullOperationName.substring(fullOperationName.lastIndexOf('.') + 1);
        return switch (shortName) {
            case "benchmark1Parsing" -> "Parsing";
            case "benchmark2ParsingFromBytes" -> "Parsing from bytes";
            case "benchmark3JustReading" -> "Just reading";
            case "benchmark4ReadingToString" -> "Reading to string";
            case "benchmark5Writing" -> "Writing";
            case "benchmark6BufferedWriting" -> "Buffered writing";
            default -> shortName;
        };
    }
}
