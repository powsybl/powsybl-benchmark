/**
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.commons.runcomparison;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.openjdk.jmh.results.RunResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Dissoubray Nathan {@literal <nathan.dissoubray at rte-france.com>}
 */
public final class BenchmarkResultSerDe {
    //used to be able to provide a default value in the Javadoc (cannot be done with a Path.of)
    public static final String BENCHMARK_PATH_STRING = "benchmark_results";
    private static final Path BENCHMARK_PATH = Path.of(BENCHMARK_PATH_STRING);
    private static final ObjectMapper MAPPER = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    private BenchmarkResultSerDe() {
        //no instancing on util class
    }

    /**
     * Write the results of all the provided benchmarks RunResult to the default directory path of {@value BENCHMARK_PATH_STRING}
     * in JSON format.
     * @param runResults all the <code>RunResult</code> to write
     * @throws IOException if the default directory path of {@value BENCHMARK_PATH_STRING} cannot be created
     */
    public static void writeAll(Collection<RunResult> runResults) throws IOException {
        writeAll(runResults, BENCHMARK_PATH);
    }

    public static void writeAll(Collection<RunResult> runResults, Path benchmarkOutputPath) throws IOException {
        List<BenchmarkReport> reports = BenchmarkReport.buildAllReports(runResults);
        writeReports(reports, benchmarkOutputPath);
    }

    /**
     * Writes the provided benchmark reports to the specified output directory in JSON format.
     * @param reports the list of benchmark reports to be written
     * @param benchmarkOutputPath the directory path where the benchmark reports will be written
     * @throws IOException IOException <ul>
     *     <li>if the directory of the provided path cannot be created</li>
     *     <li>if any file for a benchmark report cannot be created or written to.
     *     In that case, the exception is thrown only after having tried to write all the reports</li>
     * </ul>
     */
    public static void writeReports(List<BenchmarkReport> reports, Path benchmarkOutputPath) throws IOException {
        List<String> failedBenchmarkWrite = new ArrayList<>();
        //create directory and parents, does not throw if directory already exists
        Files.createDirectories(benchmarkOutputPath);
        for (BenchmarkReport report : reports) {
            String benchmarkClass = report.benchmarkClass();
            Path writePath = benchmarkOutputPath.resolve(benchmarkClass + ".json");
            try {
                MAPPER.writeValue(writePath.toFile(), report);
            } catch (IOException e) {
                failedBenchmarkWrite.add(benchmarkClass);
            }
        }
        if (!failedBenchmarkWrite.isEmpty()) {
            throw new IOException("Failed to write benchmark reports for classes: " + failedBenchmarkWrite);
        }
    }

    /**
     * Read the benchmark reports from the provided paths.
     * @param inputPaths the path of each file from which to read a benchmark report. Each file should correspond to a single benchmark report.
     * @return all the benchmark reports read from the provided paths
     * @throws IOException if any file for a benchmark report cannot be read.
     *     In that case, the exception is thrown only after having tried to read all the reports
     */
    public static List<BenchmarkReport> readReports(Path... inputPaths) throws IOException {
        List<BenchmarkReport> reports = new ArrayList<>();
        for (Path path : inputPaths) {
            try {
                BenchmarkReport report = MAPPER.readValue(path.toFile(), BenchmarkReport.class);
                reports.add(report);
            } catch (IOException e) {
                throw new IOException("Failed to read benchmark report from " + path, e);
            }
        }
        return reports;
    }
}
