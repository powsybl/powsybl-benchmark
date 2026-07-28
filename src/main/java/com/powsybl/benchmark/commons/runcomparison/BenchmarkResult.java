/**
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.commons.runcomparison;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.infra.BenchmarkParams;
import org.openjdk.jmh.results.RunResult;

import java.util.Map;
import java.util.TreeMap;

/**
 * Result per benchmark. For a given set of parameters,
 * this is the aggregated score across all the runs for this set of parameters.
 * @author Dissoubray Nathan {@literal <nathan.dissoubray at rte-france.com>}
 */
public record BenchmarkResult(
    String benchmarkName,
    Map<String, String> parameters,
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    Mode mode,
    double score,
    double scoreError,
    String scoreUnit
) {
    public BenchmarkResult(RunResult result) {
        this(
            result.getParams().getBenchmark(),
            buildParamatersMap(result.getParams()),
            result.getParams().getMode(),
            result.getPrimaryResult().getScore(),
            result.getPrimaryResult().getScoreError(),
            result.getPrimaryResult().getScoreUnit()
        );
    }

    private static Map<String, String> buildParamatersMap(BenchmarkParams parameters) {
        Map<String, String> parametersMap = new TreeMap<>();
        for (String key : parameters.getParamsKeys()) {
            parametersMap.put(key, parameters.getParam(key));
        }
        return parametersMap;
    }
}
