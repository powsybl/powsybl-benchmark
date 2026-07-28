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
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Dissoubray Nathan {@literal <nathan.dissoubray at rte-france.com>}
 */
public final class BenchmarkResultSerDe {
    private static final Path BENCHMARK_PATH = Path.of("benchmark_results");
    private static final ObjectMapper MAPPER = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    private BenchmarkResultSerDe() {
        //no instancing on util class
    }

    public static void writeAll(Collection<RunResult> runResults) {
        List<BenchmarkReport> reports = BenchmarkReport.buildAllReports(runResults);
        writeReports(reports);
    }

    //TODO test that this works
    //TODO add doc
    public static void writeReports(List<BenchmarkReport> reports) {
        List<String> failedBenchmarkWrite = new ArrayList<>();
        for (BenchmarkReport report : reports) {
            String benchmarkClass = report.benchmarkClass();
            Path writePath = BENCHMARK_PATH.resolve(benchmarkClass + ".json");
            try {
                MAPPER.writeValue(writePath.toFile(), report);
            } catch (IOException e) {
                failedBenchmarkWrite.add(benchmarkClass);
            }
        }
        if (!failedBenchmarkWrite.isEmpty()) {
            throw new IllegalStateException("Failed to write benchmark reports for classes: " + failedBenchmarkWrite);
        }
    }

    public static List<BenchmarkReport> readReports(Path... inputPaths) {
        List<BenchmarkReport> reports = new ArrayList<>();
        for (Path path : inputPaths) {
            try {
                BenchmarkReport report = MAPPER.readValue(path.toFile(), BenchmarkReport.class);
                reports.add(report);
            } catch (IOException e) {
                throw new IllegalStateException("Failed to read benchmark report from " + path, e);
            }
        }
        return reports;
    }
}
