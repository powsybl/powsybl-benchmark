/**
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.commons.serde.markdown.serialization;

import com.powsybl.benchmark.commons.serde.BenchmarkReport;
import com.powsybl.benchmark.commons.serde.BenchmarkResult;
import com.powsybl.benchmark.commons.serde.markdown.AbstractBenchmarkReportMarkdownSerializer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Dissoubray Nathan {@literal <nathan.dissoubray at rte-france.com>}
 */
public class NetworkSerializationBenchmarkReportMarkdownSerializer extends AbstractBenchmarkReportMarkdownSerializer {
    @Override
    protected String[] columnNames() {
        return new String[] {
            "Benchmark Operation",
            "XML (XIIDM)",
            "JSON (JIIDM)",
            "Binary (BIIDM)",
            "CGMES"
        };
    }

    @Override
    protected Map<String, String> getLine(List<BenchmarkResult> results) {
        Map<String, String> line = new HashMap<>(columnNames().length, 1);
        line.put("Benchmark Operation", getPrettyOperationName(results.getFirst().benchmarkName()));
        for (BenchmarkResult result : results) {
            line.put(getPrettyColumnName(result.parameters().get("format")), getFormattedScore(result));
        }
        //missing CGMES case
        line.putIfAbsent("CGMES", "—");
        return line;
    }

    @Override
    protected Map<String, Double> getLineScores(List<BenchmarkResult> results) {
        return Map.of();
    }

    @Override
    protected Function<BenchmarkResult, String> getLineSorter() {
        return BenchmarkResult::benchmarkName;
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

    private String getPrettyColumnName(String format) {
        return switch (format) {
            case "XIIDM" -> "XML (XIIDM)";
            case "JIIDM" -> "JSON (JIIDM)";
            case "BIIDM" -> "Binary (BIIDM)";
            default -> format;
        };
    }
}
