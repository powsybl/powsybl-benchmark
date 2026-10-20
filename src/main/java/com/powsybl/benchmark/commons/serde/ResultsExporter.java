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
import java.util.stream.Collectors;

/**
 * This is the base class for all the results exporters.
 *
 * @author Nicolas Rol {@literal <nicolas.rol at rte-france.com>}
 * @author Dissoubray Nathan {@literal <nathan.dissoubray at rte-france.com>}
 */
public interface ResultsExporter {

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
     *
     * @param report        the benchmark report
     * @param markdownDirectoryOutputPath path to the directory where the Markdown files will be written.
     *                      If multiple tables are generated, the path to each table will be <code>filePath/benchmarkName_tableName.md</code>
     * @param baselineReportDirectoryInputPath path to the directory where the baseline benchmark report that will be used for baseline comparison is stored. Can be null.
     * @return true if the report was successfully exported, false otherwise
     * @throws IOException if the file cannot be written (path does not exist, permission denied, etc.)
     */
    static boolean export(BenchmarkReport report, Path markdownDirectoryOutputPath, Path baselineReportDirectoryInputPath, String timestamp) throws IOException {
        ResultsExporter resultsExporter = find(report.benchmarkClass());
        if (resultsExporter != null) {
            BenchmarkReport baselineReport = null;
            if (baselineReportDirectoryInputPath != null && Files.exists(baselineReportDirectoryInputPath)) {
                baselineReport = BenchmarkReportJsonSerDe.readReports(
                    SerDeUtil.getMatchingFileFromPath(
                        baselineReportDirectoryInputPath,
                        new SerDeFilePredicate(report.benchmarkClass(), timestamp + ".json", report.datetime()),
                        false)
                        .getFirst())
                    .getFirst();
            }
            Map<String, String> serializedReports = resultsExporter.exportReport(report, baselineReport);
            for (Map.Entry<String, String> table : serializedReports.entrySet()) {
                String fileName = String.format("%s%s%s_%s.md",
                    report.benchmarkClass(),
                    table.getKey().isEmpty() ? "" : "_",
                    table.getKey(),
                    report.datetime());
                Files.writeString(
                    markdownDirectoryOutputPath.resolve(fileName),
                    table.getValue()
                );
            }
            return true;
        }
        return false;
    }

    /**
     * Group all {@link RunResult} into reports, then exportAll them in tables written to markdown files.
     *
     * @param reports       all the run reports to be exported
     * @param markdownDirectoryOutputPath path to the directory where the Markdown files will be written
     * @param baselineReportDirectoryInputPath path to the directory where the serialized benchmark reports used as baseline for comparison are located
     * @param timestamp only use serialized benchmark results file whose name matches this timestamp. Format is YYYYMMDD_hhmmss.
     *                  If not provided or empty, do not consider timestamp to match filenames
     * @see #export(BenchmarkReport, Path, Path, String)
     */
    static void exportAll(List<BenchmarkReport> reports, Path markdownDirectoryOutputPath, Path baselineReportDirectoryInputPath, String timestamp) {
        List<FailedExport> failedExports = new ArrayList<>();
        for (BenchmarkReport report : reports) {
            try {
                if (!export(report, markdownDirectoryOutputPath, baselineReportDirectoryInputPath, timestamp)) {
                    failedExports.add(new FailedExport(report.benchmarkClass(), "No results exporter found"));
                }
            } catch (Exception e) {
                failedExports.add(new FailedExport(report.benchmarkClass(), e.getClass().getSimpleName() + ": " + e.getMessage()));
            }
        }
        if (!failedExports.isEmpty()) {
            throw new PowsyblException("Failed to export benchmark reports as markdown: " + failedExports.stream()
                .map(fe -> fe.benchmarkClass() + " (" + fe.reason() + ")")
                .collect(Collectors.joining(", ")));
        }
    }

    boolean isBenchmarkClassSupported(String benchmarkClass);

    Map<String, String> exportReport(BenchmarkReport report, BenchmarkReport baseline);
}
