/**
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.commons.serde;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.openjdk.jmh.results.RunResult;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static com.powsybl.benchmark.commons.serde.BenchmarkTestUtils.assertResultsEqual;
import static com.powsybl.benchmark.commons.serde.BenchmarkTestUtils.mockRunResult;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Dissoubray Nathan {@literal <nathan.dissoubray at rte-france.com>}
 */
class BenchmarkReportJsonSerDeTest {

    @TempDir
    Path tempDir;

    @Test
    void roundTripJsonSerDeTest() throws IOException {
        String className1 = "ClassA";
        RunResult rr1 = mockRunResult("com.powsybl." + className1 + ".method1");
        BenchmarkReport report1 = new BenchmarkReport(className1, List.of(rr1));

        String className2 = "ClassB";
        RunResult rr2 = mockRunResult("com.powsybl." + className2 + ".method2");
        RunResult rr3 = mockRunResult("com.powsybl." + className2 + ".method3");
        BenchmarkReport report2 = new BenchmarkReport(className2, List.of(rr2, rr3));

        List<BenchmarkReport> reports = List.of(report1, report2);
        BenchmarkReportJsonSerDe.writeReports(reports, tempDir);

        List<Path> reportPaths;
        try (Stream<Path> stream = Files.list(tempDir)) {
            reportPaths = stream.toList();
        }
        assertThat(reportPaths)
            .hasSize(2)
            .anyMatch(p -> p.getFileName().toString().startsWith(className1) && p.getFileName().toString().endsWith(".json"))
            .anyMatch(p -> p.getFileName().toString().startsWith(className2) && p.getFileName().toString().endsWith(".json"));

        Path path1 = reportPaths.stream().filter(p -> p.getFileName().toString().startsWith(className1)).findFirst().orElseThrow();
        Path path2 = reportPaths.stream().filter(p -> p.getFileName().toString().startsWith(className2)).findFirst().orElseThrow();

        List<BenchmarkReport> readReports = BenchmarkReportJsonSerDe.readReports(path1, path2);
        assertEquals(2, readReports.size());

        BenchmarkReport readReport1 = readReports.stream().filter(r -> r.benchmarkClass().equals(className1)).findFirst().orElseThrow();
        BenchmarkReport readReport2 = readReports.stream().filter(r -> r.benchmarkClass().equals(className2)).findFirst().orElseThrow();

        assertReportsEqual(report1, readReport1);
        assertReportsEqual(report2, readReport2);
    }

    private static void assertReportsEqual(BenchmarkReport expected, BenchmarkReport actual) {
        assertEquals(expected.benchmarkClass(), actual.benchmarkClass());
        assertEquals(expected.powsyblCoreVersion(), actual.powsyblCoreVersion());
        assertEquals(expected.openLoadFlowVersion(), actual.openLoadFlowVersion());
        assertEquals(expected.datetime(), actual.datetime());
        assertEquals(expected.results().size(), actual.results().size());
        for (int i = 0; i < expected.results().size(); i++) {
            assertResultsEqual(expected.results().get(i), actual.results().get(i));
        }
    }

    @Test
    void testWriteAll() throws IOException {
        RunResult rr1 = mockRunResult("com.powsybl.ClassA.method1");
        RunResult rr2 = mockRunResult("com.powsybl.ClassB.method2");

        BenchmarkReportJsonSerDe.writeAll(List.of(rr1, rr2), tempDir);

        List<Path> reportPaths;
        try (Stream<Path> stream = Files.list(tempDir)) {
            reportPaths = stream.toList();
        }
        assertThat(reportPaths)
            .hasSize(2)
            .anyMatch(p -> p.getFileName().toString().startsWith("ClassA_") && p.getFileName().toString().endsWith(".json"))
            .anyMatch(p -> p.getFileName().toString().startsWith("ClassB_") && p.getFileName().toString().endsWith(".json"));
    }

    @Test
    void testReadReportsFailure() {
        Path nonExistentPath = tempDir.resolve("nonExistent.json");
        assertThrows(IOException.class, () -> BenchmarkReportJsonSerDe.readReports(nonExistentPath));
    }

    @Test
    void testWriteReportsIOException() {
        // Create a file where a directory should be to cause an IOException
        Path fileAsDir = tempDir.resolve("fileAsDir");
        try {
            java.nio.file.Files.createFile(fileAsDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        BenchmarkReport report = new BenchmarkReport("SomeClass", List.of(mockRunResult("SomeClass.method")));

        // writeReports calls Files.createDirectories(benchmarkOutputPath)
        // which will throw FileAlreadyExistsException (subclass of IOException) if benchmarkOutputPath is a file
        assertThrows(IOException.class, () -> BenchmarkReportJsonSerDe.writeReports(List.of(report), fileAsDir));
    }
}
