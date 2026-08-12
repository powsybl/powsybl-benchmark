/**
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.commons.serde;

import com.powsybl.benchmark.commons.serde.markdown.AbstractBenchmarkReportMarkdownSerializer;
import com.powsybl.benchmark.commons.serde.markdown.ContingenciesBenchmarkReportMarkdownSerializer;
import com.powsybl.benchmark.commons.serde.markdown.loadflow.LoadFlowBenchmarkReportMarkdownSerializer;
import com.powsybl.benchmark.commons.serde.markdown.security.MultiThreadSecurityAnalysisBenchmarkReportMarkdownSerializer;
import com.powsybl.benchmark.commons.serde.markdown.serialization.ContingencySerializationBenchmarkReportMarkdownSerializer;
import com.powsybl.benchmark.commons.serde.markdown.serialization.NetworkSerializationBenchmarkReportMarkdownSerializer;
import org.openjdk.jmh.results.RunResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;

/**
 * @author Dissoubray Nathan {@literal <nathan.dissoubray at rte-france.com>}
 */
public final class BenchmarkReportMarkdownSerializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(BenchmarkReportMarkdownSerializer.class);
    //TODO change the naming, too close to AbstractBenchmarkReportMarkdownSerializer (or change that class name)

    private BenchmarkReportMarkdownSerializer() {
        //no constructor for util class
    }

    private static AbstractBenchmarkReportMarkdownSerializer chooseSerializer(String className) {
        return switch (className) {
            case "LoadFlowBenchmark" -> new LoadFlowBenchmarkReportMarkdownSerializer();
            case "MonoThreadSecurityAnalysisBenchmark", "SensitivityAnalysisBenchmark" -> new ContingenciesBenchmarkReportMarkdownSerializer();
            case "MultiThreadSecurityAnalysisBenchmark" -> new MultiThreadSecurityAnalysisBenchmarkReportMarkdownSerializer();
            case "NetworkSerializationBenchmark" -> new NetworkSerializationBenchmarkReportMarkdownSerializer();
            case "ContingencySerializationBenchmark" -> new ContingencySerializationBenchmarkReportMarkdownSerializer();
            default -> null;
        };
    }

    /**
     * Serialize a benchmark report into one or more tables, and write them to one or more Markdown file.
     * @param report the benchmark report
     * @param filePath path to the directory where the Markdown files will be written.
     *                 If multiple tables are generated, the path to each table will be <code>filePath/benchmarkName_tableName.md</code>
     * @throws IOException if the file cannot be written (path does not exist, permission denied, etc.)
     */
    public static void serialize(BenchmarkReport report, Path filePath) throws IOException {
        AbstractBenchmarkReportMarkdownSerializer serializer = chooseSerializer(report.benchmarkClass());
        if (serializer == null) {
            LOGGER.warn("No serializer found for benchmark class {} : skipping markdown serialization", report.benchmarkClass());
        } else {
            Map<String, String> serializedReports = serializer.reportToStrings(report);
            for (Map.Entry<String, String> table : serializedReports.entrySet()) {
                String fileName = String.format("%s%s%s.md",
                    report.benchmarkClass(),
                    table.getKey().isEmpty() ? "" : "_",
                    table.getKey());
                Files.writeString(
                    filePath.resolve(fileName),
                    table.getValue()
                );
            }
        }
    }

    /**
     * Group all {@link RunResult} into reports, then serialize them in tables written to markdown files.
     * @param results all the run results to serialize
     * @param filePath path to the directory where the Markdown files will be written
     * @throws IOException if any file cannot be written (path does not exist, permission denied, etc.)
     * @see BenchmarkReportMarkdownSerializer#serialize(BenchmarkReport, Path)
     */
    public static void serialize(Collection<RunResult> results, Path filePath) throws IOException {
        for (BenchmarkReport report : BenchmarkReport.buildAllReports(results)) {
            serialize(report, filePath);
        }
    }
}
