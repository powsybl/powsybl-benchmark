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

/**
 * @author Nicolas Rol {@literal <nicolas.rol at rte-france.com>}
 */
@AutoService(ResultsExporter.class)
public class TimeSeriesNodePrintMarkdownExporter extends AbstractMarkdownResultsExporter {

    private static final List<String> BENCHMARKS_CLASSES = List.of("NodePrintBenchmark");

    public TimeSeriesNodePrintMarkdownExporter() {
        super(BENCHMARKS_CLASSES);
    }

    @Override
    protected String[] columnNames(List<BenchmarkResult> results) {
        return getColumnNamesWithFirstColumnsAndColumnsProvider(results, this::getColumnName, "Operation");
    }

    @Override
    protected Map<String, String> getLine(List<BenchmarkResult> results) {
        Map<String, String> line = new HashMap<>(results.size() + 1, 1);
        line.put("Operation", getPrettyOperationName(results.getFirst().benchmarkName()));
        results.forEach(result -> line.put(getColumnName(result), getFormattedScoreAndUnit(result)));
        return line;
    }

    protected String getColumnName(BenchmarkResult benchmarkResult) {
        return benchmarkResult.parameters().get("size");
    }

    private String getPrettyOperationName(String fullOperationName) {
        String shortName = fullOperationName.substring(fullOperationName.lastIndexOf('.') + 1);
        if ("print".equals(shortName)) {
            return "Node printing";
        }
        return shortName;
    }
}
