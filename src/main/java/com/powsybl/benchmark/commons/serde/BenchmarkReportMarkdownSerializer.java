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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

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
            default -> null;
        };
    }

    public void serialize(BenchmarkReport report, Path filePath) throws IOException {
        AbstractBenchmarkReportMarkdownSerializer serializer = chooseSerializer(report.benchmarkClass());
        if (serializer == null) {
            LOGGER.error("No serializer found for benchmark class {}", report.benchmarkClass());
        } else {
            String serializedReport = serializer.reportToString(report);
            Files.writeString(filePath.resolve(report.benchmarkClass() + ".md"), serializedReport);
        }
    }

    public void serialize(Collection<BenchmarkReport> reports, Path filePath) throws IOException {
        for (BenchmarkReport report : reports) {
            serialize(report, filePath);
        }
    }
}
