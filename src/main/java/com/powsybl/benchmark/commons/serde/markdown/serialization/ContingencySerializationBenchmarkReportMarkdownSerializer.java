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

import java.util.List;
import java.util.function.Function;

/**
 * @author Dissoubray Nathan {@literal <nathan.dissoubray at rte-france.com>}
 */
public class ContingencySerializationBenchmarkReportMarkdownSerializer extends AbstractBenchmarkReportMarkdownSerializer {
    @Override
    protected String[] columnNames() {
        return new String[]{
            "Benchmark Operation",
            "Time (ms/op)"
        };
    }

    @Override
    protected String[] getLine(List<BenchmarkResult> results) {
        return new String[]{
            getPrettyOperationName(results.getFirst().benchmarkName()),
            getFormattedScore(results.getFirst())
        };
    }

    @Override
    protected Function<BenchmarkResult, String> getLineSorter() {
        return BenchmarkResult::benchmarkName;
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
