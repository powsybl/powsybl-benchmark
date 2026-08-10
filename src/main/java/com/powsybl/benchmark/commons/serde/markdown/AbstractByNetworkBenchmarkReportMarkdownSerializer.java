/**
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.commons.serde.markdown;

import com.powsybl.benchmark.commons.serde.BenchmarkResult;

import java.util.function.Function;

/**
 * @author Dissoubray Nathan {@literal <nathan.dissoubray at rte-france.com>}
 */
public abstract class AbstractByNetworkBenchmarkReportMarkdownSerializer extends AbstractBenchmarkReportMarkdownSerializer {

    @Override
    protected Function<BenchmarkResult, String> getLineSorter() {
        return result -> result.parameters().get("networkName");
    }
}
