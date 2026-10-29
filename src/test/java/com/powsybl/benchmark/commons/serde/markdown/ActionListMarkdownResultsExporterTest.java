/**
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.commons.serde.markdown;

import org.openjdk.jmh.results.RunResult;

import java.util.List;
import java.util.Map;

import static com.powsybl.benchmark.commons.serde.BenchmarkTestUtils.mockRunResult;

/**
 * @author Nicolas Rol {@literal <nicolas.rol at rte-france.com>}
 */
public class ActionListMarkdownResultsExporterTest extends AbstractMarkdownSerializerTest {

    @Override
    protected String getBenchClass() {
        return "ActionListSerializationBenchmark";
    }

    @Override
    protected List<String> getResourcePaths() {
        return List.of("/action-list-serialization-report.md");
    }

    @Override
    protected List<String> getResourceWithBaselinePaths() {
        return List.of("/action-list-serialization-report-with-baseline.md");
    }

    @Override
    protected List<RunResult> buildResults() {
        String benchName = getBenchClass() + ".";
        return List.of(
            mockRunResult(benchName + "actionList1Deserializing", Map.of(), 1.0),
            mockRunResult(benchName + "actionList2DeserializingFromBytes", Map.of(), 2.0),
            mockRunResult(benchName + "actionList3Writing", Map.of(), 3.0),
            mockRunResult(benchName + "actionList4BufferedWriting", Map.of(), 4.0)
        );
    }

}
