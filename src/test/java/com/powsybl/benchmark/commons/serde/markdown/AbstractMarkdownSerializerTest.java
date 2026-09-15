/**
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.commons.serde.markdown;

import com.powsybl.benchmark.commons.serde.BenchmarkReport;
import com.powsybl.benchmark.commons.serde.BenchmarkTestUtils;
import com.powsybl.benchmark.commons.serde.ResultsExporter;
import org.junit.jupiter.api.io.TempDir;
import org.openjdk.jmh.results.RunResult;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Dissoubray Nathan {@literal <nathan.dissoubray at rte-france.com>}
 */
public abstract class AbstractMarkdownSerializerTest {

    @TempDir
    Path tempDir;

    @Test
    public void testReportToStringWithoutBaseline() throws IOException {
        for (int i = 0; i < getBenchFilenames().size(); ++i) {
            testReportToStringFullPath(getBenchClass(), getBenchFilenames().get(i), buildResults(), getResourcePaths().get(i));
        }
    }

    @Test
    void testReportToStringMissingBaselineFile() throws IOException {
        for (int i = 0; i < getBenchFilenames().size(); ++i) {
            testReportToStringFullPath(getBenchClass(), getBenchFilenames().get(i), buildResults(), getResourcePaths().get(i), Path.of("non-existent-path"));
        }
    }

    @Test
    public void testReportToStringWithBaseline() throws IOException, URISyntaxException {
        for (int i = 0; i < getBenchFilenames().size(); ++i) {
            testReportToStringFullPath(getBenchClass(), getBenchFilenames().get(i), buildResults(), getResourceWithBaselinePaths().get(i),
                Paths.get(getClass().getResource(getBaselineDirectoryPathString()).toURI()));
        }
    }

    private void testReportToStringFullPath(String benchClass, String generatedFileName, List<RunResult> runResults, String expectedResourcePath) throws IOException {
        BenchmarkReport report = BenchmarkTestUtils.mockBenchmarkReport(benchClass, runResults);
        ResultsExporter.export(report, tempDir);

        String actual = Files.readString(tempDir.resolve(generatedFileName), StandardCharsets.UTF_8)
            .replace("\r\n", "\n");

        String expected = new String(Objects.requireNonNull(getClass().getResourceAsStream(expectedResourcePath)).readAllBytes(), StandardCharsets.UTF_8)
            .replace("\r\n", "\n");

        assertEquals(expected, actual);
    }

    private void testReportToStringFullPath(String benchClass, String generatedFileName, List<RunResult> runResults, String expectedResourcePath, Path baselineDirectoryPath) throws IOException {
        BenchmarkReport report = BenchmarkTestUtils.mockBenchmarkReport(benchClass, runResults);
        BenchmarkReportMarkdownSerializer.serialize(report, tempDir, baselineDirectoryPath);

        String actual = Files.readString(tempDir.resolve(generatedFileName), StandardCharsets.UTF_8)
            .replace("\r\n", "\n");

        String expected = new String(Objects.requireNonNull(getClass().getResourceAsStream(expectedResourcePath)).readAllBytes(), StandardCharsets.UTF_8)
            .replace("\r\n", "\n");

        assertEquals(expected, actual);
    }

    protected abstract String getBenchClass();

    protected abstract List<String> getBenchFilenames();

    protected abstract List<String> getResourcePaths();

    protected abstract List<String> getResourceWithBaselinePaths();

    protected abstract String getBaselineDirectoryPathString();

    protected abstract List<RunResult> buildResults();
}
