/**
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.commons.serde.markdown.security;

import com.powsybl.benchmark.commons.serde.BenchmarkReport;
import com.powsybl.benchmark.commons.serde.BenchmarkResult;
import com.powsybl.benchmark.commons.serde.markdown.AbstractBenchmarkReportMarkdownSerializer;

import java.util.*;
import java.util.function.Function;

/**
 * @author Dissoubray Nathan {@literal <nathan.dissoubray at rte-france.com>}
 */
public class XmxSecurityAnalysisBenchmarkReportMarkdownSerializer extends AbstractBenchmarkReportMarkdownSerializer {

    private static final String[] XMX_VALUES = {
        "128M",
        "256M",
        "512M",
        "1G",
        "2G",
        "4G",
        "8G",
        "Undefined"
    };
    private static final String XMX_KEY = "XmX value";

    private int xmxIndex = 0;

    @Override
    protected String[] columnNames() {
        return MultiThreadUtil.columnNames(XMX_KEY);
    }

    @Override
    protected Map<String, String> getLine(List<BenchmarkResult> results) {
        Map<String, String> line = new HashMap<>();
        if (results == null || results.isEmpty()) {
            //this works because if a given XmX value fails for all thread number, then all XmX values that are lower will also fail for all thread numbers
            //meaning that if only 128M fails for all, xmxIndex will only increase once
            //if 3 lines of XmX values fail, then it will definitely be the 3 lowest XmX values,
            //and it doesn't matter in which order the lines are queried (because we return the same thing in all cases)
            line.put(XMX_KEY, XMX_VALUES[xmxIndex++]);
        } else {
            line = MultiThreadUtil.buildTableLine(
                results,
                XMX_KEY,
                r -> getLineSorter().apply(r.getFirst()));
        }
        for (String columnName : columnNames()) {
            line.putIfAbsent(columnName, "Failed");
        }
        return line;
    }

    @Override
    protected List<List<BenchmarkResult>> getResultsByTableLine(BenchmarkReport report) {
        LinkedHashMap<String, List<BenchmarkResult>> byLine = new LinkedHashMap<>();
        Function<BenchmarkResult, String> lineSorter = getLineSorter();
        for (String xmx : XMX_VALUES) {
            byLine.put(xmx, new ArrayList<>());
        }
        for (BenchmarkResult result : report.results()) {
            byLine.get(lineSorter.apply(result)).add(result);
        }
        return new ArrayList<>(byLine.values());
    }

    @Override
    protected Function<BenchmarkResult, String> getLineSorter() {
        //this assumes that all XmX benchmarks are named runXmxVALUE
        //with VALUE being one of XMX_VALUES
        return r -> r.benchmarkName().substring(r.benchmarkName().lastIndexOf('.') + 1).replace("runXmx", "");
    }
}
