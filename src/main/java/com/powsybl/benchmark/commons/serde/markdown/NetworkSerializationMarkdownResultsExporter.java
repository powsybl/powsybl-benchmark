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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Dissoubray Nathan {@literal <nathan.dissoubray at rte-france.com>}
 */
@AutoService(ResultsExporter.class)
public class NetworkSerializationMarkdownResultsExporter extends AbstractMarkdownResultsExporter {

    private static final List<String> BENCHMARKS_CLASSES = List.of("NetworkSerializationBenchmark");
    private static final Map<String, String> FORMAT_COLUMN_NAMES = Map.of(
        "XIIDM", "XML (XIIDM)",
        "JIIDM", "JSON (JIIDM)",
        "BIIDM", "Binary (BIIDM)"
    );

    public NetworkSerializationMarkdownResultsExporter() {
        super(BENCHMARKS_CLASSES);
    }

    @Override
    protected String[] columnNames(List<BenchmarkResult> resultsForNetwork) {
        return getColumnNamesWithFirstColumnsAndColumnsProvider(resultsForNetwork, this::getPrettyColumnName, "Benchmark Operation");
    }

    @Override
    protected Map<String, String> getLine(List<BenchmarkResult> results) {
        Map<String, String> line = new HashMap<>(results.size() + 1, 1);
        line.put("Benchmark Operation", getPrettyOperationName(results.getFirst().benchmarkName()));
        for (BenchmarkResult result : results) {
            line.put(getPrettyColumnName(result), getFormattedScore(result));
        }
        //missing CGMES case
        line.putIfAbsent("CGMES", "—");
        return line;
    }

    @Override
    protected Function<BenchmarkResult, String> getLineSorter() {
        return BenchmarkResult::benchmarkName;
    }

    @Override
    protected String getTableName(BenchmarkReport report) {
        return report.results().getFirst().parameters().get("networkName");
    }

    @Override
    protected List<BenchmarkReport> splitReport(BenchmarkReport report) {
        //split the results by network name, create new benchmark reports with those split results
        return report.results().stream()
            .collect(Collectors.groupingBy(r -> r.parameters().get("networkName")))
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

    private String getPrettyOperationName(String fullOperationName) {
        String shortName = fullOperationName.substring(fullOperationName.lastIndexOf('.') + 1);
        return switch (shortName) {
            case "benchmark1NetworkDeserialization" -> "Deserialization";
            case "benchmark2NetworkStreamSerialization" -> "Stream Serialization";
            case "benchmark3NetworkFileSerialization" -> "File Serialization";
            case "benchmark4NetworkCopy" -> "Copy";
            default -> shortName;
        };
    }

    private String getPrettyColumnName(BenchmarkResult benchmarkResult) {
        return FORMAT_COLUMN_NAMES.getOrDefault(
            benchmarkResult.parameters().get("format"),
            benchmarkResult.parameters().get("format"));
    }
}
