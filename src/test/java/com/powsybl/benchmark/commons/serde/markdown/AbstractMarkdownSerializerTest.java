/**
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.commons.serde.markdown;

import com.powsybl.benchmark.commons.serde.*;
import org.junit.jupiter.api.Test;
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
            testReportToStringFullPath(getBenchClass(), getBenchFilenames().get(i), buildResults(), getResourcePaths().get(i), null);
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
                Paths.get(Objects.requireNonNull(getClass().getResource(getBaselineDirectoryPathString())).toURI()));
        }
    }

    protected void testReportToStringFullPath(String benchClass, String filenameStart, List<RunResult> runResults, String expectedResourcePath, Path baselineDirectoryPath) throws IOException {
        BenchmarkReport report = BenchmarkTestUtils.mockBenchmarkReport(benchClass, runResults);
        ResultsExporter.export(report, tempDir, baselineDirectoryPath, "");

        String actual = Files.readString(tempDir.resolve(SerDeUtil.getMatchingFileFromPath(
                    tempDir,
                    new SerDeFilePredicate(filenameStart, ".md", "")
                )),
                StandardCharsets.UTF_8)
            .replace("\r\n", "\n");

        String expected = new String(Objects.requireNonNull(getClass().getResourceAsStream(expectedResourcePath)).readAllBytes(), StandardCharsets.UTF_8)
            .replace("\r\n", "\n");

        assertEquals(expected, actual);
    }

    /**
     * Retrieves the name of the benchmark class associated with the test.
     *
     * @return A string representing the benchmark class name.
     */
    protected abstract String getBenchClass();

    /**
     * Retrieves a list of benchmark filenames that are the generated files.
     *
     * @return A list of strings representing the names of the benchmark files.
     */
    protected List<String> getBenchFilenames() {
        return List.of(getBenchClass());
    }

    /**
     * Retrieves a list of resource paths that are the expected results when no comparison to a baseline is done.
     *
     * @return A list of strings representing the relative paths to the resource files.
     */
    protected abstract List<String> getResourcePaths();

    /**
     * Retrieves a list of resource paths that are the expected results when a comparison to a baseline is done.
     *
     * @return A list of strings representing the relative paths to the resource files.
     */
    protected abstract List<String> getResourceWithBaselinePaths();

    /**
     * Retrieves the path to the baseline directory as a string.
     * This method is intended to provide the location of the directory
     * where baseline files are stored, in JSON format.
     *
     * @return A string representing the path to the baseline directory.
     */
    protected abstract String getBaselineDirectoryPathString();

    /**
     * Constructs a list of benchmark run results that can be used for generating reports.
     *
     * @return A list of {@link RunResult} objects representing the benchmark results.
     */
    protected abstract List<RunResult> buildResults();
}
