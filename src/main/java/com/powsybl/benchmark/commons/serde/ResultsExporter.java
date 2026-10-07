/**
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.commons.serde;

import com.powsybl.commons.PowsyblException;
import org.openjdk.jmh.results.RunResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * This is the base class for all the results exporters.
 *
 * @author Nicolas Rol {@literal <nicolas.rol at rte-france.com>}
 */
public interface ResultsExporter {

    boolean isBenchmarkClassSupported(String benchmarkClass);

    static Collection<ResultsExporter> list(ResultsExportersLoader resultsExportersLoader) {
        Objects.requireNonNull(resultsExportersLoader);
        return resultsExportersLoader.loadExporters();
    }

    static ResultsExporter find(String benchmarkClass) {
        for (ResultsExporter exporter : list(new ResultsExportersServiceLoader())) {
            if (exporter.isBenchmarkClassSupported(benchmarkClass)) {
                return exporter;
            }
        }
        return null;
    }

    /**
     * Serialize a benchmark report into one or more tables, and write them to one or more Markdown file.
     * @param report the benchmark report
     * @param directoryPath path to the directory where the Markdown files will be written.
     *                 If multiple tables are generated, the path to each table will be <code>filePath/benchmarkName_tableName.md</code>
     * @throws IOException if the file cannot be written (path does not exist, permission denied, etc.)
     */
    static boolean export(BenchmarkReport report, Path directoryPath) throws IOException {
        ResultsExporter resultsExporter = find(report.benchmarkClass());
        if (resultsExporter != null) {
            Map<String, String> serializedReports = resultsExporter.exportReport(report);
            for (Map.Entry<String, String> table : serializedReports.entrySet()) {
                String fileName = String.format("%s%s%s.md",
                    report.benchmarkClass(),
                    table.getKey().isEmpty() ? "" : "_",
                    table.getKey());
                Files.writeString(
                    directoryPath.resolve(fileName),
                    table.getValue()
                );
            }
            return true;
        }
        return false;
    }

    /**
     * Group all {@link RunResult} into reports, then exportAll them in tables written to markdown files.
     * @param results all the run results to exportAll
     * @param directoryPath path to the directory where the Markdown files will be written
     * @throws IOException if any file cannot be written (path does not exist, permission denied, etc.)
     * @see #export(BenchmarkReport, Path)
     */
    static void exportAll(Collection<RunResult> results, Path directoryPath) throws IOException {
        List<String> failedExports = new ArrayList<>();
        for (BenchmarkReport report : BenchmarkReport.buildAllReports(results)) {
            if (!export(report, directoryPath)) {
                failedExports.add(report.benchmarkClass());
            }
        }
        if (!failedExports.isEmpty()) {
            throw new PowsyblException("No results exporter found for benchmark classes: " + String.join(", ", failedExports));
        }
    }

    Map<String, String> exportReport(BenchmarkReport report);
}
