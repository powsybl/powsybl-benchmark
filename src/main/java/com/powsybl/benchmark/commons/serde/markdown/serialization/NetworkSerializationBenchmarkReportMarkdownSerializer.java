/**
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.commons.serde.markdown.serialization;

import com.powsybl.benchmark.commons.serde.BenchmarkResult;
import com.powsybl.benchmark.commons.serde.markdown.AbstractBenchmarkReportMarkdownSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

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
    protected String[] getLine(List<BenchmarkResult> results) {
        List<String> line = new ArrayList<>(columnNames().length);
        line.add(getPrettyOperationName(results.getFirst().benchmarkName()));
        line.addAll(results.stream()
            .map(r -> String.format("%.2f", r.score()))
            .toList()
        );
        return line.toArray(new String[0]);
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
}
