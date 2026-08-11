/**
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.commons.serde.markdown;

import com.powsybl.benchmark.commons.serde.BenchmarkReport;
import com.powsybl.benchmark.commons.serde.BenchmarkReportMarkdownSerializer;
import com.powsybl.benchmark.commons.serde.BenchmarkTestUtils;
import org.junit.jupiter.api.io.TempDir;
import org.openjdk.jmh.results.RunResult;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Dissoubray Nathan {@literal <nathan.dissoubray at rte-france.com>}
 */
public abstract class AbstractMarkdownSerializerTest {

    @TempDir
    Path tempDir;

    protected void testReportToString(String benchClass, List<RunResult> runResults, String resourcePath) throws IOException {
        testReportToStringFullPath(benchClass, benchClass + ".md", runResults, resourcePath);
    }

    protected void testReportToStringFullPath(String benchClass, String generatedFileName, List<RunResult> runResults, String expectedResourcePath) throws IOException {
        BenchmarkReport report = BenchmarkTestUtils.mockBenchmarkReport(benchClass, runResults);
        BenchmarkReportMarkdownSerializer.serialize(report, tempDir);

        String actual = Files.readString(tempDir.resolve(generatedFileName), StandardCharsets.UTF_8)
            .replace("\r\n", "\n");

        String expected = new String(Objects.requireNonNull(getClass().getResourceAsStream(expectedResourcePath)).readAllBytes(), StandardCharsets.UTF_8)
            .replace("\r\n", "\n");

        assertEquals(expected, actual);
    }
}
