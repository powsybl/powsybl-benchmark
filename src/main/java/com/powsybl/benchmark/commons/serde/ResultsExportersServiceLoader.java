/**
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.commons.serde;

import com.powsybl.commons.util.ServiceLoaderCache;

import java.util.List;

/**
 * @author Nicolas Rol {@literal <nicolas.rol at rte-france.com>}
 */
public class ResultsExportersServiceLoader implements ResultsExportersLoader {

    private static final ServiceLoaderCache<ResultsExporter> EXPORTER_LOADER = new ServiceLoaderCache<>(ResultsExporter.class);

    @Override
    public List<ResultsExporter> loadExporters() {
        return EXPORTER_LOADER.getServices();
    }
}
