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
import org.openjdk.jmh.results.RunResult;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Dissoubray Nathan {@literal <nathan.dissoubray at rte-france.com>}
 */
public abstract class AbstractMarkdownSerializerTest {

    protected void testReportToString(String benchClass, List<RunResult> runResults, AbstractBenchmarkReportMarkdownSerializer serializer, String resourcePath) throws IOException {
        BenchmarkReport report = BenchmarkTestUtils.mockBenchmarkReport(benchClass, runResults);
        String actual = serializer.reportToString(report);

        String expected = new String(Objects.requireNonNull(getClass().getResourceAsStream(resourcePath)).readAllBytes(), StandardCharsets.UTF_8)
            .replace("\r\n", "\n");

        assertEquals(expected, actual.replace("\r\n", "\n"));
    }
}
