/**
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.commons.serde.markdown;

import com.google.auto.service.AutoService;
import com.powsybl.benchmark.commons.serde.ResultsExporter;

import java.util.List;

/**
 * @author Nicolas Rol {@literal <nicolas.rol at rte-france.com>}
 */
@AutoService(ResultsExporter.class)
public class LimitScalingSerializationMarkdownResultsExporter extends AbstractMultipleOperationsMarkdownResultsExporter {

    private static final List<String> BENCHMARKS_CLASSES = List.of("LimitScalingSerializationBenchmark");

    public LimitScalingSerializationMarkdownResultsExporter() {
        super(BENCHMARKS_CLASSES);
    }

    @Override
    protected String getPrettyOperationName(String fullOperationName) {
        String shortName = fullOperationName.substring(fullOperationName.lastIndexOf('.') + 1);
        return switch (shortName) {
            case "limitScaling1Deserializing" -> "Deserializing";
            case "limitScaling2DeserializingFromBytes" -> "Deserializing from bytes";
            case "limitScaling3Writing" -> "Writing";
            case "limitScaling4BufferedWriting" -> "Buffered writing";
            default -> shortName;
        };
    }
}
